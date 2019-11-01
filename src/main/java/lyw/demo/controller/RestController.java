package lyw.demo.controller;

import lombok.extern.slf4j.Slf4j;
import lyw.demo.pojo.Db_Connection;
import lyw.demo.pojo.Sql_Task;
import lyw.demo.service.ConnectionService;
import lyw.demo.service.SqlTaskService;
import lyw.demo.util.JdbcUtils;
import lyw.demo.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@org.springframework.web.bind.annotation.RestController
@CrossOrigin
@Slf4j
public class RestController {

    @Autowired
    private ConnectionService connectionService;

    @Autowired
    private SqlTaskService sqlTaskService;

    /**
     *  验证数据库是否能够正确连接 </br>
     *  method: post</br>
     *  param: name:数据库连接名,db_name：数据库名,host：主机名,port：端口号      ,username：用户名,password：密码,db_type：数据库类型
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
     * @param conn_name 数据库连接名
     * @param sql sql语句
     * @return sql正确则返回“sql正确” sql错误则返回“sql错误”
     */
    @PostMapping("sql")
    public String submitSql(String conn_name, String sql) throws SQLException {
        Db_Connection db_connection = connectionService.getByName(conn_name);
        System.out.println(db_connection.toString());

        if (!sqlTaskService.CheckSqlRight(db_connection, sql)) return "sql错误";
        return "sql正确";
    }

    /**
     * 提交任务 </br>
     * method：post
     * @param sql 任务sql语句
     * @param type 任务执行类型
     * @param source_name   源数据库连接名
     * @param target_name   目标数据库连接名
     * @param jsonStr      包含 新建表名和主键
     * @return 若表不存在 则返回“表不存在” 若新建表失败 “返回新建表失败” 若成功 则返回“任务开始运行”
     */
    @PostMapping(value = "task")
    public String submitTask(@RequestParam String sql,
                             @RequestParam String type,
                             @RequestParam String source_name,
                             @RequestParam String target_name,
                             @RequestParam String jsonStr) {
        Sql_Task sql_task = new Sql_Task();
        sql_task.setSql(sql);
        sql_task.setType(type);
        sql_task.setSource_conn(connectionService.getByName(source_name));

        Db_Connection db_connection = connectionService.getByName(target_name);
        sql_task.setTarget_conn(db_connection);

        Map<String,Object> map = StringUtil.parseJson(jsonStr);

        String new_table = map.get("table").toString();

        sql_task.setNew_table(new_table);

        try {
            if(sqlTaskService.CheckTableRight(new_table,db_connection)) {
                sqlTaskService.createTable(db_connection, new_table, sql);
                sqlTaskService.BuildTask(sql_task);
                sqlTaskService.Insert(sql_task);
            }else return "表已存在";
        } catch (SQLException e) {
            e.printStackTrace();
            return "新建表失败";
        }
        return "任务开始运行";
    }

    /**
     * 返回所有数据库连接信息</br>
     * method: get
     * @return 数据库连接数组
     */
    @GetMapping("Conn")
    public List<Db_Connection> getAllConn(){
        List<Db_Connection> list = connectionService.getAll();
        return list;
    }
}
