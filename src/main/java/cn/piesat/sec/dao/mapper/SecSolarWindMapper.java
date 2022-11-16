package cn.piesat.sec.dao.mapper;

import cn.piesat.sec.model.entity.SecF107FluxDO;
import cn.piesat.sec.model.entity.SecSolarWindDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 太阳风速
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 21:11:31
 */
@Mapper
public interface SecSolarWindMapper extends BaseMapper<SecSolarWindDO> {
    /**
     * 根据时间筛选太阳风速数据
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 太阳风速数据
     * @throws Exception
     */
    List<SecSolarWindDO> getSolarWindData(@Param("startTime") String startTime,
        @Param("endTime") String endTime) throws Exception;
}
