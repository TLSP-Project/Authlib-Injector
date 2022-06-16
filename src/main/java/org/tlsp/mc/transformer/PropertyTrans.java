package org.tlsp.mc.transformer;

import org.objectweb.asm.*;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import static org.objectweb.asm.Opcodes.ICONST_1;
import static org.objectweb.asm.Opcodes.IRETURN;

public class PropertyTrans implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (!"com/mojang/authlib/properties/Property".equals(className)){
            return classfileBuffer;
        }

        System.err.println("===》获取到属性类");

        ClassReader cr = new ClassReader(classfileBuffer);
        ClassWriter cw = new ClassWriter(cr,ClassWriter.COMPUTE_FRAMES);
        cr.accept(new ClassVisitor(Opcodes.ASM5,cw) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc,String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
                if ("isSignatureValid".equals(name)){
                    mv.visitInsn(ICONST_1);
                    mv.visitInsn(IRETURN);
                    mv.visitMaxs(-1, -1);
                    mv.visitEnd();
                    return null;
                }else if ("hasSignature".equals(name)){
                    mv.visitInsn(ICONST_1);
                    mv.visitInsn(IRETURN);
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
