package lyw.demo.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class StringUtil {

    public static String[] concatSql(String sql){
        sql = sql.toLowerCase();

        String str = sql.substring(sql.indexOf("select") + 6,sql.indexOf("from")).trim();

        String[] ss = str.split(",");

        return ss;
    }

    public static String concatUrl(String db_type,String host,Integer port,String db_name){

        String url = "";

        url += "jdbc:";

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
