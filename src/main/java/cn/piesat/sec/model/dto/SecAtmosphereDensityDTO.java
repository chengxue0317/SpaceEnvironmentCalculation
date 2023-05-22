package cn.piesat.sec.model.dto;

import cn.piesat.kjyy.common.web.annotation.validator.group.AddGroup;
import cn.piesat.kjyy.common.web.annotation.validator.group.UpdateGroup;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 大气密度预报模块DTO
 *
 * @author sw
 * @email shiwang@piesat.cn
 * @date 2022-11-16 15:07:53
 */
@Data
@ApiModel("大气密度预报模块DTO")
public class SecAtmosphereDensityDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * ID主键
    */
    @NotNull(message = "主键不能为空", groups = UpdateGroup.class)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty("ID主键")
    private Integer id;
    /**
     * 卫星标识
    */
    @ApiModelProperty("卫星标识")
    @Length(max = 50 , message = "长度必须小于等于50" ,groups ={AddGroup.class,UpdateGroup.class} )
    private String satId;
    /**
     * 单位标识
    */
    @ApiModelProperty("单位标识")
    @Length(max = 10 , message = "长度必须小于等于10" ,groups ={AddGroup.class,UpdateGroup.class} )
    private String unitId;
    /**
     * 设备标识
    */
    @ApiModelProperty("设备标识")
    @Length(max = 10 , message = "长度必须小于等于10" ,groups ={AddGroup.class,UpdateGroup.class} )
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
