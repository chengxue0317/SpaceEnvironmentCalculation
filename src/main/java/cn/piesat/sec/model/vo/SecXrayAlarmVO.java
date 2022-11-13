package cn.piesat.sec.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 太阳X射线耀斑警报事件VO
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-12 14:46:40
 */
@Data
@ApiModel("太阳X射线耀斑警报事件VO")
public class SecXrayAlarmVO implements Serializable {
    private static final long serialVersionUID = 1L;

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
     * 持续警报内容
     */
    @ApiModelProperty("持续警报内容")
    private String content;

    /**
     * 警报级别0~5分6个等级
     */
    @ApiModelProperty("警报级别0~5分6个等级")
    private Integer level;

    /**
     * 警报综述
     */
    @ApiModelProperty("警报综述")
    private String overview;
}
