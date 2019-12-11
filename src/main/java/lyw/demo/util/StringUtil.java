package lyw.demo.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import lyw.demo.pojo.Db_Connection;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.util.TablesNamesFinder;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class StringUtil {

    public static Map<String,Object> parseJson(String jsonStr){
        JSONObject object = JSONObject.parseObject(jsonStr);

        HashMap<String,Object> map = new HashMap<>();

        for(Map.Entry entry : object.entrySet()){
            map.put(entry.getKey().toString(),entry.getValue());
        }

        return map;
    }
}
