package org.tlsp.mc.wrapper.mac;

import com.mojang.authlib.GameProfile;
import org.tlsp.mc.wrapper.ReflectWrapper;
import org.tlsp.mc.wrapper.TLSPAgentWrapper;

public class ProtectedMixinMethodsWrapper extends ReflectWrapper {
    private static final String CLASS_NAME = "cn.margele.netease.clientside.utils.ProtectedMixinMethods";

    public ProtectedMixinMethodsWrapper() throws ClassNotFoundException, IllegalAccessException {
        super(CLASS_NAME, TLSPAgentWrapper.Instance.getLaunchLoader());
        instance = getStaticFieldByName("INSTANCE");
    }

    public ProtectedMixinMethodsWrapper(ClassLoader loader) throws ClassNotFoundException, IllegalAccessException {
        super(CLASS_NAME,loader);
        instance = getStaticFieldByName("INSTANCE");
    }

    public void joinServer(GameProfile gameProfile, String accessToken, String serverId, MixinCallbackInfoWrapper callbackInfo){
        callByMethodName("joinServer",gameProfile,accessToken,serverId,callbackInfo.getInstance());
    }
}
