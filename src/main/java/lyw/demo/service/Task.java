package lyw.demo.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lyw.demo.pojo.Sql_Task;
import lyw.demo.util.JdbcUtils;
import lyw.demo.util.StringUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Data
@Slf4j
public class Task implements Runnable{

    private Sql_Task sql_task;

    private List<Class> classes;

    @Override
    public void run() {
        try {
            Connection targetConn = JdbcUtils.getConnection(sql_task.getTarget_conn());

            //删除表所有数据
            String delete_sql = "";
            delete_sql += " delete from " + sql_task.getNew_table();
            JdbcUtils.execute(targetConn,delete_sql);

            //查询源表数据
            Connection sourceConn = JdbcUtils.getConnection(sql_task.getSource_conn());
            List<List<Object>> list = JdbcUtils.getResult(sql_task.getSql(),sourceConn,sql_task.getSource_conn().getDb_type());
            JdbcUtils.close(sourceConn,null,null);

            String[] ss = StringUtil.concatSql(sql_task.getSql());

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
            System.out.println(sql_task.getSql() + "执行");
            JdbcUtils.close(targetConn,null,null);


        } catch (SQLException e) {
            e.printStackTrace();
            log.error("移出任务" + sql_task.getSql());
            ThreadPool.remove(sql_task.getSql());
        }catch (RuntimeException e){
            e.printStackTrace();
        }
    }

    private void setClasses(List<Object> list){
        list.forEach(o -> {
            if(o instanceof Double) classes.add(Double.class);
            else if(o instanceof Integer) classes.add(Integer.class);
            else if(o instanceof Float) classes.add(Float.class);
        });
    }

}
