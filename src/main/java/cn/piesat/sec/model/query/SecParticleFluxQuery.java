package cn.piesat.sec.model.query;

import cn.piesat.kjyy.common.mybatisplus.annotation.query.Where;
import cn.piesat.kjyy.common.mybatisplus.model.entity.Between;
import cn.piesat.kjyy.common.mybatisplus.model.enums.Condition;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 高能粒子通量数据Query
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-15 11:11:30
 */
@Data
@ApiModel("高能粒子通量数据查询对象")
public class SecParticleFluxQuery {
    /**
     * 卫星标识
     */
    @ApiModelProperty("卫星标识")
    private String satId;
    /**
     * 能量大于2MEV通道电子通量
     */
    @ApiModelProperty("能量大于2MEV通道电子通量")
    private Double e2;
    /**
     * 3~5 MEV质子积分通量
     */
    @ApiModelProperty("3~5 MEV质子积分通量")
    private Double p1;
    /**
     * 5~10 MEV质子积分通量
     */
    @ApiModelProperty("5~10 MEV质子积分通量")
    private Double p2;
    /**
     * 10~26 MEV质子积分通量
     */
    @ApiModelProperty("10~26 MEV质子积分通量")
    private Double p3;
    /**
     * 26~40 MEV质子积分通量
     */
    @ApiModelProperty("26~40 MEV质子积分通量")
    private Double p4;
    /**
     * 40~100 MEV质子积分通量
     */
    @ApiModelProperty("40~100 MEV质子积分通量")
    private Double p5;
    /**
     * 100300 MEV质子积分通量
     */
    @ApiModelProperty("100300 MEV质子积分通量")
    private Double p6;
    /**
     * 12~110 MEV HE离子积分通量
     */
    @ApiModelProperty("12~110 MEV HE离子积分通量")
    private Double he;
    /**
     * 24~220 MEV LI离子积分通量
     */
    @ApiModelProperty("24~220 MEV LI离子积分通量")
    private Double li;
    /**
     * 60~570 MEV HE离子积分通量
     */
    @ApiModelProperty("60~570 MEV HE离子积分通量")
    private Double c;
    /**
     * 0.2~1.2 GEV MG离子积分通量
     */
    @ApiModelProperty("0.2~1.2 GEV MG离子积分通量")
    private Double mg;
    /**
     * 0.3~2.0 MEV AR离子积分通量
     */
    @ApiModelProperty("0.3~2.0 MEV AR离子积分通量")
    private Double ar;
    /**
     * 0.5~2.0 FE 离子积分通量
     */
    @ApiModelProperty("0.5~2.0 FE 离子积分通量")
    private Double fe;
    /**
     * 时间（UTC）
     */
    @ApiModelProperty("时间（UTC）")
    @Where(value = Condition.BETWEEN,column = "TIME")
    private Between<LocalDateTime> timeBetween;
}
