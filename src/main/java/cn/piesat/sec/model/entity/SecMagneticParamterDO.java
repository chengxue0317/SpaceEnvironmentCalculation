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
 * 地磁参数数据
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 21:52:42
 */
@Data
@TableName("SEC_MAGNETIC_PARAMTER")
@ApiModel("地磁参数数据实体类")
public class SecMagneticParamterDO implements Serializable {
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
    * 总磁场
    */
    @ApiModelProperty("总磁场")
    private Double bT;
    /**
    * 磁子午线
    */
    @ApiModelProperty("磁子午线")
    private Double bH;
    /**
    * 磁偏角
    */
    @ApiModelProperty("磁偏角")
    private Double bD;
    /**
    * 垂直分量
    */
    @ApiModelProperty("垂直分量")
    private Double bZ;
    /**
    * 经度
    */
    @ApiModelProperty("经度")
    private Double lon;
    /**
    * 纬度
    */
    @ApiModelProperty("纬度")
    private Double lat;
    /**
    * 时间
    */
    @ApiModelProperty("时间")
    private LocalDateTime time;
}
