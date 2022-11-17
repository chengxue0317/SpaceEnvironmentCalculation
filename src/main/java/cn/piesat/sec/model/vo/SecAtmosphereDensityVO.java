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
 * 大气密度预报模块VO
 *
 * @author sw
 * @email shiwang@piesat.cn
 * @date 2022-11-16 15:07:53
 */
@Data
@ApiModel("大气密度预报模块VO")
public class SecAtmosphereDensityVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID主键
     */
    @TableId(type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty("ID主键")
    private Integer id;

    /**
     * 卫星标识
     */
    @ApiModelProperty("卫星标识")
    private String satId;

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
     * 时间（UTC）
     */
    @ApiModelProperty("时间（UTC）")
    private LocalDateTime time;

    /**
     * 经度（°）
     */
    @ApiModelProperty("经度（°）")
    private Double lon;

    /**
     * 纬度（°）
     */
    @ApiModelProperty("纬度（°）")
    private Double lat;

    /**
     * 高度（KM）
     */
    @ApiModelProperty("高度（KM）")
    private Double ele;

    /**
     * 大气密度（KG/M3）
     */
    @ApiModelProperty("大气密度（KG/M3）")
    private Double density;
}
