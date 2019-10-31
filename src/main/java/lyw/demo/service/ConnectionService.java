package lyw.demo.service;

import lyw.demo.pojo.Db_Connection;

import java.util.List;

public interface ConnectionService {

    /**
     * 检查连接信息是否正确
     * @param db_connection 数据库连接信息
     * @return 正确返回true 错误返回false
     */
    boolean checkConnection(Db_Connection db_connection);


    /**
     * 保存此连接信息
     * @param db_connection 数据库连接信息
     * @return 保存正确返回true 如果名字已被使用返回false
     */
    boolean keepConnection(Db_Connection db_connection);

    List<Db_Connection> getAll();

    void update(Db_Connection db_connection);

    /**
     * 根据连接名称查找数据库连接
     * @param name 连接名称
     * @return 返回数据库连接 若不存在 返回 null
     */
    Db_Connection getByName(String name);
}
