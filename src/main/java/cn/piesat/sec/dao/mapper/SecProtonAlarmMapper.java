package cn.piesat.sec.dao.mapper;

import cn.piesat.sec.model.entity.SecProtonAlarmDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 太阳质子事件
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-21 15:52:18
 */
@Mapper
public interface SecProtonAlarmMapper extends BaseMapper<SecProtonAlarmDO> {
    /**
     * 获取太阳质子事件
     * @param startTime 开始事件
     * @param endTime 结束时间
     * @return 太阳质子事件
     */
    List<SecProtonAlarmDO> getSecProton(@Param("startTime") String startTime, @Param("endTime") String endTime);
}
