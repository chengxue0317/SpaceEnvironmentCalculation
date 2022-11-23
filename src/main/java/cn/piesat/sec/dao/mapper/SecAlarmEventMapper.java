package cn.piesat.sec.dao.mapper;

import cn.piesat.sec.model.entity.SecProtonAlarmDO;
import cn.piesat.sec.model.entity.SecAlarmForecastDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 太阳质子事件
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-08 23:02:08
 */
@Mapper
public interface SecAlarmEventMapper extends BaseMapper<SecProtonAlarmDO> {
    /**
     * 数据总条数
     *
     * @param tableName 表名称
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param level     警报级别
     * @return 总条数
     */
    Long getAlarmEventDataCount(@Param("tableName") String tableName, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("level") String level);

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
    List<SecProtonAlarmDO> getAlarmEventDataList(@Param("type") String type, @Param("tableName") String tableName, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("level") String level, @Param("offset") long offset, @Param("pageSize") int pageSize);

    /**
     * 数据总条数
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param level     警报级别
     * @return 总条数
     */
    Long getAlarmEventsDataCount(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("level") String level);

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
    List<SecProtonAlarmDO> getAlarmEventsDataList(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("level") String level, @Param("offset") long offset, @Param("pageSize") int pageSize);

    /**
     * 获取当天的警报事件
     *
     * @param tableName 表名称
     * @return 指定表当天的警报事件
     */
    List<SecProtonAlarmDO> getTodayAlarmEvent(@Param("tableName") String tableName);

    /**
     * 查询未来三天预报事件
     *
     * @return 未来三天预报事件数据
     */
    List<SecAlarmForecastDO> getAlarmEvent3daysForecast();

    /**
     * 获取四种警报事件
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 四种警报事件结果
     */
    Map<String, Object> getFourthAlramEventsBef24h(@Param("startTime") String startTime, @Param("endTime") String endTime) throws Exception;

    /**
     * 更新报文路径
     *
     * @param time 时间
     * @param path 文件相对路径
     * @return 四种警报事件结果
     */
    int updatePath(@Param("time") String time, @Param("path") String path, @Param("type") String type);

    /**
     * 获取警报事件统计信息
     *
     * @param tableName 警报事件表
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 警报分类统计信息
     */
    List<SecProtonAlarmDO> getAlarmEventCount(@Param("tableName") String tableName, @Param("startTime") String startTime, @Param("endTime") String endTime);
}
