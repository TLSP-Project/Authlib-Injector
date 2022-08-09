package org.tlsp.mc.wrapper;

import org.tlsp.mc.utils.ReflectUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

public class ReflectWrapper {
    protected String className;
    protected ClassLoader loader;
    protected Class cls;
    protected Object instance;
    protected Method[] publicMethods;
    protected Method[] methods;
    protected Field[] publicFields;
    protected Field[] fields;

    public Object getInstance(){
        return instance;
    }

    private void initClassInfo(){
        publicMethods = cls.getMethods();
        methods = cls.getDeclaredMethods();
        publicFields = cls.getFields();
        fields = cls.getDeclaredFields();
    }

    public static ReflectWrapper CastReflectWrapper(Object instance){
            ReflectWrapper wrapper = new ReflectWrapper(instance.getClass().getName(),instance.getClass().getClassLoader());
            wrapper.instance = instance;
            return wrapper;
    }

    public ReflectWrapper(String className){
        this(className,ClassLoader.getSystemClassLoader());
    }

    public ReflectWrapper(String className,ClassLoader loader) {
        this.className = className;
        this.loader = loader;
        try {
            cls = ReflectUtils.getClass(className,loader);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (cls == null){
            throw new RuntimeException(className);
        }
        initClassInfo();
    }

    protected Object newInstance(){
        instance = ReflectUtils.newInstance(cls);
        if (instance == null){
            throw new IllegalArgumentException(String.format("未能创建实例,%s",className));
        }
        return instance;
    }
    protected Object newInstance(Object ... params){
        instance = ReflectUtils.newInstance(cls,params);
        if (instance == null){
            throw new IllegalArgumentException(String.format("未能创建实例,%s,参数:%s",className, Arrays.toString(params)));
        }
        return instance;
    }
    protected Object tryNewInstance(Object ... params){
        instance = ReflectUtils.newInstanceTryAllConstructor(cls,params);
        if (instance == null){
            throw new IllegalArgumentException(String.format("未能创建实例,%s,参数:%s",className, Arrays.toString(params)));
        }
        return instance;
    }

    protected Object getFieldByName(String name) throws IllegalAccessException {
        Optional<Field> opt = Arrays.stream(fields).filter(x -> name.equals(x.getName())).findFirst();
        Field field = opt.orElse(null);
        if (field == null){
            throw new IllegalArgumentException(String.format("不存在此属性,%s",name));
        }
        field.setAccessible(true);
        return field.get(instance);
    }
    protected Object getStaticFieldByName(String name) throws IllegalAccessException {
        Optional<Field> opt = Arrays.stream(fields).filter(x -> name.equals(x.getName())).findFirst();
        Field field = opt.orElse(null);
        if (field == null){
            throw new IllegalArgumentException(String.format("不存在此属性,%s",name));
        }
        field.setAccessible(true);
        return field.get(null);
    }
    protected void setFieldByName(String name,Object value) throws IllegalAccessException {
        Optional<Field> opt = Arrays.stream(fields).filter(x -> name.equals(x.getName())).findFirst();
        Field field = opt.orElse(null);
        if (field == null){
            throw new IllegalArgumentException(String.format("不存在此属性,%s",name));
        }
        field.setAccessible(true);
        field.set(instance,value);
    }
    protected void setStaticFieldByName(String name,Object value) throws IllegalAccessException {
        Optional<Field> opt = Arrays.stream(fields).filter(x -> name.equals(x.getName())).findFirst();
        Field field = opt.orElse(null);
        if (field == null){
            throw new IllegalArgumentException(String.format("不存在此属性,%s",name));
        }
        field.setAccessible(true);
        field.set(null,value);
    }

    protected Object callByMethodName(String methodName,Object ... params) {
        Optional<Method> opt = Arrays.stream(methods).filter(x -> methodName.equals(x.getName())).findFirst();
        Method method = opt.orElse(null);
        if (method == null){
            throw new IllegalArgumentException(String.format("不存在此方法,%s.%s",className,methodName));
        }
        try {
            method.setAccessible(true);
            System.err.println("call method " + method + " \t " + Arrays.toString(params));
            return method.invoke(instance,params);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
    protected Object callStaticByMethodName(String methodName,Object ... params) {
        Optional<Method> opt = Arrays.stream(methods).filter(x -> methodName.equals(x.getName())).findFirst();
        Method method = opt.orElse(null);
        if (method == null){
            throw new IllegalArgumentException(String.format("不存在此方法,%s.%s",className,methodName));
        }
        try {
            method.setAccessible(true);
            return method.invoke(null,params);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

}
