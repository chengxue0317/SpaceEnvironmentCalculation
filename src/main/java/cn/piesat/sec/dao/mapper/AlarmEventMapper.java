package cn.piesat.sec.dao.mapper;

import cn.piesat.sec.model.entity.AlarmEventDO;
import cn.piesat.sec.model.entity.SecAlarmForecastDO;
import cn.piesat.sec.model.vo.AlarmEventVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 太阳质子事件
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-08 23:02:08
 */
@Mapper
public interface AlarmEventMapper extends BaseMapper<AlarmEventDO> {
    /**
     * 数据总条数
     *
     * @param tableName 表名称
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param level     警报级别
     * @return 总条数
     */
    Long getAlarmEventDataCount(@Param("tableName") String tableName,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime,
        @Param("level") String level);

    /**
     * 获取警报数据列表
     *
     * @param type      警报事件类型 如：X射线耀斑事件
     * @param tableName 表名称
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param level     警报级别
     * @param offset    数据偏移量
     * @param pageSize  数据每页条数
     * @return 数据集合
     */
    List<AlarmEventDO> getAlarmEventDataList(@Param("type") String type,
        @Param("tableName") String tableName,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime,
        @Param("level") String level,
        @Param("offset") long offset,
        @Param("pageSize") int pageSize);

    /**
     * 数据总条数
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param level     警报级别
     * @return 总条数
     */
    Long getAlarmEventsDataCount(
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime,
        @Param("level") String level);

    /**
     * 获取警报数据列表
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param level     警报级别
     * @param offset    数据偏移量
     * @param pageSize  数据每页条数
     * @return 数据集合
     */
    List<AlarmEventDO> getAlarmEventsDataList(
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime,
        @Param("level") String level,
        @Param("offset") long offset,
        @Param("pageSize") int pageSize);

    /**
     * 获取当天的警报事件
     *
     * @param tableName 表名称
     * @return 指定表当天的警报事件
     */
    List<AlarmEventDO> getTodayAlarmEvent(@Param("tableName") String tableName);

    /**
     * 查询未来三天预报事件
     *
     * @return 未来三天预报事件数据
     */
    List<SecAlarmForecastDO> getAlarmEvent3daysForecast();
}
