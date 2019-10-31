package lyw.demo.pojo;

import lombok.Data;

import java.sql.Connection;
import java.util.List;

@Data
public class Sql_Task {

    private String sql;

    private Db_Connection source_conn;

    private Db_Connection target_conn;

    private String type;

    private String new_table;

}
