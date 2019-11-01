package lyw.demo.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import lyw.demo.pojo.Db_Connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class StringUtil {

    public static String[] concatSql(String sql){
        sql = sql.toLowerCase();

        String str = sql.substring(sql.indexOf("select") + 6,sql.indexOf("from")).trim();

        if(str.equals("*")){

        }

        String[] ss = str.split(",");



        return ss;
    }

    public static String concatUrl(Db_Connection db_connection){

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

    public static Map<String,Object> parseJson(String jsonStr){
        JSONObject object = JSONObject.parseObject(jsonStr);

        HashMap<String,Object> map = new HashMap<>();

        for(Map.Entry entry : object.entrySet()){
            map.put(entry.getKey().toString(),entry.getValue());
        }

        return map;
    }
}
