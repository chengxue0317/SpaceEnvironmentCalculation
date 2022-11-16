package cn.piesat.sec.model.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 太阳F10.7指数Query
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 20:49:13
 */
@Data
@ApiModel("太阳F10.7指数查询对象")
public class SecF107FluxQuery {
    /**
     * F10.7
     */
    @ApiModelProperty("F10.7")
    private Double f107;
    /**
     * 时间
     */
    @ApiModelProperty("时间")
    private LocalDateTime time;
}
