package org.tlsp.mc.wrapper.network;

import io.netty.buffer.ByteBuf;
import org.tlsp.mc.wrapper.ReflectWrapper;
import org.tlsp.mc.wrapper.TLSPAgentWrapper;

public class PacketBufferWrapper extends ReflectWrapper {
    private static String CLASS_NAME = "net.minecraft.network.PacketBuffer";

    public PacketBufferWrapper(Object instance) {
        super(CLASS_NAME, TLSPAgentWrapper.Instance.getLaunchLoader());
        if (!CLASS_NAME.equals(instance.getClass().getName())){
            throw new IllegalArgumentException("传入了错误的对象,无法初始化实例");
        }
        this.instance = instance;
    }

    public PacketBufferWrapper(ByteBuf wrapped, boolean placeParam) {
        super(CLASS_NAME,TLSPAgentWrapper.Instance.getLaunchLoader());
        tryNewInstance(wrapped);
    }

    public byte[] array() {
        return (byte[]) callByMethodName("array");
    }
}
