package cn.piesat.sec.model.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 太阳风速Query
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 21:11:31
 */
@Data
@ApiModel("太阳风速查询对象")
public class SecSolarWindQuery {
    /**
     * 太阳风速
     */
    @ApiModelProperty("太阳风速")
    private Double bulkspeed;
    /**
     * 时间
     */
    @ApiModelProperty("时间")
    private LocalDateTime time;
}
