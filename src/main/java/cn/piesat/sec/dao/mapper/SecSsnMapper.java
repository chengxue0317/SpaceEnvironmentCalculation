package cn.piesat.sec.dao.mapper;

import cn.piesat.sec.model.entity.SecSsnDO;
import cn.piesat.sec.model.query.SecSsnQuery;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 太阳黑子数
 * 
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-13 20:02:03
 */
@Mapper
public interface SecSsnMapper extends BaseMapper<SecSsnDO> {
    /**
     * 获取太阳黑子数据
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 太阳黑子数据
     */
    List<SecSsnDO> getSunSpotData(@Param("startTime") String startTime, @Param("endTime") String endTime) throws Exception;
}
