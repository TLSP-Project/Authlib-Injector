package org.tlsp.mc.wrapper.forge.plugin;

import org.tlsp.mc.wrapper.ReflectWrapper;

import java.util.List;

public class CoreModManagerWrapper extends ReflectWrapper {
    private static String CLASS_NAME = "net.minecraftforge.fml.relauncher.CoreModManager";

    public CoreModManagerWrapper() {
        super(CLASS_NAME);
    }

    public List getLoadPlugins(){
        try {
            return (List) getStaticFieldByName("loadPlugins");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            System.err.println(e);
            throw new RuntimeException(e);
        }
    }

    public boolean addLoadPlugin(Object plugin){
        List list = getLoadPlugins();
        if (list == null){
            return false;
        }else if(list.contains(plugin)){
            return true;
        }
        return list.add(plugin);
    }
}
