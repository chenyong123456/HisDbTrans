import lyw.demo.Application;
import lyw.demo.service.SqlTaskService;
import lyw.demo.util.JdbcUtils;
import lyw.demo.util.StringUtil;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
public class Test {
    @Autowired
    private SqlTaskService sqlTaskService;

    @org.junit.Test
    public void test() {
        StringUtil.concatSql("Select value,time from test_table where id = 1");
    }

    @org.junit.Test
    public void test1() {
//        System.out.println(StringUtil.concatUrl("sqlServer", "localhost", 3306, "test"));
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

}