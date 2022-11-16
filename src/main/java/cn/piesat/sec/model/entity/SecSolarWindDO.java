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

/**
 * 太阳风速
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 21:11:31
 */
@Data
@TableName("SEC_SOLAR_WIND")
@ApiModel("太阳风速实体类")
public class SecSolarWindDO implements Serializable {
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
    * 太阳风速
    */
    @ApiModelProperty("太阳风速")
    private Double bulkspeed;
    /**
    * 太阳温度
    */
    @ApiModelProperty("太阳温度")
    private Double iontemp;
    /**
    * 太阳密度
    */
    @ApiModelProperty("太阳密度")
    private Double protrondensity;
    /**
    * 时间
    */
    @ApiModelProperty("时间")
    private LocalDateTime time;
}
