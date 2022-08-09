package org.tlsp.mc.wrapper.mac;

import org.tlsp.mc.wrapper.ReflectWrapper;
import org.tlsp.mc.wrapper.TLSPAgentWrapper;

import java.util.concurrent.CancellationException;

/**
 * Mixin CallbackInfo 反射处理类
 */
public class MixinCallbackInfoWrapper extends ReflectWrapper {
    private static final String CLASS_NAME = "org.spongepowered.asm.mixin.injection.callback.CallbackInfo";

    private String name;

    public MixinCallbackInfoWrapper(String name,boolean cancellable) throws ClassNotFoundException, IllegalAccessException {
        super(CLASS_NAME, TLSPAgentWrapper.Instance.getLaunchLoader());
        //创建目标对象
        newInstance(name,cancellable);
        this.name = name;
    }

    public MixinCallbackInfoWrapper(ClassLoader loader,String name,boolean cancellable) throws ClassNotFoundException, IllegalAccessException {
        super(CLASS_NAME,loader);
        //创建目标对象
        newInstance(name,cancellable);
        this.name = name;
    }

    public String getId() {
        return this.name;
    }

    public String toString() {
        try {
            return String.format("CallbackInfo[TYPE=%s,NAME=%s,CANCELLABLE=%s]", new Object[] { getClass().getSimpleName(), this.name, Boolean.valueOf(isCancellable()) });
        } catch (IllegalAccessException e) {
            return String.format("CallbackInfo[TYPE=%s,NAME=%s,CANCELLABLE=%s]", new Object[] { getClass().getSimpleName(), this.name, Boolean.valueOf(false) });
        }
    }

    public final boolean isCancellable() throws IllegalAccessException {
        return (boolean) getFieldByName("cancellable");
    }

    public final boolean isCancelled() throws IllegalAccessException {
        return (boolean) getFieldByName("cancelled");
    }

    public void cancel() throws CancellationException, IllegalAccessException {
        if (!this.isCancelled())
            throw new CancellationException(String.format("The call %s is not cancellable.", new Object[] { this.name }));
        setFieldByName("cancelled",true);
    }

}
