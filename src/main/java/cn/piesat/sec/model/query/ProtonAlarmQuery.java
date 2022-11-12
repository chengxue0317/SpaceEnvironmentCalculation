package cn.piesat.sec.model.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import cn.piesat.kjyy.common.mybatisplus.annotation.query.Where;
import cn.piesat.kjyy.common.mybatisplus.model.entity.Between;
import cn.piesat.kjyy.common.mybatisplus.annotation.query.OrderBy;
import cn.piesat.kjyy.common.mybatisplus.model.enums.Condition;
import java.time.LocalDateTime;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.util.List;
import lombok.Data;

/**
 * 太阳质子事件Query
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-08 23:02:08
 */
@Data
@ApiModel("太阳质子事件查询对象")
public class ProtonAlarmQuery {
    /**
     * 预报时间(UTC+8)
     */
    @ApiModelProperty("预报时间(UTC+8)")
    private LocalDateTime publishTime;
    /**
     * 实际警报出现时间(UTC+8)
     */
    @ApiModelProperty("实际警报出现时间(UTC+8)")
    private LocalDateTime thresholdTime;
    /**
     * 警报类型
     */
    @ApiModelProperty("警报类型")
    private String type;
    /**
     * 警报级别0~4分5个等级
     */
    @ApiModelProperty("警报级别0~4分5个等级")
    private String[] level;
}