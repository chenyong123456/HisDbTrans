package lyw.demo.controller;

import lombok.extern.slf4j.Slf4j;
import lyw.demo.pojo.Db_Connection;
import lyw.demo.pojo.Sql_Task;
import lyw.demo.service.ConnectionService;
import lyw.demo.service.SqlTaskService;
import lyw.demo.util.JdbcUtils;
import lyw.demo.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@org.springframework.web.bind.annotation.RestController
@Slf4j
public class RestController {

    @Autowired
    private ConnectionService connectionService;

    @Autowired
    private SqlTaskService sqlTaskService;

    @RequestMapping("SourceConn")
    public String submitSourceConn(Db_Connection db_connection) {

        if (!connectionService.checkConnection(db_connection)) return "连接失败";

        connectionService.keepConnection(db_connection);
        return "连接成功";
    }

    @RequestMapping("TargetConn")
    public String submitTargetConn(Db_Connection db_connection) {
        if (!connectionService.checkConnection(db_connection)) return "连接失败";

        connectionService.keepConnection(db_connection);
        return "连接成功";
    }

    @RequestMapping("sql")
    public String submitSql(String conn_name, String sql) {
        Db_Connection db_connection = connectionService.getByName(conn_name);
        System.out.println(db_connection.toString());

        if (!sqlTaskService.CheckSqlRight(db_connection, sql)) return "sql错误";
        return "sql正确";
    }

    @RequestMapping(value = "task")
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
}
