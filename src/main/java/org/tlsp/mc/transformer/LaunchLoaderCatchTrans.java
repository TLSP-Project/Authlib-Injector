package org.tlsp.mc.transformer;

import org.tlsp.mc.AgentMain;
import org.tlsp.mc.wrapper.forge.plugin.CoreModManagerWrapper;
import org.tlsp.mc.wrapper.forge.plugin.FMLPluginWrapper;
import org.tlsp.mc.wrapper.launch.LaunchClassLoaderWrapper;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class LaunchLoaderCatchTrans implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (loader == null){
            return classfileBuffer;
        }

        String loaderName = loader.getClass().getName();
        if (AgentMain.LaunchLoader == null && "net.minecraft.launchwrapper.LaunchClassLoader".equals(loaderName)){
            System.err.println("find LaunchClassLoader," + loader);

            AgentMain.LaunchLoader = loader;
            LaunchClassLoaderWrapper wrapper = new LaunchClassLoaderWrapper(loader);

            //初始化Forge插件
            try {
                System.err.println("register forge plugin -> NetworkManager");
                Class ClientTransPluginCls = Class.forName("org.tlsp.mc.plugin.MinecraftClientTransPlugin",true,loader);
                Object coreModInstance = ClientTransPluginCls.newInstance();
                CoreModManagerWrapper coreModManagerWrapper = new CoreModManagerWrapper();
                FMLPluginWrapper fmlPluginWrapper = new FMLPluginWrapper(ClientTransPluginCls.getName(),coreModInstance, null,0);
                //开启线程 监听CoreModList动向
                new Thread(()->{
                    Object instance = fmlPluginWrapper.getInstance();
                    //判断是否加载成功
                    while(!coreModManagerWrapper.addLoadPlugin(instance)){
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.err.println("NetworkManager ===> CoreMod Injecto Success");
                }).start();
            } catch (Exception e) {
                System.err.println("Create ClientTransPluginCls Failed ," + e);
                e.printStackTrace();
                throw new RuntimeException(e);
            }

            //System.err.println("register forge transformer -> NetworkManager");
            //劫持NetwaorkManager 进服验证密钥-注册至forge ClassLoader中
            //wrapper.registerTransformer("org.tlsp.mc.transformer.mc.network.NetworkManagerTrans");
        }

        return classfileBuffer;
    }
}
