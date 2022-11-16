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
 * 太阳X射线流量
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 12:16:14
 */
@Data
@TableName("SEC_XRAY_FLUX")
@ApiModel("太阳X射线流量实体类")
public class SecXrayFluxDO implements Serializable {
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
    * 卫星标识
    */
    @ApiModelProperty("卫星标识")
    private String satId;
    /**
    * 0.1~0.8毫微米通道流量
    */
    @ApiModelProperty("0.1~0.8毫微米通道流量")
    private Double longer;
    /**
    * 0.05~0.4毫微米通道流量
    */
    @ApiModelProperty("0.05~0.4毫微米通道流量")
    private Double shorter;
    /**
    * 时间
    */
    @ApiModelProperty("时间")
    private LocalDateTime time;
}
