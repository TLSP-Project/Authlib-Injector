package org.tlsp.mc.transformer.netease;

import org.apache.commons.io.IOUtils;
import org.objectweb.asm.*;
import org.tlsp.mc.utils.ResourceUtils;
import org.tlsp.mc.utils.ThreadUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

import static org.objectweb.asm.Opcodes.*;

public class NetassLaunchTrans implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        switch (className){
            case "net/minecraft/launchwrapper/Launch":
                return doLaunchInit(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
            case "net/minecraft/launchwrapper/LaunchClassLoader":
                return doLaunchClassLoaderInit(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
            case "com/netease/mc/mod/network/common/NeteaseMain":
                return doNetassInit(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
            default:
                return classfileBuffer;
        }
    }

    private byte[] doLaunchInit(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        System.err.println("===》拦截游戏启动");
        System.err.println(System.getProperty("user.dir"));

        byte[] buffer = new byte[0];

        try{
            buffer = ResourceUtils.getResource("Launch.class");
        }catch (Exception e){
            e.printStackTrace();
        }

        return buffer;
    }

    private byte[] doLaunchClassLoaderInit(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        System.err.println("===》拦截游戏启动类加载器");
        System.err.println(System.getProperty("user.dir"));

        byte[] buffer = new byte[0];
        try{
            buffer = ResourceUtils.getResource("LaunchClassLoader.class");
        }catch (Exception e){
            e.printStackTrace();
        }

        return buffer;
    }

    private byte[] doNetassInit(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer){
        System.err.println("===》拦截网易通讯初始化");

        ClassReader cr = new ClassReader(classfileBuffer);
        ClassWriter cw = new ClassWriter(cr,ClassWriter.COMPUTE_FRAMES);
        cr.accept(new ClassVisitor(Opcodes.ASM5,cw) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
                if ("init".equals(name)){
                    mv.visitInsn(ARETURN);
                    mv.visitMaxs(-1, -1);
                    mv.visitEnd();
                    return null;
                }
                return mv;
            }
        },0);

        byte[] buffer = cw.toByteArray();
        File file = new File(className.replace("/",".") + ".class");
        try {
            if (file.exists()){
                file.delete();
            }
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            out.write(buffer);
            out.close();
        }catch (Exception e){

        }

        return buffer;
    }
}
