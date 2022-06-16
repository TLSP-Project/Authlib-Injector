package org.tlsp.mc.transformer;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import static org.objectweb.asm.Opcodes.*;

import org.tlsp.mc.AgentMain;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.ProtectionDomain;


public class YggdrasilMinecraftSessionServiceTrans implements ClassFileTransformer {
    public static ClassLoader LaunchLoader;
    private static String MojangSessionServer = "https://sessionserver.mojang.com";
    private static URL JOIN_URL;
    private static URL CHECK_URL;

    private boolean isLastAReturn = false;

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (!"com/mojang/authlib/yggdrasil/YggdrasilMinecraftSessionService".equals(className)){
            return classfileBuffer;
        }

        System.err.println("===》获取到验证服务 \t" + loader);

        LaunchLoader = loader;

        try {
            JOIN_URL = new URL(AgentMain.API_URL + "/session/minecraft/join");
            CHECK_URL = new URL(AgentMain.API_URL + "/session/minecraft/hasJoined");
        } catch (MalformedURLException e) {
            throw new IllegalClassFormatException("URL 初始化失败");
        }

        ClassNode cn = new ClassNode(Opcodes.ASM5);
        ClassReader cr = new ClassReader(classfileBuffer);
        ClassWriter cw = new ClassWriter(cr,ClassWriter.COMPUTE_FRAMES);
        cr.accept(new ClassVisitor(Opcodes.ASM5,cw) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc,String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

                if ("<clinit>".equals(name) && "()V".equals(desc)){
                    return new MethodVisitor(Opcodes.ASM5, mv) {
                        @Override
                        public void visitLdcInsn(Object cst) {
                            if (cst instanceof String){
                                String val = ((String)cst);
                                if(val.startsWith(MojangSessionServer)) {
                                    cst = val.replace(MojangSessionServer,AgentMain.API_URL);
                                }
                            }
                            super.visitLdcInsn(cst);
                        }
                    };
                }
                else if(access == Opcodes.ACC_PUBLIC && "fillProfileProperties".equals(name)){
                    mv.visitVarInsn(ALOAD,0);
                    mv.visitVarInsn(ALOAD,1);
                    mv.visitInsn(Opcodes.ICONST_0);
                    mv.visitMethodInsn(INVOKEVIRTUAL,"com/mojang/authlib/yggdrasil/YggdrasilMinecraftSessionService","fillGameProfile","(Lcom/mojang/authlib/GameProfile;Z)Lcom/mojang/authlib/GameProfile;",false);
                    mv.visitInsn(ARETURN);
                    mv.visitMaxs(-1, -1);
                    mv.visitEnd();
                    return null;
                }
                else if(access == Opcodes.ACC_PROTECTED && "fillGameProfile".equals(name)){
                    return new MethodVisitor(Opcodes.ASM5, mv) {
                        @Override
                        public void visitLdcInsn(Object cst) {
                            if (cst instanceof String){
                                String val = ((String)cst);
                                if(val.startsWith(MojangSessionServer)) {
                                    cst = val.replace(MojangSessionServer,AgentMain.API_URL);
                                }
                            }
                            super.visitLdcInsn(cst);
                        }
                    };
                }
                else if("getTextures".equals(name)){

                    Label l0 = new Label();
                    Label l1 = new Label();
                    Label l2 = new Label();
                    mv.visitTryCatchBlock(l0, l1, l2, "java/lang/Exception");
                    Label l3 = new Label();
                    Label l4 = new Label();
                    mv.visitTryCatchBlock(l3, l4, l2, "java/lang/Exception");
                    mv.visitLabel(l0);
                    mv.visitLineNumber(111, l0);
                    mv.visitVarInsn(ALOAD, 1);
                    mv.visitMethodInsn(INVOKEVIRTUAL, "com/mojang/authlib/GameProfile", "getProperties", "()Lcom/mojang/authlib/properties/PropertyMap;", false);
                    mv.visitLdcInsn("textures");
                    mv.visitMethodInsn(INVOKEVIRTUAL, "com/mojang/authlib/properties/PropertyMap", "get", "(Ljava/lang/Object;)Ljava/util/Collection;", false);
                    mv.visitInsn(ACONST_NULL);
                    mv.visitMethodInsn(INVOKESTATIC, "com/google/common/collect/Iterables", "getFirst", "(Ljava/lang/Iterable;Ljava/lang/Object;)Ljava/lang/Object;", false);
                    mv.visitTypeInsn(CHECKCAST, "com/mojang/authlib/properties/Property");
                    mv.visitVarInsn(ASTORE, 4);
                    Label l5 = new Label();
                    mv.visitLabel(l5);
                    mv.visitLineNumber(112, l5);
                    mv.visitVarInsn(ALOAD, 4);
                    mv.visitJumpInsn(IFNONNULL, l3);
                    Label l6 = new Label();
                    mv.visitLabel(l6);
                    mv.visitLineNumber(114, l6);
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitVarInsn(ALOAD, 1);
                    mv.visitInsn(ICONST_0);
                    mv.visitMethodInsn(INVOKEVIRTUAL, "com/mojang/authlib/yggdrasil/YggdrasilMinecraftSessionService", "fillGameProfile", "(Lcom/mojang/authlib/GameProfile;Z)Lcom/mojang/authlib/GameProfile;", false);
                    mv.visitMethodInsn(INVOKEVIRTUAL, "com/mojang/authlib/GameProfile", "getProperties", "()Lcom/mojang/authlib/properties/PropertyMap;", false);
                    mv.visitLdcInsn("textures");
                    mv.visitMethodInsn(INVOKEVIRTUAL, "com/mojang/authlib/properties/PropertyMap", "get", "(Ljava/lang/Object;)Ljava/util/Collection;", false);
                    mv.visitInsn(ACONST_NULL);
                    mv.visitMethodInsn(INVOKESTATIC, "com/google/common/collect/Iterables", "getFirst", "(Ljava/lang/Iterable;Ljava/lang/Object;)Ljava/lang/Object;", false);
                    mv.visitTypeInsn(CHECKCAST, "com/mojang/authlib/properties/Property");
                    mv.visitVarInsn(ASTORE, 4);
                    Label l7 = new Label();
                    mv.visitLabel(l7);
                    mv.visitLineNumber(115, l7);
                    mv.visitVarInsn(ALOAD, 4);
                    mv.visitJumpInsn(IFNONNULL, l3);
                    Label l8 = new Label();
                    mv.visitLabel(l8);
                    mv.visitLineNumber(117, l8);
                    mv.visitTypeInsn(NEW, "java/util/HashMap");
                    mv.visitInsn(DUP);
                    mv.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false);
                    mv.visitLabel(l1);
                    mv.visitInsn(ARETURN);
                    mv.visitLabel(l3);
                    mv.visitLineNumber(122, l3);
                    mv.visitFrame(Opcodes.F_APPEND, 2, new Object[]{Opcodes.TOP, "com/mojang/authlib/properties/Property"}, 0, null);
                    mv.visitTypeInsn(NEW, "java/lang/String");
                    mv.visitInsn(DUP);
                    mv.visitVarInsn(ALOAD, 4);
                    mv.visitMethodInsn(INVOKEVIRTUAL, "com/mojang/authlib/properties/Property", "getValue", "()Ljava/lang/String;", false);
                    mv.visitMethodInsn(INVOKESTATIC, "org/apache/commons/codec/binary/Base64", "decodeBase64", "(Ljava/lang/String;)[B", false);
                    mv.visitFieldInsn(GETSTATIC, "org/apache/commons/codec/Charsets", "UTF_8", "Ljava/nio/charset/Charset;");
                    mv.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>", "([BLjava/nio/charset/Charset;)V", false);
                    mv.visitVarInsn(ASTORE, 5);
                    Label l9 = new Label();
                    mv.visitLabel(l9);
                    mv.visitLineNumber(124, l9);
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitFieldInsn(GETFIELD, "com/mojang/authlib/yggdrasil/YggdrasilMinecraftSessionService", "gson", "Lcom/google/gson/Gson;");
                    mv.visitVarInsn(ALOAD, 5);
                    mv.visitLdcInsn(Type.getType("Lcom/mojang/authlib/yggdrasil/response/MinecraftTexturesPayload;"));
                    mv.visitMethodInsn(INVOKEVIRTUAL, "com/google/gson/Gson", "fromJson", "(Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/Object;", false);
                    mv.visitTypeInsn(CHECKCAST, "com/mojang/authlib/yggdrasil/response/MinecraftTexturesPayload");
                    mv.visitInsn(DUP);
                    mv.visitVarInsn(ASTORE, 3);
                    Label l10 = new Label();
                    mv.visitLabel(l10);
                    Label l11 = new Label();
                    mv.visitJumpInsn(IFNULL, l11);
                    mv.visitVarInsn(ALOAD, 3);
                    mv.visitMethodInsn(INVOKEVIRTUAL, "com/mojang/authlib/yggdrasil/response/MinecraftTexturesPayload", "getTextures", "()Ljava/util/Map;", false);
                    Label l12 = new Label();
                    mv.visitJumpInsn(IFNONNULL, l12);
                    mv.visitLabel(l11);
                    mv.visitLineNumber(126, l11);
                    mv.visitFrame(Opcodes.F_FULL, 6, new Object[]{"com/mojang/authlib/yggdrasil/YggdrasilMinecraftSessionService", "com/mojang/authlib/GameProfile", Opcodes.INTEGER, "com/mojang/authlib/yggdrasil/response/MinecraftTexturesPayload", "com/mojang/authlib/properties/Property", "java/lang/String"}, 0, new Object[]{});
                    mv.visitTypeInsn(NEW, "java/util/HashMap");
                    mv.visitInsn(DUP);
                    mv.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false);
                    mv.visitLabel(l4);
                    mv.visitInsn(ARETURN);
                    mv.visitLabel(l12);
                    mv.visitLineNumber(133, l12);
                    mv.visitFrame(Opcodes.F_CHOP, 2, null, 0, null);
                    Label l13 = new Label();
                    mv.visitJumpInsn(GOTO, l13);
                    mv.visitLabel(l2);
                    mv.visitLineNumber(129, l2);
                    mv.visitFrame(Opcodes.F_FULL, 3, new Object[]{"com/mojang/authlib/yggdrasil/YggdrasilMinecraftSessionService", "com/mojang/authlib/GameProfile", Opcodes.INTEGER}, 1, new Object[]{"java/lang/Exception"});
                    mv.visitVarInsn(ASTORE, 4);
                    Label l14 = new Label();
                    mv.visitLabel(l14);
                    mv.visitLineNumber(131, l14);
                    mv.visitVarInsn(ALOAD, 4);
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Exception", "printStackTrace", "()V", false);
                    Label l15 = new Label();
                    mv.visitLabel(l15);
                    mv.visitLineNumber(132, l15);
                    mv.visitTypeInsn(NEW, "java/util/HashMap");
                    mv.visitInsn(DUP);
                    mv.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false);
                    mv.visitInsn(ARETURN);
                    mv.visitLabel(l13);
                    mv.visitLineNumber(134, l13);
                    mv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{"com/mojang/authlib/yggdrasil/response/MinecraftTexturesPayload"}, 0, null);
                    mv.visitVarInsn(ALOAD, 3);
                    mv.visitMethodInsn(INVOKEVIRTUAL, "com/mojang/authlib/yggdrasil/response/MinecraftTexturesPayload", "getTextures", "()Ljava/util/Map;", false);
                    mv.visitInsn(ARETURN);
                    Label l16 = new Label();
                    mv.visitLabel(l16);
                    mv.visitLocalVariable("textureProperty", "Lcom/mojang/authlib/properties/Property;", null, l5, l12, 4);
                    mv.visitLocalVariable("json", "Ljava/lang/String;", null, l9, l12, 5);
                    mv.visitLocalVariable("result", "Lcom/mojang/authlib/yggdrasil/response/MinecraftTexturesPayload;", null, l10, l2, 3);
                    mv.visitLocalVariable("e", "Ljava/lang/Exception;", null, l14, l13, 4);
                    mv.visitLocalVariable("this", "Lcom/mojang/authlib/yggdrasil/YggdrasilMinecraftSessionService;", null, l0, l16, 0);
                    mv.visitLocalVariable("profile", "Lcom/mojang/authlib/GameProfile;", null, l0, l16, 1);
                    mv.visitLocalVariable("requireSecure", "Z", null, l0, l16, 2);
                    mv.visitLocalVariable("result", "Lcom/mojang/authlib/yggdrasil/response/MinecraftTexturesPayload;", null, l13, l16, 3);
                    mv.visitMaxs(4, 6);
                    mv.visitEnd();

                    return null;

                    /*mv.visitVarInsn(Opcodes.ALOAD,0);
                    mv.visitVarInsn(Opcodes.ALOAD,1);
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC,"org/tlsp/mc/wrapper","getTextures","(Lcom/mojang/authlib/yggdrasil/YggdrasilMinecraftSessionService;Lcom/mojang/authlib/GameProfile;)Ljava/util/Map;",false);
                    mv.visitInsn(Opcodes.ARETURN);
                    mv.visitMaxs(-1, -1);
                    mv.visitEnd();
                    return null;*/
                    /*return new MethodVisitor(Opcodes.ASM5, mv) {
                        @Override
                        public void visitCode() {
                            super.visitCode();
                            this.visitVarInsn(Opcodes.ALOAD,0);
                            this.visitVarInsn(Opcodes.ALOAD,1);
                            this.visitInsn(Opcodes.ICONST_1);
                            this.visitMethodInsn(Opcodes.INVOKEVIRTUAL,"com/mojang/authlib/yggdrasil/YggdrasilMinecraftSessionService","fillGameProfile","(Lcom/mojang/authlib/GameProfile;Z)Lcom/mojang/authlib/GameProfile;",false);
                            this.visitVarInsn(Opcodes.ASTORE,1);
                        }
                    };*/
                }else if("isWhitelistedDomain".equals(name)){
                    mv.visitInsn(Opcodes.ICONST_1);
                    mv.visitInsn(Opcodes.IRETURN);
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
