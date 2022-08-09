package org.tlsp.mc.transformer.mac;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import static org.objectweb.asm.Opcodes.*;

public class AntiAttachTrans implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        switch (className){
            case "sun/management/HotSpotDiagnostic":
                return doHDInit(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
            default:
                return classfileBuffer;
        }
    }

    private byte[] doHDInit(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        ClassNode cn = new ClassNode(Opcodes.ASM5);
        ClassReader cr = new ClassReader(classfileBuffer);
        ClassWriter cw = new ClassWriter(cr,ClassWriter.COMPUTE_FRAMES);
        cr.accept(new ClassVisitor(Opcodes.ASM5,cw) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

                if ("getVMOption".equalsIgnoreCase(name)){
                    mv.visitTypeInsn(NEW, "com/sun/management/VMOption");
                    mv.visitInsn(DUP);
                    mv.visitVarInsn(ALOAD, 1);
                    mv.visitLdcInsn("true");
                    mv.visitInsn(ICONST_0);
                    mv.visitFieldInsn(GETSTATIC, "com/sun/management/VMOption$Origin", "VM_CREATION", "Lcom/sun/management/VMOption$Origin;");
                    mv.visitMethodInsn(INVOKESPECIAL, "com/sun/management/VMOption", "<init>", "(Ljava/lang/String;Ljava/lang/String;ZLcom/sun/management/VMOption$Origin;)V", false);
                    mv.visitInsn(ARETURN);
                    mv.visitMaxs(-1, -1);
                    mv.visitEnd();
                    return null;
                }

                return mv;
            }
        },0);

        byte[] buffer = cw.toByteArray();
        /*File file = new File(className.replace("/",".") + ".class");
        try {
            if (file.exists()){
                file.delete();
            }
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            out.write(buffer);
            out.close();
        }catch (Exception e){

        }*/

        return buffer;
    }

    private byte[] doInit(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        ClassNode cn = new ClassNode(Opcodes.ASM5);
        ClassReader cr = new ClassReader(classfileBuffer);
        ClassWriter cw = new ClassWriter(cr,ClassWriter.COMPUTE_FRAMES);
        cr.accept(new ClassVisitor(Opcodes.ASM5,cw) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

                if ("getVmArguments".equalsIgnoreCase(name)){
                    mv.visitInsn(ICONST_1);
                    mv.visitTypeInsn(ANEWARRAY, "java/lang/String");
                    mv.visitInsn(DUP);
                    mv.visitInsn(ICONST_0);
                    mv.visitLdcInsn("-XX:+DisableAttachMechanism");
                    mv.visitInsn(AASTORE);
                    mv.visitMethodInsn(INVOKESTATIC, "java/util/Arrays", "asList", "([Ljava/lang/Object;)Ljava/util/List;", false);
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
