package cn.piesat.sec.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 太阳质子事件
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-21 15:52:18
 */
@Data
@TableName("SEC_PROTON_ALARM")
@ApiModel("太阳质子事件实体类")
public class SecProtonAlarmDO implements Serializable {
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
    * 持续警报内容
    */
    @ApiModelProperty("持续警报内容")
    private String content;
    /**
    * 警报级别0~4分5个等级
    */
    @ApiModelProperty("警报级别0~4分5个等级")
    private Integer level;
    /**
    * 警报综述
    */
    @ApiModelProperty("警报综述")
    private String overview;

    /**
     * 警报类型
     */
    @ApiModelProperty("警报类型")
    private String type;
}
