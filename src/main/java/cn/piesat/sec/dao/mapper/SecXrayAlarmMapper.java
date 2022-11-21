package cn.piesat.sec.dao.mapper;

import cn.piesat.sec.model.entity.SecXrayAlarmDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 太阳X射线耀斑警报事件
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-12 14:46:40
 */
@Mapper
public interface SecXrayAlarmMapper extends BaseMapper<SecXrayAlarmDO> {
    /**
     * 获取X射线耀斑警报数据
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 太阳黑子数据
     */
    List<SecXrayAlarmDO> getXrayAlarmList(@Param("startTime") String startTime, @Param("endTime") String endTime) throws Exception;
}
