package org.tlsp.mc.transformer.netease;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import static org.objectweb.asm.Opcodes.RETURN;

public class DepartModTrans implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        switch (className){
            case "com/netease/mc/mod/departmod/DepartMod":
                return doAntiMod(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
            case "com/netease/mc/mod/departmod/coremod/NewDrawSplashScreen":
                return doAntiMod(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
            default:
                return classfileBuffer;
        }
    }

    private byte[] doAntiMod(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        ClassNode cn = new ClassNode(Opcodes.ASM5);
        ClassReader cr = new ClassReader(classfileBuffer);
        ClassWriter cw = new ClassWriter(cr,ClassWriter.COMPUTE_FRAMES);
        cr.accept(new ClassVisitor(Opcodes.ASM5,cw) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

                if ("preInit".equalsIgnoreCase(name)){
                    mv.visitInsn(RETURN);
                    mv.visitMaxs(-1, -1);
                    mv.visitEnd();
                    return null;
                }else if("drawSplashScreen".equalsIgnoreCase(name)){
                    mv.visitInsn(RETURN);
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
}
