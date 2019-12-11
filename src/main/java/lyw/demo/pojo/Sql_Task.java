package lyw.demo.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;


@Data
@Table(name = "sql_task")
public class Sql_Task {
    @Id
    private String t_sql;

    private String source_name;

    private String target_name;

    @Transient
    private Db_Connection source_conn;
    @Transient
    private Db_Connection target_conn;

    private String type;

    private String new_table;

    private String status;

    @Transient
    private List<Column> columns;

}
