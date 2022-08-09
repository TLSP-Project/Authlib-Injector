package org.tlsp.mc.wrapper.network.login.server;

import org.tlsp.mc.wrapper.ReflectWrapper;
import org.tlsp.mc.wrapper.TLSPAgentWrapper;
import org.tlsp.mc.wrapper.notch.ClassMapping;
import org.tlsp.mc.wrapper.notch.FuncMapping;

import java.io.IOException;
import java.security.PublicKey;

public class S01PacketEncryptionRequestWrapper extends ReflectWrapper {
    private static String CLASS_NAME = ClassMapping.S01PacketEncryptionRequest_SRG;

    public S01PacketEncryptionRequestWrapper() {
        super(CLASS_NAME, TLSPAgentWrapper.Instance.getLaunchLoader());
        newInstance();
    }

    public S01PacketEncryptionRequestWrapper(Object instance) {
        super(CLASS_NAME, TLSPAgentWrapper.Instance.getLaunchLoader());
        if (!CLASS_NAME.equals(instance.getClass().getName())){
            throw new IllegalArgumentException("传入了错误的对象,无法初始化实例");
        }
        this.instance = instance;
    }

    public S01PacketEncryptionRequestWrapper(String serverId, PublicKey key, byte[] verifyToken) {
        super(CLASS_NAME,TLSPAgentWrapper.Instance.getLaunchLoader());
        tryNewInstance(serverId,key,verifyToken);
    }

    //PacketBuffer
    public void readPacketData(Object buf) throws IOException {
        callByMethodName(FuncMapping.S01_readPacketData_SRG,buf);
    }

    //PacketBuffer
    public void writePacketData(Object buf) throws IOException {
        callByMethodName(FuncMapping.S01_writePacketData_SRG,buf);
    }

    //INetHandlerLoginClient
    public void processPacket(Object handler) {
        callByMethodName(FuncMapping.S01_processPacket_SRG,getInstance());
    }

    public String getServerId() {
        return (String) callByMethodName(FuncMapping.S01_getServerId_SRG);
    }

    public PublicKey getPublicKey() {
        return (PublicKey) callByMethodName(FuncMapping.S01_getPublicKey_SRG);
    }

    public byte[] getVerifyToken() {
        return (byte[]) callByMethodName(FuncMapping.S01_getVerifyToken_SRG);
    }
}
