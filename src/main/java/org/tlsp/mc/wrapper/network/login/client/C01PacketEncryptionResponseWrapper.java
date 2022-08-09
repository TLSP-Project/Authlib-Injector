package org.tlsp.mc.wrapper.network.login.client;

import org.tlsp.mc.wrapper.ReflectWrapper;
import org.tlsp.mc.wrapper.TLSPAgentWrapper;
import org.tlsp.mc.wrapper.notch.ClassMapping;
import org.tlsp.mc.wrapper.notch.FuncMapping;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class C01PacketEncryptionResponseWrapper extends ReflectWrapper {
    private static String CLASS_NAME = ClassMapping.C01PacketEncryptionResponse_SRG; //= "net.minecraft.network.login.client.C01PacketEncryptionResponse";

    public C01PacketEncryptionResponseWrapper() {
        super(CLASS_NAME, TLSPAgentWrapper.Instance.getLaunchLoader());
        newInstance();
    }

    public C01PacketEncryptionResponseWrapper(Object instance) {
        super(CLASS_NAME, TLSPAgentWrapper.Instance.getLaunchLoader());
        if (!CLASS_NAME.equals(instance.getClass().getName())){
            throw new IllegalArgumentException("传入了错误的对象,无法初始化实例");
        }
        this.instance = instance;
    }

    public C01PacketEncryptionResponseWrapper(SecretKey secretKey, PublicKey publicKey, byte[] verifyToken) {
        super(CLASS_NAME,TLSPAgentWrapper.Instance.getLaunchLoader());
        tryNewInstance(secretKey,publicKey,verifyToken);
    }

    //PacketBuffer
    public void readPacketData(Object buf) throws IOException {
        System.err.println(buf + "\t" + buf.getClass().getName());
        callByMethodName(FuncMapping.C01_readPacketData_SRG,buf);
    }

    //PacketBuffer
    public void writePacketData(Object buf) throws IOException {
        callByMethodName(FuncMapping.C01_writePacketData_SRG,buf);
    }

    //INetHandlerLoginClient
    public void processPacket(Object handler) {
        callByMethodName(FuncMapping.C01_processPacket_SRG,getInstance());
    }

    public SecretKey getSecretKey(PrivateKey key) {
        return (SecretKey) callByMethodName(FuncMapping.C01_getSecretKey_SRG,key);
    }

    public byte[] getVerifyToken(PrivateKey key) {
        return (byte[]) callByMethodName(FuncMapping.C01_getVerifyToken_SRG,key);
    }
}
