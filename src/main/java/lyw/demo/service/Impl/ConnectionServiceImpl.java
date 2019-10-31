package lyw.demo.service.Impl;

import lombok.extern.slf4j.Slf4j;
import lyw.demo.mapper.DbConnectionMapper;
import lyw.demo.pojo.Db_Connection;
import lyw.demo.service.ConnectionService;
import lyw.demo.util.JdbcUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Service
@Slf4j
public class ConnectionServiceImpl implements ConnectionService {

    
    @Autowired
    private DbConnectionMapper dbConnectionMapper;

    @Override
    public boolean checkConnection(Db_Connection db_connection) {
        Connection connection = null;
        try {
            connection = JdbcUtils.getConnection(db_connection);
        } catch (SQLException e) {
            e.printStackTrace();
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                log.error(stackTraceElement.toString());
            }
            return false;
        }finally {
            if(connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                        log.error(stackTraceElement.toString());
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean keepConnection(Db_Connection db_connection) {
        if(dbConnectionMapper.selectByPrimaryKey(db_connection.getName()) != null) return false;

        dbConnectionMapper.insertSelective(db_connection);

        return true;
    }

    @Override
    public List<Db_Connection> getAll() {
        List<Db_Connection> list = dbConnectionMapper.selectAll();
        return list;
    }

    @Override
    public void update(Db_Connection db_connection) {
        dbConnectionMapper.updateByPrimaryKeySelective(db_connection);
    }

    @Override
    public Db_Connection getByName(String name) {
        Db_Connection db_connection = dbConnectionMapper.selectByPrimaryKey(name);
        return db_connection;
    }
}
