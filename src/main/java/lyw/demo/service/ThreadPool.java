package lyw.demo.service;

import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPool {

    private static ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    private static HashMap<String,ScheduledFuture> map = new HashMap<>();

    public static void addTask(Task task){
        ScheduledFuture scheduledFuture = scheduledThreadPoolExecutor.scheduleAtFixedRate(task,0,5, TimeUnit.SECONDS);
        map.put(task.getSql_task().getSql(),scheduledFuture);
    }

    public static void init(){
        scheduledThreadPoolExecutor  = new ScheduledThreadPoolExecutor(10);
    }

    public static void remove(String sql){
        ScheduledFuture scheduledFuture = map.get(sql);
        if(!scheduledFuture.isCancelled()) scheduledFuture.cancel(false); //当前正在运行的任务还能继续执行
        map.remove(sql);
    }
}
