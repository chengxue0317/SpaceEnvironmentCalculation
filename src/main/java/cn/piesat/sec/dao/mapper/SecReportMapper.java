package cn.piesat.sec.dao.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 报文综合数据
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-13 20:02:03
 */
@Mapper
public interface SecReportMapper {
    /**
     * 获取一段时间内的数据组合
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> getCombinData(@Param("startTime") String startTime, @Param("endTime") String endTime) throws Exception;
}
