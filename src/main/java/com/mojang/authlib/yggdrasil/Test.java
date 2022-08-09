package com.mojang.authlib.yggdrasil;

import com.sun.management.HotSpotDiagnosticMXBean;
import com.sun.management.VMOption;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.tlsp.mc.utils.ReflectUtils;
import org.tlsp.mc.wrapper.TLSPAgentWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Stream;

public class Test {
    public Test(){

    }
    public Test(String p1,int[] p2){
        System.err.println(p1);
        System.err.println(p2);
    }

    public VMOption getVMOption(String name){
        return new VMOption(name,"true",false,VMOption.Origin.VM_CREATION);
    }

    public static void main(String[] args) throws IOException {
        /*System.err.println(ManagementFactory.getRuntimeMXBean().getInputArguments());
        HotSpotDiagnosticMXBean hotSpotDiagnosticMXBean = ManagementFactory
                .getPlatformMXBean(HotSpotDiagnosticMXBean.class);
        //hotSpotDiagnosticMXBean.setVMOption("DisableAttachMechanism","false");
        System.err.println(hotSpotDiagnosticMXBean.getVMOption("DisableAttachMechanism"));
        System.err.println(new Test().getVMOption("DisableAttachMechanism"));

        System.err.println(ReflectUtils.newInstance(Test.class,"fuck",new int[]{1}));

        System.err.println(URLEncoder.encode("abc=/\\c"));*/

        URLTest();
    }

    public static void URLTest() throws IOException {
        String api = "http://127.0.0.1:14250/yggdrasil/0";
        String name = URLEncoder.encode("c01Packet");
        String value = URLEncoder.encode("==???😄");
        URL url = new URL(String.format("%s/network/get/%s",api,name));
        URLConnection connection = url.openConnection();
        connection.setDoInput(true);
        connection.connect();
        InputStream in = connection.getInputStream();
        byte[] buffer = IOUtils.toByteArray(in);
        System.err.println(Arrays.toString(buffer));
        String result = null;
        if (isUTF8(buffer)){
            result = utf8ConvertString(buffer);
        }else{
            StringUtils.toEncodedString(buffer,null);
        }
        System.err.println(result + "\t" + result.length());
        System.err.println(result.isEmpty());
        in.close();
    }

    public static boolean isUTF8(byte[] utf8){
        if (utf8.length >= 3){
            if (utf8[0] == -17 && utf8[1] == -69 && utf8[2] == -65){
                return true;
            }
        }
        return false;
    }

    public static String utf8ConvertString(byte[] utf8) throws UnsupportedEncodingException {
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

    public  static int searchByte(byte[] data, byte value) {
        int size = data.length;
        for (int i = 0; i < size; ++i) {
            if (data[i] == value) {
                return i;
            }
        }
        return -1;
    }
}
