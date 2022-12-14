package cn.piesat.sec.model.dto;

import cn.piesat.kjyy.common.mybatisplus.annotation.validator.group.AddGroup;
import cn.piesat.kjyy.common.mybatisplus.annotation.validator.group.UpdateGroup;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 测站基本信息DTO
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-12-13 14:18:47
 */
@Data
@ApiModel("测站基本信息DTO")
public class SdcResourceStationDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
    */
    @NotNull(message = "主键不能为空", groups = UpdateGroup.class)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty("主键")
    private Long id;
    /**
     * 地面站标识
    */
    @ApiModelProperty("地面站标识")
    @NotBlank(message = "地面站标识不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 50 , message = "长度必须小于等于50" ,groups ={AddGroup.class,UpdateGroup.class} )
    private String staId;
    /**
     * 地面站名称
    */
    @ApiModelProperty("地面站名称")
    @Length(max = 50 , message = "长度必须小于等于50" ,groups ={AddGroup.class,UpdateGroup.class} )
    private String staName;
    /**
     * 地面站类型：1-低轨信关站，2-高轨信关站，3-高轨测控站，4-轻型信关站，5-地面监测站，6-激光标校站。
    */
    @ApiModelProperty("地面站类型：1-低轨信关站，2-高轨信关站，3-高轨测控站，4-轻型信关站，5-地面监测站，6-激光标校站。")
    private Integer staType;
    /**
     * 状态：0-可用，1-不可用。
    */
    @ApiModelProperty("状态：0-可用，1-不可用。")
    private Integer status;
    /**
     * 天线数量
    */
    @ApiModelProperty("天线数量")
    private Integer antennaNum;
    /**
     * 地理经度
    */
    @ApiModelProperty("地理经度")
    private Double longitude;
    /**
     * 地理纬度
    */
    @ApiModelProperty("地理纬度")
    private Double latitude;
    /**
     * 海拔高度
    */
    @ApiModelProperty("海拔高度")
    private Double elevation;
    /**
     * 部署地点
    */
    @ApiModelProperty("部署地点")
    @Length(max = 50 , message = "长度必须小于等于50" ,groups ={AddGroup.class,UpdateGroup.class} )
    private String location;
}
