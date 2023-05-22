package cn.piesat.sec.model.dto;

import cn.piesat.kjyy.common.web.annotation.validator.group.AddGroup;
import cn.piesat.kjyy.common.web.annotation.validator.group.UpdateGroup;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 卫星基本信息DTO
 *
 * @author sw
 * @email shiwang@piesat.cn
 * @date 2022-11-14 16:35:45
 */
@Data
@ApiModel("卫星基本信息DTO")
public class SdcResourceSatelliteDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键自增
    */
    @NotNull(message = "主键不能为空", groups = UpdateGroup.class)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty("主键自增")
    private Long id;
    /**
     * 卫星标识
    */
    @ApiModelProperty("卫星标识")
    @NotBlank(message = "卫星标识不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 50 , message = "长度必须小于等于50" ,groups ={AddGroup.class,UpdateGroup.class} )
    private String satId;
    /**
     * 卫星名称
    */
    @ApiModelProperty("卫星名称")
    @Length(max = 200 , message = "长度必须小于等于200" ,groups ={AddGroup.class,UpdateGroup.class} )
    private String satName;
    /**
     * 卫星PRN号
    */
    @ApiModelProperty("卫星PRN号")
    @Length(max = 50 , message = "长度必须小于等于50" ,groups ={AddGroup.class,UpdateGroup.class} )
    private String satPrn;
    /**
     * 卫星状态：1-入轨，2-入网，3-离网，4-离轨
    */
    @ApiModelProperty("卫星状态：1-入轨，2-入网，3-离网，4-离轨")
    private Integer satStatus;
    /**
     * 发射时间
    */
    @ApiModelProperty("发射时间")
    @Length(max = 50 , message = "长度必须小于等于50" ,groups ={AddGroup.class,UpdateGroup.class} )
    private String sendTime;
    /**
     * 轨道类型(1-倾斜轨，2-近极轨，3-高轨)
    */
    @ApiModelProperty("轨道类型(1-倾斜轨，2-近极轨，3-高轨)")
    private Integer orbitalType;
    /**
     * 轨道面号
    */
    @ApiModelProperty("轨道面号")
    @Length(max = 50 , message = "长度必须小于等于50" ,groups ={AddGroup.class,UpdateGroup.class} )
    private String orbitalPlaid;
    /**
     * 节点编号
    */
    @ApiModelProperty("节点编号")
    @Length(max = 50 , message = "长度必须小于等于50" ,groups ={AddGroup.class,UpdateGroup.class} )
    private String orbitalPlaNodeId;
    /**
     * 太阳光反射系数
    */
    @ApiModelProperty("太阳光反射系数")
    @Length(max = 50 , message = "长度必须小于等于50" ,groups ={AddGroup.class,UpdateGroup.class} )
    private String cd;
    /**
     * 卫星质量
    */
    @ApiModelProperty("卫星质量")
    private Double weight;
    /**
     * 敏感器几何特性
    */
    @ApiModelProperty("敏感器几何特性")
    @Length(max = 50 , message = "长度必须小于等于50" ,groups ={AddGroup.class,UpdateGroup.class} )
    private String sdGeo;
    /**
     * 迎风面积
    */
    @ApiModelProperty("迎风面积")
    private Double windArea;
    /**
     * 尺寸信息，单位米
    */
    @ApiModelProperty("尺寸信息，单位米")
    private Double size;
    /**
     * 卫星材质
    */
    @ApiModelProperty("卫星材质")
    @Length(max = 50 , message = "长度必须小于等于50" ,groups ={AddGroup.class,UpdateGroup.class} )
    private String material;
    /**
     * 载荷数量
    */
    @ApiModelProperty("载荷数量")
    private Integer sensorNum;
}
