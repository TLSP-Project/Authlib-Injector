package org.tlsp.mc.wrapper.mac;

import org.tlsp.mc.wrapper.ReflectWrapper;
import org.tlsp.mc.wrapper.TLSPAgentWrapper;

public class MargeleAntiCheatWrapper extends ReflectWrapper {
    private static final String CLASS_NAME = "cn.margele.netease.clientside.MargeleAntiCheat";
    private static MargeleAntiCheatWrapper Instance = new MargeleAntiCheatWrapper();

    public MargeleAntiCheatWrapper() {
        super(CLASS_NAME, TLSPAgentWrapper.Instance.getLaunchLoader());
        instance = getMACInstance();
    }

    public Object getMACInstance(){
        try {
            return getStaticFieldByName("INSTANCE");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Packet<?>
    public boolean processPacket(Object packet, boolean isRead){
        return (boolean) callByMethodName("processPacket",packet,isRead);
    }
}
