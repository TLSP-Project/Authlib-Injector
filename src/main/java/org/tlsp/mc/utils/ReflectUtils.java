package org.tlsp.mc.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;

public class ReflectUtils {
    public static Class getClass(String className,ClassLoader loader) throws ClassNotFoundException {
        return Class.forName(className,false,loader);
    }

    /**
     * 无参构造
     * @param cls
     * @return
     */
    public static Object newInstance(Class cls){
        try {
            return cls.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 有参构造 - 自动封包，无法识别基本类型参数 无法识别null
     * @param cls
     * @param params
     * @return
     */
    public static Object newInstance(Class cls,Object ... params){
        Class[] paramTypes = Arrays.stream(params).map(x -> x.getClass()).toArray(Class[]::new);
        for (Constructor constructor : cls.getDeclaredConstructors()) {
            try {
                Class[] constructorParamTypes = constructor.getParameterTypes();
                //包装构造器参数类型
                for (int i = 0; i < constructorParamTypes.length; i++) {
                    if (boolean.class.equals(constructorParamTypes[i])) {
                        constructorParamTypes[i] = Boolean.class;
                    } else if (short.class.equals(constructorParamTypes[i])) {
                        constructorParamTypes[i] = Short.class;
                    } else if (int.class.equals(constructorParamTypes[i])) {
                        constructorParamTypes[i] = Integer.class;
                    } else if (long.class.equals(constructorParamTypes[i])) {
                        constructorParamTypes[i] = Long.class;
                    } else if (float.class.equals(constructorParamTypes[i])) {
                        constructorParamTypes[i] = Float.class;
                    } else if (double.class.equals(constructorParamTypes[i])) {
                        constructorParamTypes[i] = Double.class;
                    }
                }
                System.err.println(Arrays.toString(constructorParamTypes));

                //判断参数数量是否一致
                if (paramTypes.length != constructorParamTypes.length){
                    continue;
                }
                boolean isEQ = true;
                //判断参数类型是否一致
                for (int i = 0; i < paramTypes.length; i++) {
                    if (paramTypes[i] != constructorParamTypes[i]){
                        isEQ = false;
                        break;
                    }
                }
                if (!isEQ){
                    continue;
                }
                constructor.setAccessible(true);
                return constructor.newInstance(params);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                System.err.println(e);
            }
        }
        throw new RuntimeException("构建失败,请检查传入参数");
    }

    /**
     * 有参构造 - 根据传入参数尝试所有构造器
     * @param cls
     * @param params
     * @return
     */
    public static Object newInstanceTryAllConstructor(Class cls,Object ... params){
        for (Constructor constructor : cls.getDeclaredConstructors()) {
            try {
                if (constructor.getParameterCount() < params.length){
                    continue;
                }
                constructor.setAccessible(true);
                return constructor.newInstance(params);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println(e);
            }
        }
        throw new RuntimeException("构建失败,请检查传入参数");
    }

    /**
     * 有参构造 - 手动提供参数类型，避免基本类型与封装类型的误判
     * @param cls
     * @param params
     * @param types
     * @return
     */
    public static Object newInstanceByTypes(Class cls,Object[] params,Class[] types){
        try{
            Constructor c = cls.getDeclaredConstructor(types);
            c.setAccessible(true);
            return c.newInstance(params);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static Method getMethodByName(Class cls,String methodName){
        Optional<Method> opt = Arrays.stream(cls.getDeclaredMethods()).filter(x -> methodName.equals(x.getName())).findFirst();
        return opt.orElse(null);
    }
    public static Method getPublicMethodByName(Class cls,String methodName){
        Optional<Method> opt = Arrays.stream(cls.getMethods()).filter(x -> methodName.equals(x.getName())).findFirst();
        return opt.orElse(null);
    }

    public static Object callMethod(Method method,Object ins,Object ... params){
        try {
            return method.invoke(ins,params);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Object callStaticMethod(Method method,Object ... params){
        try {
            return method.invoke(null,params);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

}
