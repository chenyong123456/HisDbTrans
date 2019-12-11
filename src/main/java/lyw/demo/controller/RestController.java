package lyw.demo.controller;

import lombok.extern.slf4j.Slf4j;
import lyw.demo.pojo.Db_Connection;
import lyw.demo.pojo.Sql_Task;
import lyw.demo.service.ConnectionService;
import lyw.demo.service.SqlTaskService;
import net.sf.jsqlparser.JSQLParserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@org.springframework.web.bind.annotation.RestController
@CrossOrigin
@Slf4j
public class RestController {

    @Autowired
    private ConnectionService connectionService;

    @Autowired
    private SqlTaskService sqlTaskService;

    /**
     * 验证数据库是否能够正确连接 </br>
     * method: post</br>
     * param: name:数据库连接名,db_name：数据库名,host：主机名,port：端口号      ,username：用户名,password：密码,db_type：数据库类型
     *
     * @param db_connection 数据库连接信息
     * @return 连接成功 返回 “连接成功” 连接失败 返回 “连接失败"
     */
    @PostMapping("Conn")
    public String submitSourceConn(Db_Connection db_connection) {

        if (!connectionService.checkConnection(db_connection)) return "连接失败";

        connectionService.keepConnection(db_connection);
        return "连接成功";
    }


    /**
     * 检验sql语句是否正确 </br>
     * method:post
     *
     * @param { "t_sql": "select  date,value,age from test_table",
     *          "source_name": "lyw-sqlServer",
     *          "target_name": "lyw",
     *          "columns": [
     *          {
     *          "column_name": "date",
     *          "column_type": "date"
     *          },
     *          {
     *          "column_name": "name",
     *          "column_type": "varchar(255)"
     *          },
     *          {
     *          "column_name": "age",
     *          "column_type": "bigint(20)"
     *          }
     *          ]
     *          }
     * @return sql正确则返回“sql正确” sql错误则返回“sql错误”
     */
    @PostMapping(value = "sql", produces = "application/json")
    public String submitSql(@RequestBody Sql_Task sql_task) {
        Db_Connection db_connection = connectionService.getByName(sql_task.getSource_name());
        sql_task.setSource_conn(db_connection);
        System.out.println(db_connection.toString());
        System.out.println(sql_task.getT_sql());
        try{
            if (!sqlTaskService.CheckSqlRight(sql_task)) return "sql错误";
            return "sql正确";
        }catch (SQLException | JSQLParserException e) {
            return "sql错误";
        }


    }

    /**
     * 提交任务 </br>
     * method：post
     *
     * @param { "t_sql": "select  date,value,age from test_table",
     *            "type": "运行一次",
     *            "source_name": "lyw-sqlServer",
     *            "target_name": "lyw",
     *            "columns": [
     *            {
     *            "column_name": "date",
     *            "column_type": "date"
     *            },
     *            {
     *            "column_name": "name",
     *            "column_type": "varchar(255)"
     *            },
     *            {
     *            "column_name": "age",
     *            "column_type": "bigint(20)"
     *            }
     *            ],
     *            "new_table": "new_data"
     *            }
     * @return 若表不存在 则返回“表不存在” 若新建表失败 “返回新建表失败” 若成功 则返回“任务开始运行”
     */
    @PostMapping(value = "task", produces = "application/json")
    public String submitTask(@RequestBody Sql_Task sql_task) {
        sql_task.setSource_conn(connectionService.getByName(sql_task.getSource_name()));
        Db_Connection db_connection = connectionService.getByName(sql_task.getTarget_name());
        sql_task.setTarget_conn(db_connection);

        String new_table = sql_task.getNew_table();

        sql_task.setNew_table(new_table);

        try {
            if (sqlTaskService.CheckTableRight(sql_task)) {
                sqlTaskService.createTable(sql_task);
                sqlTaskService.BuildTask(sql_task);
                sqlTaskService.Insert(sql_task);
            } else return "表已存在";
        } catch (SQLException | JSQLParserException e) {
            e.printStackTrace();
            return "新建表失败";
        }
        return "任务开始运行";
    }

    /**
     * 返回所有数据库连接信息</br>
     * method: get
     *
     * @return 数据库连接数组
     */
    @GetMapping("Conn")
    public List<Db_Connection> getAllConn() {
        List<Db_Connection> list = connectionService.getAll();
        return list;
    }

    /**
     * 返回所有任务信息</br>
     * method: get
     *
     * @return 任务数组 包含每个任务相应的连接信息
     */
    @GetMapping("task")
    public List<Sql_Task> getAll() {
        return sqlTaskService.getAll();
    }

    /**
     * 改变该任务的状态
     *
     * @param t_sql 该任务提交的sql
     * @return 修改成功 修改失败
     */
    @GetMapping("sql")
    public String changeTask(String t_sql) {
        try {
            sqlTaskService.changeStatus(t_sql);
        } catch (Exception e) {
            e.printStackTrace();
            return "修改失败";
        }
        return "修改成功";
    }

}
