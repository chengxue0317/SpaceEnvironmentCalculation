package cn.piesat.sec.model.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 地磁参数数据Query
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 21:52:42
 */
@Data
@ApiModel("地磁参数数据查询对象")
public class SecMagneticParamterQuery {
    /**
     * 总磁场
     */
    @ApiModelProperty("总磁场")
    private Double bT;
    /**
     * 磁子午线
     */
    @ApiModelProperty("磁子午线")
    private Double bH;
    /**
     * 磁偏角
     */
    @ApiModelProperty("磁偏角")
    private Double bD;
    /**
     * 垂直分量
     */
    @ApiModelProperty("垂直分量")
    private Double bZ;
    /**
     * 时间
     */
    @ApiModelProperty("时间")
    private LocalDateTime time;
}
