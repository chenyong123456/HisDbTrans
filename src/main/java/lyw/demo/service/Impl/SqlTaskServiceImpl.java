package lyw.demo.service.Impl;

import lombok.extern.slf4j.Slf4j;
import lyw.demo.mapper.DbConnectionMapper;
import lyw.demo.mapper.SqlTaskMapper;
import lyw.demo.pojo.Db_Connection;
import lyw.demo.pojo.Sql_Task;
import lyw.demo.service.JdbcService;
import lyw.demo.service.Task;
import lyw.demo.service.SqlTaskService;
import lyw.demo.service.ThreadPool;
import lyw.demo.util.JdbcUtils;
import net.sf.jsqlparser.JSQLParserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Service
@Slf4j
public class SqlTaskServiceImpl extends JdbcService implements SqlTaskService {

    @Autowired
    private SqlTaskMapper sqlTaskMapper;
    @Autowired
    private DbConnectionMapper dbConnectionMapper;

    @Override
    public boolean CheckSqlRight(Sql_Task sql_task) throws SQLException, JSQLParserException {
        Connection connection = null;
        connection = getConnection(sql_task.getSource_conn());

        //解析sql得到查询的字段 判断查询字段与需要字段是否相同
        String[] ss = concatSql(sql_task.getT_sql(),sql_task.getSource_conn());
        //List<Column> columns = sql_task.getColumns();
        //if(ss.length != columns.size()) return false;

        try {
            JdbcUtils.getResultSet(connection,sql_task.getT_sql());
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
        sql_task.setStatus("运行");
        ThreadPool.addTask(task);

        if(!sql_task.getType().equals("持续运行")){
            sql_task.setStatus("停止");
            sqlTaskMapper.updateByPrimaryKeySelective(sql_task);
        }
        return task;
    }

    @Override
    public boolean CheckTableRight(Sql_Task sql_task) throws SQLException, JSQLParserException {
        Connection connection = null;

        Db_Connection db_connection = sql_task.getTarget_conn();

        connection = getConnection(db_connection);

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

        List<List<Object>> list = getResult(db_connection,sql);

        for(List<Object> objectList : list){
            for(Object o : objectList){
                if(o.toString().equals(sql_task.getNew_table())) return false;
            }
        }

        JdbcUtils.close(connection,null,null);
        return true;
    }


    @Override
    public String Insert(Sql_Task sql_task) {
        sqlTaskMapper.insertSelective(sql_task);
        return sql_task.getT_sql();
    }

    @Override
    public void createTable(Sql_Task sql_task) throws SQLException {
        Connection connection = getConnection(sql_task.getTarget_conn());

        ///List<Column> columns = sql_task.getColumns();

        String create_sql = "";

        switch (sql_task.getTarget_conn().getDb_type()){
            case "mysql":
                create_sql += "create table ";

                create_sql = create_sql + "`" + sql_task.getNew_table() + "`";

                create_sql = create_sql + "(";

                boolean flag = true;
//                for(String s : ss){
//                    if(flag) flag = false;
//                    else create_sql = create_sql + ",";
//                    create_sql = create_sql + "`" + s + "`" + " varchar(255) NULL";
//                }

              /*  //for(Column column : columns){
                    if(flag) flag = false;
                    else create_sql = create_sql + ",";
                    create_sql = create_sql + "`" + column.getColumn_name() + "` " + column.getColumn_type();
                }*/

                create_sql += ")";
                break;
            case "sqlServer":
        }

        JdbcUtils.execute(connection,create_sql);

        JdbcUtils.close(connection,null,null);

    }

    @Override
    public List<Sql_Task> getAll() {
        List<Sql_Task> list = sqlTaskMapper.selectAll();

        list.forEach(sql_task -> {
            sql_task.setTarget_conn(dbConnectionMapper.selectByPrimaryKey(sql_task.getTarget_name()));
            sql_task.setSource_conn(dbConnectionMapper.selectByPrimaryKey(sql_task.getSource_name()));
        });
        return list;
    }

    @Override
    public void changeStatus(String sql) {
        Sql_Task sql_task = sqlTaskMapper.selectByPrimaryKey(sql);
        sql_task.setTarget_conn(dbConnectionMapper.selectByPrimaryKey(sql_task.getTarget_name()));
        sql_task.setSource_conn(dbConnectionMapper.selectByPrimaryKey(sql_task.getSource_name()));
        String status = sql_task.getStatus();
        if(status.equals("停止")) {
            BuildTask(sql_task);
            if(sql_task.getType().equals("持续运行")) sql_task.setStatus("运行");
        }else{
            ThreadPool.remove(sql);
            sql_task.setStatus("停止");
        }
        sqlTaskMapper.updateByPrimaryKeySelective(sql_task);
    }

    @Override
    public void UpdateStatus() {
        sqlTaskMapper.update();
    }
}
