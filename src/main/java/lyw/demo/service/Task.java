package lyw.demo.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lyw.demo.pojo.Sql_Task;
import lyw.demo.util.JdbcUtils;

import java.sql.Connection;
import java.util.List;

@Data
@Slf4j
public class Task extends JdbcService implements Runnable{

    private Sql_Task sql_task;

    @Override
    public void run() {
        try {
            Connection targetConn = getConnection(sql_task.getTarget_conn());

            //删除表所有数据
            String delete_sql = "";
            delete_sql += " delete from " + sql_task.getNew_table();
            JdbcUtils.execute(targetConn,delete_sql);

            //查询源表数据
            Connection sourceConn = getConnection(sql_task.getSource_conn());
            List<List<Object>> list = getResult(sql_task.getSource_conn(),sql_task.getT_sql());
            JdbcUtils.close(sourceConn,null,null);

            String[] ss = concatSql(sql_task.getT_sql(),sql_task.getSource_conn());

            String sql = "insert into " + sql_task.getNew_table() + " values(";

            for(int i = 0;i < ss.length;++i){
                if(i == 0) sql += "?";
                else sql += ",?";
            }
            sql += ")";

            //向目标表新增数据
            for(List<Object> objectList : list){
                JdbcUtils.execute(targetConn,sql,objectList);
            }
            log.info(sql_task.getT_sql() + "执行");
            JdbcUtils.close(targetConn,null,null);
            if(sql_task.getType().equals("运行一次")){
                ThreadPool.remove(sql_task.getT_sql());
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("移出任务" + sql_task.getT_sql());
            ThreadPool.remove(sql_task.getT_sql());
        }
    }



}
