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
 * 卫星基本信息VO
 *
 * @author sw
 * @email shiwang@piesat.cn
 * @date 2022-11-14 16:35:45
 */
@Data
@ApiModel("卫星基本信息VO")
public class SdcResourceSatelliteVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键自增
     */
    @TableId(type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty("主键自增")
    private Long id;

    /**
     * 卫星标识
     */
    @ApiModelProperty("卫星标识")
    private String satId;

    /**
     * 卫星名称
     */
    @ApiModelProperty("卫星名称")
    private String satName;

    /**
     * 卫星PRN号
     */
    @ApiModelProperty("卫星PRN号")
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
    private String orbitalPlaid;

    /**
     * 节点编号
     */
    @ApiModelProperty("节点编号")
    private String orbitalPlaNodeId;

    /**
     * 太阳光反射系数
     */
    @ApiModelProperty("太阳光反射系数")
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
    private String material;

    /**
     * 载荷数量
     */
    @ApiModelProperty("载荷数量")
    private Integer sensorNum;

    /**
     * 创建日期
     */
    @ApiModelProperty("创建日期")
    private LocalDateTime createTime;

    /**
     * 修改日期
     */
    @ApiModelProperty("修改日期")
    private LocalDateTime updateTime;
}
