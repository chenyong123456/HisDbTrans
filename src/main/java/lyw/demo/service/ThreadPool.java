package lyw.demo.service;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ThreadPool {

    private static ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    private static HashMap<String,ScheduledFuture> map = new HashMap<>();

    public static final int TIME = 120;

    public static void addTask(Task task){
        ScheduledFuture scheduledFuture = scheduledThreadPoolExecutor.scheduleAtFixedRate(task,0,TIME, TimeUnit.SECONDS);
        log.info("加入任务");
        map.put(task.getSql_task().getT_sql(),scheduledFuture);
    }

    public static void init(){
        scheduledThreadPoolExecutor  = new ScheduledThreadPoolExecutor(10);
    }

    public static void remove(String sql){
        ScheduledFuture scheduledFuture = map.get(sql);
        if(!scheduledFuture.isCancelled()) scheduledFuture.cancel(false); //当前正在运行的任务还能继续执行
        map.remove(sql);
        log.info(sql + "任务已停止");
    }

    public static ScheduledFuture getScheduledFuture(String sql){
        return map.get(sql);
    }
}
