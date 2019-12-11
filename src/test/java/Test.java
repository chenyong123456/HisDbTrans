import lyw.demo.Application;
import lyw.demo.service.SqlTaskService;
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
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.StringReader;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
public class Test {
    @Autowired
    private SqlTaskService sqlTaskService;

    @org.junit.Test
    public void test() {
//        StringUtil.concatSql("Select value,time from test_table where id = 1");
    }

    @org.junit.Test
    public void test1() throws JSQLParserException {
//        sqlTaskService.parseTable("select * from info_data as d left outer join info_type as t on d.id = t.id");
    }

    @org.junit.Test
    public void test2() {
        Map<String, Object> map = StringUtil.parseJson("{\n" +
                "    \"table\": \"test_table\",\n" +
                "    \"primary\": \"id\"\n" +
                "}");

        for (Map.Entry entry : map.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
    }

    @org.junit.Test
    public void test3() throws JSQLParserException {
        String sql = "select t.column_name,t.column_type from information_schema.columns as t where table_schema='test' and table_name = 'info_data'";

        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        Select select = (Select) parserManager.parse(new StringReader(sql));
        PlainSelect plain = (PlainSelect) select.getSelectBody();
        List<SelectItem> selectitems = plain.getSelectItems();


        Statement statement = CCJSqlParserUtil.parse(sql);
        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
        List<String> list = tablesNamesFinder.getTableList(statement);
        String[] ss = list.toArray(new String[0]);


        selectitems.forEach(selectItem -> System.out.println(selectItem.toString()));

        System.out.println(Arrays.toString(ss));

    }

}