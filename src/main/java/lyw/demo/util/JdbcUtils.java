package lyw.demo.util;

import lombok.extern.slf4j.Slf4j;
import lyw.demo.pojo.Db_Connection;
import net.sf.jsqlparser.JSQLParserException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class JdbcUtils {

    public static void getResultSet(Connection connection,String sql) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
        close(null,preparedStatement,resultSet);
    }

    public static void execute(Connection connection,String sql) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.executeUpdate();
        close(null,preparedStatement,null);
    }

    public static void execute(Connection connection,String sql,List<Object> list) throws SQLException,RuntimeException {
        String s = null;
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for(int i = 1;i <= list.size();++i){
//            preparedStatement.setString(i,s = list.get(i-1)==null ? null : list.get(i-1).toString());
            preparedStatement.setObject(i,list.get(i-1)==null ? null : list.get(i-1));
        }
        preparedStatement.executeUpdate();
        close(null,preparedStatement,null);
    }


    public static void close(Connection connection,PreparedStatement preparedStatement,ResultSet resultSet) throws SQLException {
        if(resultSet != null) resultSet.close();
        if(preparedStatement != null) preparedStatement.close();
        if(connection != null) connection.close();
    }
}
