package org.tlsp.mc.wrapper;

import org.tlsp.mc.AgentMain;

import java.util.Map;

public class TLSPAgentWrapper extends ReflectWrapper {
    public static TLSPAgentWrapper Instance = new TLSPAgentWrapper();
    private static Map<String,Object> globalCache = null;

    public TLSPAgentWrapper() {
        super(AgentMain.class.getName());
    }

    public boolean IsLoadMAC(){
        try {
            return (boolean) getStaticFieldByName("IsLoadMAC");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean IsMACServer(){
        try {
            String role = (String) getStaticFieldByName("MAC_ROLE");
            if ("Server".equalsIgnoreCase(role)){
                return true;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean IsMACClient(){
        try {
             String role = (String) getStaticFieldByName("MAC_ROLE");
             if ("Client".equalsIgnoreCase(role)){
                 return true;
             }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean IsOpenCL(){
        try {
            return (boolean) getStaticFieldByName("OpenCL");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getAPIURL(){
        try {
            return (String) getStaticFieldByName("API_URL");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getMCVersion(){
        try {
            return (String) getStaticFieldByName("MC_VERSION");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void setMCVersion(String version){
        try {
            setStaticFieldByName("MC_VERSION",version);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public ClassLoader getLaunchLoader(){
        try {
            return (ClassLoader) getStaticFieldByName("LaunchLoader");
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public Map<String,Object> getGlobalVar(){
        if (globalCache == null){
            try {
                globalCache = (Map<String, Object>) getStaticFieldByName("GlobalVar");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return globalCache;
    }

    public Object putGlobalVar(String name,Object value){
        return getGlobalVar().put(name,value);
    }
    public Object getGlobalValue(String name){
        return getGlobalVar().getOrDefault(name,null);
    }
}
