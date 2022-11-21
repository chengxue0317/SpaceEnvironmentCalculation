package cn.piesat.sec.dao.mapper;

import cn.piesat.sec.model.vo.SecEnvOverviewVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 获取空间环境预报综述信息
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 17:54:18
 */
@Mapper
public interface SecEnvOverviewMapper {
    List<SecEnvOverviewVO> getEnvOverview() throws Exception;

    List<SecEnvOverviewVO> getWeekOverview(@Param("startTime") String startTime,
                                           @Param("endTime") String endTime) throws Exception;
}
