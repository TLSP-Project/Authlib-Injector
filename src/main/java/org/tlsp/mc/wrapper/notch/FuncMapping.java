package org.tlsp.mc.wrapper.notch;

import org.tlsp.mc.wrapper.TLSPAgentWrapper;

public class FuncMapping {
    public static String enableEncryption;
    public static String enableEncryption_SRG;
    public static String enableEncryptionDesc;
    public static String enableEncryptionDesc_SRG;
    public static String channelRead0;
    public static String channelRead0_SRG;
    public static String channelRead0Desc;
    public static String channelRead0Desc_SRG;
    public static String sendPacket;
    public static String sendPacket_SRG;
    public static String[] sendPacketDesc;
    public static String[] sendPacketDesc_SRG;

    public static String C01_readPacketData_SRG;
    public static String C01_writePacketData_SRG;
    public static String C01_processPacket_SRG;
    public static String C01_getSecretKey_SRG;
    public static String C01_getVerifyToken_SRG;

    public static String S01_readPacketData_SRG;
    public static String S01_writePacketData_SRG;
    public static String S01_processPacket_SRG;
    public static String S01_getVerifyToken_SRG;
    public static String S01_getServerId_SRG;
    public static String S01_getPublicKey_SRG;

    static {
        switch (TLSPAgentWrapper.Instance.getMCVersion()){
            case "1.8.9":
                Init1_8_9();
                break;
            default:
                break;
        }
    }

    public static void Init1_8_9(){
        enableEncryption = "a";
        enableEncryption_SRG = "func_150727_a";
        enableEncryptionDesc = String.format("(Ljavax/crypto/SecretKey;)V");
        enableEncryptionDesc_SRG = enableEncryptionDesc;
        channelRead0 = "a";
        channelRead0_SRG = "channelRead0";
        channelRead0Desc = String.format("(Lio/netty/channel/ChannelHandlerContext;L%s;)V",ClassMapping.Packet);
        channelRead0Desc_SRG = String.format("(Lio/netty/channel/ChannelHandlerContext;L%s;)V",ClassMapping.Packet_SRG);
        sendPacket = "a";
        sendPacket_SRG = "func_179290_a";
        sendPacketDesc = new String[]{
                String.format("(L%s;)V",ClassMapping.Packet),
                String.format("(L%s;Lio/netty/util/concurrent/GenericFutureListener;[Lio/netty/util/concurrent/GenericFutureListener;)V",ClassMapping.Packet),
        };
        sendPacketDesc_SRG = new String[]{
                String.format("(L%s;)V",ClassMapping.Packet_SRG),
                String.format("(L%s;Lio/netty/util/concurrent/GenericFutureListener;[Lio/netty/util/concurrent/GenericFutureListener;)V",ClassMapping.Packet_SRG),
        };

        C01_readPacketData_SRG = "func_148837_a";
        C01_writePacketData_SRG = "func_148840_b";
        C01_processPacket_SRG = "func_148833_a";
        C01_getSecretKey_SRG = "func_149300_a";
        C01_getVerifyToken_SRG = "func_149299_b";

        S01_readPacketData_SRG = "func_148837_a";
        S01_writePacketData_SRG = "func_148840_b";
        S01_processPacket_SRG = "func_148833_a";
        S01_getVerifyToken_SRG = "func_149607_e";
        S01_getServerId_SRG = "func_149609_c";
        S01_getPublicKey_SRG = "func_149608_d";
    }
}
