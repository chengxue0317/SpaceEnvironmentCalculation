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
 * 太阳质子事件VO
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-08 23:02:08
 */
@Data
@ApiModel("太阳质子事件VO")
public class ProtonAlarmVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID主键
     */
    @TableId(type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty("ID主键")
    private Long id;

    /**
     * 单位标识
     */
    @ApiModelProperty("单位标识")
    private String unitId;

    /**
     * 设备标识
     */
    @ApiModelProperty("设备标识")
    private String deviceId;

    /**
     * 台站标识
     */
    @ApiModelProperty("台站标识")
    private String staId;

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
     * 警报内容
     */
    @ApiModelProperty("警报内容")
    private String content;

    /**
     * 警报级别0~4分5个等级
     */
    @ApiModelProperty("警报级别0~4分5个等级")
    private Integer level;
}
