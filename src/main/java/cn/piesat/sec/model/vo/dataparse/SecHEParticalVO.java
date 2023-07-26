package cn.piesat.sec.model.vo.dataparse;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 高能粒子数据
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-13 20:02:03
 */
@Data
@ApiModel("高能粒子数据对象")
public class SecHEParticalVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("时间")
    private LocalDateTime time;

    @ApiModelProperty("≥10MeV质子通量")
    private Double p10;

    @ApiModelProperty("≥50MeV质子通量")
    private Double p50;

    @ApiModelProperty("≥100MeV质子通量")
    private Double p100;

    @ApiModelProperty("≥2MeV电子通量")
    private Double e2;

    @ApiModelProperty("1020-1860keV质子通量")
    private Double pdiff1;

    @ApiModelProperty("1900-2300keV质子通量")
    private Double pdiff2a;

    @ApiModelProperty("2310-3340keV质子通量")
    private Double pdiff2b;

    @ApiModelProperty("3400-6480keV质子通量")
    private Double pdiff3;

    @ApiModelProperty("5840-11000keV质子通量")
    private Double pdiff4;

    @ApiModelProperty("11640-23270keV质子通量")
    private Double pdiff5;

    @ApiModelProperty("25900-38100keV质子通量")
    private Double pdiff6;

    @ApiModelProperty("40300-73400keV质子通量")
    private Double pdiff7;

    @ApiModelProperty("83700-98500keV质子通量")
    private Double pdiff8a;

    @ApiModelProperty("99900-118000keV质子通量")
    private Double pdiff8b;

    @ApiModelProperty("115000-143000keV质子通量")
    private Double pdiff8c;

    @ApiModelProperty("160000-242000keV质子通量")
    private Double pdiff9;

    @ApiModelProperty("276000-404000keV质子通量")
    private Double pdiff10;
}
