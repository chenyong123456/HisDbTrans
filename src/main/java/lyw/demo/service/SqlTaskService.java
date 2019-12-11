package lyw.demo.service;

import lyw.demo.pojo.Db_Connection;
import lyw.demo.pojo.Sql_Task;
import net.sf.jsqlparser.JSQLParserException;

import java.sql.SQLException;
import java.util.List;

public interface SqlTaskService {


    /**
     * 检查sql 是否正确
     * @return
     */
    boolean CheckSqlRight(Sql_Task sql_task) throws SQLException, JSQLParserException;

    /**
     * 通过sql_task生成定时任务task
     * @param sql_task
     * @return
     */
    Task BuildTask(Sql_Task sql_task);

    /**
     * 检查table 是否正确
     * @return 表存在返回false
     */
    boolean CheckTableRight(Sql_Task sql_task) throws SQLException, JSQLParserException;

    String Insert(Sql_Task sql_task);

    /**
     * 创建表
     * @param sql_task
     * @throws SQLException
     * @throws JSQLParserException
     */
    void createTable(Sql_Task sql_task) throws SQLException, JSQLParserException;

    /**
     * 获取所有任务
     * @return
     */
    List<Sql_Task> getAll();

    void changeStatus(String sql) throws Exception;

    /**
     * 修改所有任务的状态
     */
    void UpdateStatus();
}
