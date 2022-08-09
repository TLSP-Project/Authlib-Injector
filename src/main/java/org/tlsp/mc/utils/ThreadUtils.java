package org.tlsp.mc.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Optional;

public class ThreadUtils {
    /**
     * 获取当前活跃线程
     * @return
     */
    public static Thread[] getCurrentThreads(){
        ThreadGroup group = Thread.currentThread().getThreadGroup();
        ThreadGroup top = group;
        while (group != null){
            top = group;
            group = group.getParent();
        }
        int stackSize = top.activeCount() * 2;
        Thread[] actives = new Thread[stackSize];
        int actualSize = top.enumerate(actives);
        Thread[] actuals = new Thread[actualSize];
        System.arraycopy(actives,0,actuals,0,actualSize);
        return actuals;
    }

    /**
     * 设置线程名称
     * @param thread
     * @param name
     * @return
     */
    public static boolean setThreadName(Thread thread,String name){
        try {
            thread.setName(name);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 设置线程名称 通过现有名称
     * @param name
     * @param newName
     * @param like 模糊搜索
     * @return
     */
    public static boolean setThreadName(String name,String newName,boolean like){
        Optional<Thread> opt = Arrays.asList(getCurrentThreads()).stream().filter(x -> x != null && StringUtils.isNoneBlank(x.getName()) &&
                (like ? x.getName().contains(name) : name.equals(x.getName()))
        ).findFirst();
        if (opt.isPresent()){
            return setThreadName(opt.get(),newName);
        }
        return false;
    }
}
