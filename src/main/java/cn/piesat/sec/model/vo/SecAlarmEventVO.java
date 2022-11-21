package cn.piesat.sec.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 警报事件VO
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-08 23:02:08
 */
@Data
@ApiModel("警报事件VO")
public class SecAlarmEventVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 警报时间(UTC+8)
     */
    @ApiModelProperty("警报时间(UTC+8)")
    private LocalDateTime thresholdTime;

    /**
     * 警报类别
     */
    @ApiModelProperty("警报类别")
    private String type;

    /**
     * 警报等级
     */
    @ApiModelProperty("警报等级")
    private Integer level = 0;

    /**
     * 警报内容
     */
    @ApiModelProperty("警报内容")
    private String content;

    /**
     * 警报综述
     */
    @ApiModelProperty("警报综述")
    private String overview;
}
