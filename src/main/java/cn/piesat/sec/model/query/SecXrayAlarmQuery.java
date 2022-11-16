package cn.piesat.sec.model.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 太阳X射线耀斑警报事件Query
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-12 14:46:40
 */
@Data
@ApiModel("太阳X射线耀斑警报事件查询对象")
public class SecXrayAlarmQuery {
    /**
     * 实际警报出现时间(UTC+8)
     */
    @ApiModelProperty("实际警报出现时间(UTC+8)")
    private LocalDateTime thresholdTime;
    /**
     * 警报级别0~5分6个等级
     */
    @ApiModelProperty("警报级别0~5分6个等级")
    private Integer level;
}
