package lyw.demo.service;

import lombok.Data;
import lyw.demo.pojo.Sql_Task;
import lyw.demo.util.JdbcUtils;
import lyw.demo.util.StringUtil;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Data
public class Task implements Runnable{

    private Sql_Task sql_task;

    private List<List<Object>> result;

    private List<Class> classes;

    @Override
    public void run() {
        try {
            String delete_sql = "";

            delete_sql += " delete from " + sql_task.getNew_table();

            JdbcUtils.execute(JdbcUtils.getConnection(sql_task.getTarget_conn()),delete_sql);

            List<List<Object>> list = JdbcUtils.getResult(sql_task.getSql(),JdbcUtils.getConnection(sql_task.getSource_conn()),sql_task.getSource_conn().getDb_type());

            String[] ss = StringUtil.concatSql(sql_task.getSql());

            String sql = "insert into " + sql_task.getNew_table() + " values(";

            for(int i = 0;i < ss.length;++i){
                if(i == 0) sql += "?";
                else sql += ",?";
            }

            sql += ")";

            for(List<Object> objectList : list){
                JdbcUtils.execute(JdbcUtils.getConnection(sql_task.getTarget_conn()),sql,objectList);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            ThreadPool.remove(sql_task.getSql());
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
