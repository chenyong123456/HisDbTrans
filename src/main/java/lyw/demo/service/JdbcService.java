package lyw.demo.service;

import lyw.demo.pojo.Db_Connection;
import lyw.demo.util.JdbcUtils;
import lyw.demo.util.StringUtil;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.util.TablesNamesFinder;

import java.io.StringReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcService {

    protected Connection getConnection(Db_Connection db_connection) throws SQLException {
        String username = db_connection.getUsername();
        String password = db_connection.getPassword();

//        ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();
        Connection connection = DriverManager.getConnection(concatUrl(db_connection),username,password);
        return connection;
    }

    protected List<List<Object>> getResult(Db_Connection db_connection,String sql) throws SQLException, JSQLParserException {
        Connection connection = getConnection(db_connection);

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
        List<List<Object>> objs = new ArrayList<>();
        List<Object> list = null;

        int dataSize = resultSet.getMetaData().getColumnCount();

        while(resultSet.next()){
            list = new ArrayList<>();
            for(int i = 1;i <= dataSize; ++i){
                list.add(resultSet.getObject(i));
            }
            objs.add(list);
        }
        JdbcUtils.close(null,preparedStatement,resultSet);
        return objs;
    }

    protected String[] concatSql(String sql,Db_Connection db_connection) throws JSQLParserException, SQLException {

        Connection connection = getConnection(db_connection);

        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        Select select = (Select) parserManager.parse(new StringReader(sql));
        PlainSelect plain = (PlainSelect) select.getSelectBody();
        List<SelectItem> selectitems = plain.getSelectItems();

        List<String> result = new ArrayList<>();

        if(selectitems.get(0).toString().equals("*")){
            String[] ss = parseTable(sql);
            String s = null;
            for(int i = 0; i < ss.length; ++i){
                if(db_connection.getDb_type().equals("mysql")){
                    s = "select column_name from information_schema.columns where table_name='" + ss[i] + "' and table_schema='" + db_connection.getDb_name() + "'";
                }else if(db_connection.getDb_type().equals("sqlServer")){
                    JdbcUtils.execute(connection,"use " + db_connection.getDb_name());
                    s = "SELECT column_name FROM INFORMATION_SCHEMA.COLUMNS where table_name = '" + ss[i] + "'";
                }else if(db_connection.getDb_type().equals("oracle")){
//                    JdbcUtils.execute(connection,null);
                }

                List<List<Object>> oList = getResult(db_connection,s);
                oList.forEach(list -> {
                    list.forEach(o -> {
                        result.add(o.toString());
                    });
                });
            }
        }else{
            selectitems.forEach(selectItem -> {
                result.add(selectItem.toString());
            });
        }

        String[] ss = result.toArray(new String[0]);

        return ss;
    }

    protected String[] parseTable(String sql) throws JSQLParserException {
        Statement statement = CCJSqlParserUtil.parse(sql);
        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
        List<String> list = tablesNamesFinder.getTableList(statement);
        String[] ss = list.toArray(new String[0]);
        return ss;
    }

    protected String concatUrl(Db_Connection db_connection){

        String url = "";

        url += "jdbc:";

        String db_type = db_connection.getDb_type();
        String host = db_connection.getHost();
        Integer port = db_connection.getPort();
        String db_name = db_connection.getDb_name();

        switch (db_type){
            case "mysql": {
                url += "mysql://";
                url += host;
                url = url + ":" + port;
                url = url + "/" + db_name + "?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT&useSSL=false&autoReconnect=true";
                break;
            }
            case "sqlServer": {
                url += "sqlserver://";
                url += host;
                url = url + ":" + port;
                url = url + ";databaseName=" + db_name;
                break;
            }
            case "oracle": {
                url += "oracle:thin:@//";
                url += host;
                url = url + ":" + port;
                url += "/orcl";
            }
        }
        return url;
    }
}
