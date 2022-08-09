package org.tlsp.mc.wrapper.notch;

import org.tlsp.mc.wrapper.TLSPAgentWrapper;

public class ClassMapping {
    public static String NetworkManager;
    public static String NetworkManager_SRG;
    public static String Packet;
    public static String Packet_SRG;
    public static String C01PacketEncryptionResponse_SRG;
    public static String S01PacketEncryptionRequest_SRG;

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
        Packet = "ff";
        Packet_SRG = "net.minecraft.network.Packet";
        NetworkManager = "ek";
        NetworkManager_SRG = "net.minecraft.network.NetworkManager";
        C01PacketEncryptionResponse_SRG = "net.minecraft.network.login.client.C01PacketEncryptionResponse";
        S01PacketEncryptionRequest_SRG = "net.minecraft.network.login.server.S01PacketEncryptionRequest";
    }
}
