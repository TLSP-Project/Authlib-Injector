package org.tlsp.mc;

import org.tlsp.mc.transformer.PropertyTrans;
import org.tlsp.mc.transformer.YggdrasilMinecraftSessionServiceTrans;

import java.lang.instrument.Instrumentation;

public class AgentMain {
    public static String API_URL;
    public static boolean IsPreAgent;

    public static void premain(String api, Instrumentation instrumentation){
        System.err.println("获取请求API");
        System.err.println(api);

        IsPreAgent = true;
        API_URL = api;

        instrumentation.addTransformer(new PropertyTrans());
        instrumentation.addTransformer(new YggdrasilMinecraftSessionServiceTrans());
    }

    public static void agentmain(String api, Instrumentation instrumentation){
        IsPreAgent = false;
        API_URL = api;
    }
}
