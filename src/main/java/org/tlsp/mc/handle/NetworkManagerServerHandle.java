package org.tlsp.mc.handle;

import com.mojang.authlib.GameProfile;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;

import org.tlsp.mc.utils.NetworkUtils;
import org.tlsp.mc.wrapper.mac.MargeleAntiCheatWrapper;
import org.tlsp.mc.wrapper.network.NetworkManagerWrapper;
import org.tlsp.mc.wrapper.network.PacketBufferWrapper;
import org.tlsp.mc.wrapper.network.login.client.C01PacketEncryptionResponseWrapper;
import org.tlsp.mc.wrapper.network.login.server.S01PacketEncryptionRequestWrapper;

import javax.crypto.SecretKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.UUID;

public class NetworkManagerServerHandle {
    private static Thread ClientAuthPackHandle;

    static {
        ClientAuthPackHandle = new Thread(()->{
            while (true){
                try {
                    String netState = NetworkUtils.getTGlobalValue("netState");
                    if (!netState.startsWith("NeedEncrypt")){
                        continue;
                    }

                    if(netState.endsWith("Join")){
                        //开启重连

                    }
                }catch (Exception e){
                    System.err.println("Handle Network error," + e.getMessage());
                    System.err.println(e);
                    e.printStackTrace();
                }finally {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        ClientAuthPackHandle.setDaemon(true);
        //ClientAuthPackHandle.start();
    }

    /**
     * 设置秘钥
     * @param networkManager NetworkManager
     * @param secretKey
     * @return
     */
    public static SecretKey setSecretKey(Object networkManager, SecretKey secretKey){
        try{
            byte[] buffer = SerializationUtils.serialize(secretKey);
            String onlineKeyBase64 = Base64.getEncoder().encodeToString(buffer);
            System.err.println("catch secretkey " + onlineKeyBase64);
            //提交密钥
            NetworkUtils.putTGlobalValue("secretKey",onlineKeyBase64);
        }catch (Exception e){
            System.err.println("setSecretKey error");
            System.err.println(e);
            e.printStackTrace();
        }

        /*NetworkManagerWrapper networkManagerWrapper = new NetworkManagerWrapper(networkManager);
        Channel channel = networkManagerWrapper.getChannel();
        if (channel.isOpen()){
            channel.close();
        }*/

        return secretKey;
    }

    /**
     * 发送数据包
     * @param packet Packet
     * @return
     */
    public static Object sendPacket(Object packet) {
        String className = packet.getClass().getName();

        try {
            if (className.contains("C01PacketEncryptionResponse") || className.contains("CPacketEncryptionResponse")) {
                System.err.println("catch C01_RES");

                //等待Client进入Send状态
                NetworkUtils.tryGetTGlobalEqulasValue("netState","NeedEncrypt,CSend",15);

                //构造第三方客户端S01请求包
                NetworkUtils.putTGlobalValue("netState","C01,SRead");
                System.err.println("[Mac Server]C01,Read");
                C01PacketEncryptionResponseWrapper c01Wrapper = new C01PacketEncryptionResponseWrapper(packet);
                //读取pakcet buffer
                ByteBuf newBuffer = UnpooledByteBufAllocator.DEFAULT.heapBuffer();
                PacketBufferWrapper packetBufferWrapper = new PacketBufferWrapper(newBuffer,true);
                //将数据写入至缓冲区
                c01Wrapper.writePacketData(packetBufferWrapper.getInstance());
                //提交数据
                String packetBase64 = Base64.getEncoder().encodeToString(packetBufferWrapper.array());

                NetworkUtils.putTGlobalValue("c01Base64",packetBase64);
                NetworkUtils.putTGlobalValue("netState","C01,SOK");
                System.err.println("[Mac Server]C01,OK");

                //等待Client发送数据
                NetworkUtils.tryGetTGlobalEqulasValue("netState","C01,COK",15);
            }
        }catch (Exception e){
            System.err.println("ERR sendPacket"+e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return packet;
    }

    /**
     * 接收数据包
     * @param packet Packet
     * @return
     */
    public static Object recPacket(Object packet) {
        String className = packet.getClass().getName();

        try {
            if (className.contains("S01PacketEncryptionRequest") || className.contains("SPacketEncryptionRequest")) {
                System.err.println("catch S01_RES");

                String serverId = NetworkUtils.getTGlobalValue("serverId");
                String publicKeyBase64 = NetworkUtils.getTGlobalValue("publicKey");
                String verifyTokenBase64 = NetworkUtils.getTGlobalValue("verifyToken");

                //替换为第三方客户端S01请求包
                if (StringUtils.isNotBlank(serverId) && StringUtils.isNotBlank(publicKeyBase64) && StringUtils.isNotBlank(verifyTokenBase64)){
                    PublicKey publicKey = SerializationUtils.deserialize(Base64.getDecoder().decode(publicKeyBase64));
                    byte[] verifyToken = Base64.getDecoder().decode(verifyTokenBase64);
                    packet = new S01PacketEncryptionRequestWrapper(serverId,publicKey,verifyToken).getInstance();
                    NetworkUtils.putTGlobalValue("netState","S01RecvOK");
                    NetworkUtils.putTGlobalValue("publicKey",null);

                    NetworkUtils.putTGlobalValue("netState","S01,SOK");
                }else{
                    System.err.println("S01 ClientPacket is Empty");
                    throw new RuntimeException("已禁止白端进服,请使用第三方客户端进入游戏服务器!");
                }
            }
        }catch (Exception e){
            System.err.println("ERR recPacket"+e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return packet;
    }
}
