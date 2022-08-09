package org.tlsp.mc.utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.tlsp.mc.wrapper.TLSPAgentWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

public class NetworkUtils {
    /**
     * 将KV提交至TLSP本地服务
     * @param name
     * @param value
     * @return
     */
    public static boolean putTGlobalValue(String name,String value){
        String api = TLSPAgentWrapper.Instance.getAPIURL();
        name = URLEncoder.encode(name);
        if (StringUtils.isBlank(value)){
            value = "null";
        }
        value = URLEncoder.encode(value);

        URL url = null;
        try {
            url = new URL(String.format("%s/network/put/%s/%s",api,name,value));
            URLConnection connection = url.openConnection();
            connection.connect();
            InputStream in = connection.getInputStream();
            byte[] buffer = IOUtils.toByteArray(in);
            in.close();
            String result = new String(buffer);
            if ("{}".equals(result)){
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 从TLSP本地服务取V
     * @param name
     * @return
     */
    public static String getTGlobalValue(String name){
        String api = TLSPAgentWrapper.Instance.getAPIURL();
        name = URLEncoder.encode(name);

        URL url = null;
        try {
            url = new URL(String.format("%s/network/get/%s",api,name));
            URLConnection connection = url.openConnection();
            connection.setReadTimeout(6000);
            connection.connect();
            InputStream in = connection.getInputStream();
            byte[] buffer = IOUtils.toByteArray(in);
            in.close();
            //System.err.println(name + "\t " + Arrays.toString(buffer));
            String result = null;
            if (isUTF8(buffer)){
                result = utf8ConvertString(buffer);
            }else{
                result = new String(buffer);
            }
            if (result != null && "null".equalsIgnoreCase(result)){
                return "";
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 尝试从TLSP本地服务取值直至成功，可设置超时时间默认无限
     * @param name
     * @param timout 秒
     * @return
     */
    public static String tryGetTGlobalValue(String name,long timout) throws TimeoutException, InterruptedException {
        long expireTime = System.currentTimeMillis() + timout * 1000;
        //等待处理过的C01包的buffer
        String value = NetworkUtils.getTGlobalValue(name);
        while (StringUtils.isEmpty(value)){
            Thread.sleep(500);
            value = NetworkUtils.getTGlobalValue(name);
            if (System.currentTimeMillis() >= expireTime){
                throw new TimeoutException("get " + name + " time out");
            }
        }
        return value;
    }

    public static String tryGetTGlobalEqulasValue(String name,String evalue,long timout) throws TimeoutException, InterruptedException {
        if (StringUtils.isEmpty(evalue)){
            return null;
        }
        long expireTime = System.currentTimeMillis() + timout * 1000;
        //等待处理过的C01包的buffer
        String value = NetworkUtils.getTGlobalValue(name);
        while (StringUtils.isEmpty(value) || !value.equals(evalue)){
            Thread.sleep(500);
            value = NetworkUtils.getTGlobalValue(name);
            if (System.currentTimeMillis() >= expireTime){
                throw new TimeoutException("get " + name + " time out");
            }
        }
        return value;
    }

    private static boolean isUTF8(byte[] utf8){
        if (utf8.length >= 3){
            if (utf8[0] == -17 && utf8[1] == -69 && utf8[2] == -65){
                return true;
            }
        }
        return false;
    }

    private static String utf8ConvertString(byte[] utf8) throws UnsupportedEncodingException {
        if (utf8.length == 3){
            return "";
        }
        byte[] temp = new byte[utf8.length - 3];
        System.arraycopy(utf8,3,temp,0,temp.length);
        int end = searchByte(utf8,(byte)0);
        if (end == 0){
            return "";
        }else if(end > 0){
            temp = Arrays.copyOf(temp,end + 1);
        }

        return new String(temp,"UTF-8");
    }

    private static int searchByte(byte[] data, byte value) {
        int size = data.length;
        for (int i = 0; i < size; ++i) {
            if (data[i] == value) {
                return i;
            }
        }
        return -1;
    }
}
