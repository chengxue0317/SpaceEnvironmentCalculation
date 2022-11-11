package cn.piesat.sec.model.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 太阳警报事件Query
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-08 23:02:08
 */
@Data
@ApiModel("太阳质子事件查询对象")
public class AlarmEventQuery {
    /**
     * 警报类型
     */
    @ApiModelProperty("开始时间")
    private LocalDateTime startTime;
    /**
     * 警报类型
     */
    @ApiModelProperty("结束时间")
    private LocalDateTime endTime;
    /**
     * 警报类型
     */
    @ApiModelProperty("警报类型")
    private String type;
    /**
     * 警报类型
     */
    @ApiModelProperty("警报警报级别")
    private String[] level;
}
