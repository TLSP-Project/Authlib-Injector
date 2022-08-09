package org.tlsp.mc.wrapper.network;

import io.netty.channel.Channel;

import org.tlsp.mc.wrapper.ReflectWrapper;
import org.tlsp.mc.wrapper.TLSPAgentWrapper;

public class NetworkManagerWrapper extends ReflectWrapper {
    private static String CLASS_NAME = "net.minecraft.network.login.client.C01PacketEncryptionResponse";

    public NetworkManagerWrapper(Object instance) {
        super(CLASS_NAME, TLSPAgentWrapper.Instance.getLaunchLoader());
        this.instance = instance;
    }

    //EnumPacketDirection
    public NetworkManagerWrapper(Object packetDirection,boolean placeParam) {
        super(CLASS_NAME,TLSPAgentWrapper.Instance.getLaunchLoader());
        newInstance(packetDirection);
    }

    public Channel getChannel(){
        try {
            return (Channel) getFieldByName("channel");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
