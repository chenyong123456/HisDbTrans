package lyw.demo.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "db_connection")
public class Db_Connection {
    @Id
    private String name;

    private String db_name;

    private String host;

    private Integer port;

    private String username;

    private String password;

    private String db_type;

}
