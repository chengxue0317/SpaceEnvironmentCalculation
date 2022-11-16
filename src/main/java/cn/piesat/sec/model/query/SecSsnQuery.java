package cn.piesat.sec.model.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 太阳黑子数Query
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-13 20:02:03
 */
@Data
@ApiModel("太阳黑子数查询对象")
public class SecSsnQuery {
    /**
     * 太阳黑子数
     */
    @ApiModelProperty("太阳黑子数")
    private Integer ssn;
    /**
     * 时间
     */
    @ApiModelProperty("时间")
    private LocalDateTime time;
}
