package org.tlsp.mc.transformer.mc.network;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.*;
import org.tlsp.mc.wrapper.TLSPAgentWrapper;
import org.tlsp.mc.wrapper.notch.FuncMapping;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;

import static org.objectweb.asm.Opcodes.*;

/**
 * 修改原版Class，最高优先级
 */
public class NetworkManagerTrans implements IClassTransformer {
    private byte[] doAntiMod(String className, byte[] classfileBuffer) {
        try {
            String HandleClassName = String.format("org/tlsp/mc/handle/NetworkManager%sHandle", TLSPAgentWrapper.Instance.IsMACClient() ? "Client" : "Server");

            ClassReader cr = new ClassReader(classfileBuffer);
            ClassWriter cw = new ClassWriter(cr,ClassWriter.COMPUTE_FRAMES);
            cr.accept(new ClassVisitor(Opcodes.ASM5,cw) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
                    mv = new MethodVisitor(ASM5,mv) {
                        /**
                         * InsertBefore
                         */
                        @Override
                        public void visitCode() {
                            if (FuncMapping.enableEncryption.equalsIgnoreCase(name) && FuncMapping.enableEncryptionDesc.equals(desc)){
                                System.err.println(String.format("catch method %s , desc: %s , signature: %s",name,desc,signature));
                                this.visitVarInsn(ALOAD, 0);
                                this.visitVarInsn(ALOAD, 1);
                                this.visitMethodInsn(INVOKESTATIC, HandleClassName, "setSecretKey", "(Ljava/lang/Object;Ljavax/crypto/SecretKey;)Ljavax/crypto/SecretKey;", false);
                                this.visitVarInsn(ASTORE,1);
                            }
                            else if(FuncMapping.channelRead0.equalsIgnoreCase(name) && FuncMapping.channelRead0Desc.equals(desc)){
                                System.err.println(String.format("catch method %s , desc: %s , signature: %s",name,desc,signature));
                                this.visitVarInsn(ALOAD,2);
                                this.visitMethodInsn(INVOKESTATIC, HandleClassName,"recPacket","(Ljava/lang/Object;)Ljava/lang/Object;",false);
                                this.visitVarInsn(ASTORE,2);
                            }
                            else if(FuncMapping.sendPacket.equalsIgnoreCase(name) && Arrays.stream(FuncMapping.sendPacketDesc).filter(x->x.equals(desc)).findFirst().isPresent()){
                                System.err.println(String.format("catch method %s , desc: %s , signature: %s",name,desc,signature));
                                this.visitVarInsn(ALOAD,1);
                                this.visitMethodInsn(INVOKESTATIC, HandleClassName,"sendPacket","(Ljava/lang/Object;)Ljava/lang/Object;",false);
                                this.visitVarInsn(ASTORE,1);
                            }
                        }
                    };
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
        }catch (Exception e){
            System.err.println("build new NetworkManager error ," + e);
            e.printStackTrace();
        }

        return classfileBuffer;
    }

    @Override
    public byte[] transform(String obfName, String name, byte[] classfileBuffer) {
        if ("net.minecraft.network.NetworkManager".equals(name)){
            return doAntiMod(name,classfileBuffer);
        }
        return classfileBuffer;
    }
}
