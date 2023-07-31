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
 * 高能粒子通量数据
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-15 11:11:30
 */
@Data
@TableName("SEC_PARTICLE_FLUX")
@ApiModel("高能粒子通量数据实体类")
public class SecParticleFluxDO implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty("ID主键")
    private Long id;

    @ApiModelProperty("卫星轨道监测到的≥10MeV质子积分通量")
    private Double p10;

    @ApiModelProperty("卫星轨道监测到的≥50MeV质子积分通量")
    private Double p50;

    @ApiModelProperty("卫星轨道监测到的≥100MeV质子积分通量")
    private Double p100;

    @ApiModelProperty("卫星轨道监测到的≥2MeV电子积分通量")
    private Double e2;

    @ApiModelProperty("卫星轨道监测到的1020-1860keV质子通量")
    private Double pdiff1;

    @ApiModelProperty("卫星轨道监测到的1900-2300keV质子通量")
    private Double pdiff2a;

    @ApiModelProperty("卫星轨道监测到的2310-3340keV质子通量")
    private Double pdiff2b;

    @ApiModelProperty("卫星轨道监测到的3400-6480keV质子通量")
    private Double pdiff3;

    @ApiModelProperty("卫星轨道监测到的5840-11000keV质子通量")
    private Double pdiff4;

    @ApiModelProperty("卫星轨道监测到的11640-23270keV质子通量")
    private Double pdiff5;

    @ApiModelProperty("卫星轨道监测到的25900-38100keV质子通量")
    private Double pdiff6;

    @ApiModelProperty("卫星轨道监测到的40300-73400keV质子通量")
    private Double pdiff7;

    @ApiModelProperty("卫星轨道监测到的83700-98500keV质子通量")
    private Double pdiff8a;

    @ApiModelProperty("卫星轨道监测到的99900-118000keV质子通量")
    private Double pdiff8b;

    @ApiModelProperty("卫星轨道监测到的115000-143000keV质子通量")
    private Double pdiff8c;

    @ApiModelProperty("卫星轨道监测到的160000-242000keV质子通量")
    private Double pdiff9;

    @ApiModelProperty("卫星轨道监测到的276000-404000keV质子通量")
    private Double pdiff10;

    @ApiModelProperty("时间（UTC）")
    private LocalDateTime time;
}
