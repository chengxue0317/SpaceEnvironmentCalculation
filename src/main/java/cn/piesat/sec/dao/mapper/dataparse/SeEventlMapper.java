package cn.piesat.sec.dao.mapper.dataparse;

import cn.piesat.sec.model.vo.dataparse.SecEventsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 警报事件
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-13 20:02:03
 */
@Mapper
public interface SeEventlMapper {
    int save(@Param("data") SecEventsVO data, @Param("tableName") String tableName) throws Exception;
}
