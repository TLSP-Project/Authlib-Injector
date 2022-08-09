package org.tlsp.mc.transformer.mac;


import org.tlsp.mc.AgentMain;
import org.tlsp.mc.utils.ThreadUtils;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

public class MACTrans implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        switch (className){
            case "cn/margele/netease/clientside/MargeleAntiCheat":
                return doMACInit(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
            default:
                return classfileBuffer;
        }
    }

    private byte[] doMACInit(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        AgentMain.IsLoadMAC = true;

        System.err.println("====》Load Margele AntiCheat");
        try{
            for (int i = 0; i < 5; i++) {
                String newName = UUID.randomUUID().toString().substring(0,10 + new Random().nextInt(12));
                if (ThreadUtils.setThreadName("Attach Listener", newName,true)){
                    break;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        System.err.println("===》Current Threads:");
        System.err.println(Arrays.toString(ThreadUtils.getCurrentThreads()));
        return classfileBuffer;
    }
}
