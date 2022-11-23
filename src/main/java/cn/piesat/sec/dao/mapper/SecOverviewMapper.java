package cn.piesat.sec.dao.mapper;

import cn.piesat.sec.model.vo.SecOverviewVO;
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
public interface SecOverviewMapper {
    List<SecOverviewVO> getPeriodOverview(@Param("tableName") String tableName,
                                          @Param("startTime") String startTime,
                                          @Param("endTime") String endTime) throws Exception;
}
