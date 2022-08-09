package org.tlsp.mc.wrapper.forge.plugin;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.tlsp.mc.wrapper.ReflectWrapper;

import java.io.File;

public class FMLPluginWrapper extends ReflectWrapper {
    private static String CLASS_NAME = "net.minecraftforge.fml.relauncher.CoreModManager$FMLPluginWrapper";

    /**
     * @param name
     * @param coreModInstance IFMLLoadingPlugin
     * @param location
     * @param sortIndex
     * @param predepends
     */
    public FMLPluginWrapper(String name,Object  coreModInstance, File location, int sortIndex, String... predepends) {
        super(CLASS_NAME);
        tryNewInstance(name,coreModInstance,location,sortIndex,predepends);
    }
}
