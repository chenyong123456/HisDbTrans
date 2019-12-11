package lyw.demo.mapper;

import lyw.demo.pojo.Sql_Task;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

public interface SqlTaskMapper extends Mapper<Sql_Task> {
    @Update("update sql_task set status = '停止' where status = '运行'")
    void update();

}
