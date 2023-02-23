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

    @ApiModelProperty("")
    private Double p10;

    @ApiModelProperty("")
    private Double p50;

    @ApiModelProperty("")
    private Double p100;

    @ApiModelProperty("")
    private Double e2;

    @ApiModelProperty("")
    private Double pdiff1;

    @ApiModelProperty("")
    private Double pdiff2a;

    @ApiModelProperty("")
    private Double pdiff2b;

    @ApiModelProperty("")
    private Double pdiff3;

    @ApiModelProperty("")
    private Double pdiff4;

    @ApiModelProperty("")
    private Double pdiff5;

    @ApiModelProperty("")
    private Double pdiff6;

    @ApiModelProperty("")
    private Double pdiff7;

    @ApiModelProperty("")
    private Double pdiff8a;

    @ApiModelProperty("")
    private Double pdiff8b;

    @ApiModelProperty("")
    private Double pdiff8c;

    @ApiModelProperty("")
    private Double pdiff9;

    @ApiModelProperty("")
    private Double pdiff10;
}
