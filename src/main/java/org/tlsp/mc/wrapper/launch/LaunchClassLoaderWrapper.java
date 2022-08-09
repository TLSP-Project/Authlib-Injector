package org.tlsp.mc.wrapper.launch;

import org.tlsp.mc.wrapper.ReflectWrapper;

import java.net.URL;

public class LaunchClassLoaderWrapper extends ReflectWrapper {
    private static String CLASS_NAME  = "net.minecraft.launchwrapper.LaunchClassLoader";

    public LaunchClassLoaderWrapper(Object instance) {
        super(CLASS_NAME);
        this.instance = instance;
    }

    public void registerTransformer(String transformerClassName){
        callByMethodName("registerTransformer",transformerClassName);
    }

    public void addURL(URL url){
        callByMethodName("addURL",url);
    }

    public void addClassLoaderExclusion(String toExclude){
        callByMethodName("addClassLoaderExclusion",toExclude);
    }

    public void addTransformerExclusion(String toExclude){
        callByMethodName("addTransformerExclusion",toExclude);
    }

    public void addCoreMod(String m){

    }
}
