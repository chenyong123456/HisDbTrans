package lyw.demo.service.Impl;

import lombok.extern.slf4j.Slf4j;
import lyw.demo.pojo.Db_Connection;
import lyw.demo.pojo.Sql_Task;
import lyw.demo.service.Task;
import lyw.demo.service.SqlTaskService;
import lyw.demo.service.ThreadPool;
import lyw.demo.util.JdbcUtils;
import lyw.demo.util.StringUtil;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Service
@Slf4j
public class SqlTaskServiceImpl implements SqlTaskService {



    @Override
    public boolean CheckSqlRight(Db_Connection db_connection,String sql) throws SQLException {
        Connection connection = null;

        connection = JdbcUtils.getConnection(db_connection);

        try {
            JdbcUtils.getResultSet(connection,sql);
        } catch (SQLException e) {
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                log.error(stackTraceElement.toString());
            }
            return false;
        }finally {
            JdbcUtils.close(connection,null,null);
        }
        return true;
    }

    @Override
    public Task BuildTask(Sql_Task sql_task) {
        Task task = new Task();
        task.setSql_task(sql_task);

        ThreadPool.addTask(task);

        return task;
    }

    @Override
    public boolean CheckTableRight(String table, Db_Connection db_connection) throws SQLException {
        Connection connection = null;

        connection = JdbcUtils.getConnection(db_connection);

        String sql = null;
        switch (db_connection.getDb_type()){
            case "mysql":
                sql = "select table_name from information_schema.tables where table_schema=" + "'" + db_connection.getDb_name() + "'";
                break;
            case "sqlServer":
                JdbcUtils.execute(connection,"use " + db_connection.getDb_name());
                sql = "select name from sys.tables";
                break;
        }

        List<List<Object>> list = JdbcUtils.getResult(sql,connection,db_connection.getDb_type());

        for(List<Object> objectList : list){
            for(Object o : objectList){
                if(o.toString().equals(table)) return false;
            }
        }

        JdbcUtils.close(connection,null,null);
        return true;
    }


    @Override
    public String Insert(Sql_Task sql_task) {
        return null;
    }

    @Override
    public void createTable(Db_Connection db_connection, String table, String sql) throws SQLException {
        Connection connection = JdbcUtils.getConnection(db_connection);

        String[] ss = StringUtil.concatSql(sql);

        String create_sql = "";

        switch (db_connection.getDb_type()){
            case "mysql":
                create_sql += "create table ";

                create_sql = create_sql + "`" + table + "`";

                create_sql = create_sql + "(";

                boolean flag = true;
                for(String s : ss){
                    if(flag) flag = false;
                    else create_sql = create_sql + ",";
                    create_sql = create_sql + "`" + s + "`" + " varchar(255) NULL";
                }

                create_sql += ")";
                break;
            case "sqlServer":
        }

        JdbcUtils.execute(connection,create_sql);

        JdbcUtils.close(connection,null,null);
    }
}
