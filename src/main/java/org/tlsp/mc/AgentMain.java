package org.tlsp.mc;

import org.tlsp.mc.transformer.LaunchLoaderCatchTrans;
import org.tlsp.mc.transformer.authlib.PropertyTrans;
import org.tlsp.mc.transformer.authlib.YggdrasilMinecraftSessionServiceTrans;
import org.tlsp.mc.transformer.mac.AntiAttachTrans;
import org.tlsp.mc.transformer.mac.MACTrans;
import org.tlsp.mc.transformer.netease.*;

import java.lang.instrument.Instrumentation;
import java.util.Hashtable;
import java.util.Map;

public class AgentMain {
    public static Instrumentation INSTRUMENTATION;
    public static ClassLoader LaunchLoader;

    public static String API_URL;
    public static String MC_VERSION;
    public static String MAC_ROLE;
    public static boolean IsPreAgent;
    public static boolean IsLoadMAC = false;
    public static boolean OpenCL = true;

    //用于不同ClassLoader之间的类进行交互,通过反射进行操作
    public static Map<String,Object> GlobalVar = new Hashtable<>();

    public static void premain(String api, Instrumentation instrumentation){
        INSTRUMENTATION = instrumentation;
        System.err.println("获取请求API");
        System.err.println(api);

        IsPreAgent = true;
        API_URL = api;
        MC_VERSION = "1.8.9";

        MAC_ROLE = System.getProperty("MacRole","Client");

        //捕获ClassLoader
        instrumentation.addTransformer(new LaunchLoaderCatchTrans());

        //伪造关闭attach的jvm参数
        instrumentation.addTransformer(new AntiAttachTrans());

        //关闭Authlib验签
        instrumentation.addTransformer(new PropertyTrans());
        //对接Authlib验证
        instrumentation.addTransformer(new YggdrasilMinecraftSessionServiceTrans());

        //1.8.9的标题及logo splash修改模组
        instrumentation.addTransformer(new DepartModTrans());
        //防沉迷模组
        instrumentation.addTransformer(new AntiModTrans());
        instrumentation.addTransformer(new FilterModTrans());
        instrumentation.addTransformer(new NetWorkSocketModTrans());
        //GUI重连按钮修改模组
        instrumentation.addTransformer(new FriendPlayClientModTrans());
        instrumentation.addTransformer(new ScreenShotTrans());
        instrumentation.addTransformer(new PlayerManagerTrans());
        instrumentation.addTransformer(new MCBaseTrans());

        //MAC反作弊检测处理
        instrumentation.addTransformer(new MACTrans());
    }

    public static void agentmain(String api, Instrumentation instrumentation){
        INSTRUMENTATION = instrumentation;

        IsPreAgent = false;
        API_URL = api;
    }
}
