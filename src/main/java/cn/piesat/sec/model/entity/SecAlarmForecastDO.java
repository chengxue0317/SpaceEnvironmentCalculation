package cn.piesat.sec.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 未来三天警报预报表
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-12 15:16:45
 */
@Data
@TableName("SEC_ALARM_FORECAST")
@ApiModel("未来三天警报预报表实体类")
public class SecAlarmForecastDO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
    * ID主键||DO
    */
    @TableId(type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty("ID主键||DO")
    private Long id;
    /**
    * 单位标识||DO
    */
    @ApiModelProperty("单位标识||DO")
    private String unitId;
    /**
    * 设备标识||DO
    */
    @ApiModelProperty("设备标识||DO")
    private String deviceId;
    /**
    * 台站标识||DO
    */
    @ApiModelProperty("台站标识||DO")
    private String staId;
    /**
    * 未来第3天质子事件
    */
    @ApiModelProperty("未来第1天质子事件")
    private String spe1;
    /**
    * 
    */
    @ApiModelProperty("未来第2天质子事件")
    private String spe2;
    /**
    * 
    */
    @ApiModelProperty("未来第3天质子事件")
    private String spe3;
    /**
    * 未来第3天高能电子暴事件
    */
    @ApiModelProperty("未来第1天高能电子暴事件")
    private String ree1;
    /**
    * 
    */
    @ApiModelProperty("未来第2天高能电子暴事件")
    private String ree2;
    /**
    * 
    */
    @ApiModelProperty("未来第3天高能电子暴事件")
    private String ree3;
    /**
    * 未来第3天小地磁暴事件
    */
    @ApiModelProperty("未来第1天小地磁暴事件")
    private String gsmi1;
    /**
    * 
    */
    @ApiModelProperty("未来第2天小地磁暴事件\"")
    private String gsmi2;
    /**
    * 
    */
    @ApiModelProperty("未来第3天小地磁暴事件\"")
    private String gsmi3;
    /**
    * 未来第3天大地磁暴事件
    */
    @ApiModelProperty("未来第1天大地磁暴事件")
    private String gsma1;
    /**
    * 
    */
    @ApiModelProperty("未来第2天大地磁暴事件")
    private String gsma2;
    /**
    * 
    */
    @ApiModelProperty("未来第3天大地磁暴事件")
    private String gsma3;
    /**
    * 未来第1天发生耀斑事件描述
    */
    @ApiModelProperty("未来第1天发生耀斑事件描述")
    private String sxr1;
    /**
    * 未来第2天发生耀斑事件描述
    */
    @ApiModelProperty("未来第2天发生耀斑事件描述")
    private String sxr2;
    /**
    * 未来第3天发生耀斑事件描述
    */
    @ApiModelProperty("未来第3天发生耀斑事件描述")
    private String sxr3;
    /**
    * 时间(UTC+8)
    */
    @ApiModelProperty("时间(UTC+8)")
    private LocalDateTime time;
}
