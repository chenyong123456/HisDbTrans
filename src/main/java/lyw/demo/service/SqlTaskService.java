package lyw.demo.service;

import lyw.demo.pojo.Db_Connection;
import lyw.demo.pojo.Sql_Task;

import java.sql.Connection;
import java.sql.SQLException;

public interface SqlTaskService {

    /**
     * 检查sql 是否正确
     * @param db_connection 数据库连接
     * @param sql 用户输入sql
     * @return
     */
    boolean CheckSqlRight(Db_Connection db_connection,String sql) throws SQLException;

    /**
     * 通过sql_task生成定时任务task
     * @param sql_task
     * @return
     */
    Task BuildTask(Sql_Task sql_task);

    /**
     * 检查table 是否正确
     * @param table table名
     * @param db_connection source_connection
     * @return 表存在返回false
     */
    boolean CheckTableRight(String table,Db_Connection db_connection) throws SQLException;

    String Insert(Sql_Task sql_task);

    /**
     * 根据sql以及表名生成一个新表
     * @param db_connection
     * @param table
     * @param sql
     */
    void createTable(Db_Connection db_connection,String table,String sql) throws SQLException;
}
