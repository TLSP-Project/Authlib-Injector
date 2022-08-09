package org.tlsp.mc.handle;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledByteBufAllocator;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.tlsp.mc.utils.NetworkUtils;
import org.tlsp.mc.wrapper.TLSPAgentWrapper;
import org.tlsp.mc.wrapper.network.NetworkManagerWrapper;
import org.tlsp.mc.wrapper.network.PacketBufferWrapper;
import org.tlsp.mc.wrapper.network.login.client.C01PacketEncryptionResponseWrapper;
import org.tlsp.mc.wrapper.network.login.server.S01PacketEncryptionRequestWrapper;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class NetworkManagerClientHandle {
    private static SecretKey L_SecretKey;
    private static PublicKey L_PublicKey;
    private static byte[] L_VerifyToken;
    private static String L_ServerId;

    /**
     * 设置秘钥
     * @param networkManager NetworkManager
     * @param secretKey
     * @return
     */
    public static SecretKey setSecretKey(Object networkManager, SecretKey secretKey){
        try{
            //获取密钥
            String onlineKeyBase64 = NetworkUtils.getTGlobalValue("secretKey");
            if (StringUtils.isNotBlank(onlineKeyBase64)){
                byte[] buffer = Base64.getDecoder().decode(onlineKeyBase64);
                SecretKey key = SerializationUtils.deserialize(buffer);
                NetworkUtils.putTGlobalValue("netState","SecretKeyOK");
                return key;
            }else{
                NetworkUtils.putTGlobalValue("netState","SecretKeyEmpty");
            }
        }catch (Exception e){
            NetworkUtils.putTGlobalValue("netState","SecretKeyError");
            System.err.println("setSecretKey error");
            System.err.println(e);
            e.printStackTrace();
        }
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

                NetworkUtils.putTGlobalValue("c01Base64","");
                NetworkUtils.putTGlobalValue("netState","NeedEncrypt,CSend");

                //等待处理过的C01包的buffer
                String c01Packet = NetworkUtils.tryGetTGlobalValue("c01Base64",15);

                try{
                    System.err.println("catch c01Packet " + c01Packet);
                    //接收MAC Server端传来的pakcetBuffer
                    C01PacketEncryptionResponseWrapper C01Wrapper = new C01PacketEncryptionResponseWrapper(packet);
                    ByteBuf newBuffer = Unpooled.wrappedBuffer(Base64.getDecoder().decode(c01Packet));
                    PacketBufferWrapper packetBufferWrapper = new PacketBufferWrapper(newBuffer,true);
                    //读取缓冲区数据
                    C01Wrapper.readPacketData(packetBufferWrapper.getInstance());
                    NetworkUtils.putTGlobalValue("netState","C01,COK");
                }catch (Exception ex){
                    throw ex;
                }
            }else{
                System.err.println("catch other send packet " + className);
            }
        }catch (Exception e){
            System.err.println("ERR sendPacket"+e.getMessage());
            System.err.println(e);
            e.printStackTrace();
            NetworkUtils.putTGlobalValue("netState","Error,CSend," + e);
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

        //抓到丢失连接包时执行重连
        //catch recPacket :net.minecraft.network.login.server.S00PacketDisconnect

        try {
            if (className.contains("S01PacketEncryptionRequest") || className.contains("SPacketEncryptionRequest")) {
                System.err.println("catch S01_RES");

                NetworkUtils.putTGlobalValue("s01Base64","");
                NetworkUtils.putTGlobalValue("netState","NeedEncrypt,CRecv");
                
                S01PacketEncryptionRequestWrapper s01PacketEncryptionRequestWrapper = new S01PacketEncryptionRequestWrapper(packet);

                String serverId = s01PacketEncryptionRequestWrapper.getServerId();
                String publicKeyBase64 = Base64.getEncoder().encodeToString(SerializationUtils.serialize(s01PacketEncryptionRequestWrapper.getPublicKey()));
                String verifyTokenBase64 = Base64.getEncoder().encodeToString(s01PacketEncryptionRequestWrapper.getVerifyToken());

                //提交认证请求凭证信息
                NetworkUtils.putTGlobalValue("serverId",serverId);
                NetworkUtils.putTGlobalValue("publicKey",publicKeyBase64);
                NetworkUtils.putTGlobalValue("verifyToken",verifyTokenBase64);
            }
        }catch (Exception e){
            System.err.println("ERR recPacket"+e.getMessage());
            e.printStackTrace();
        }

        return packet;
    }
}
