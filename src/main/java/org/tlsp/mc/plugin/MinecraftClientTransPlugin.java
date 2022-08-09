package org.tlsp.mc.plugin;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.tlsp.mc.transformer.mc.network.NetworkManagerTrans;

import java.util.Map;

public class MinecraftClientTransPlugin implements IFMLLoadingPlugin {
    @Override
    public String[] getASMTransformerClass() {
        return new String[]{
                NetworkManagerTrans.class.getName()
        };
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> map) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
