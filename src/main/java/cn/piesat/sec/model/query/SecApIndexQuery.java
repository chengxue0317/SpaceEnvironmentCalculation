package cn.piesat.sec.model.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AP指数Query
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 15:53:01
 */
@Data
@ApiModel("AP指数查询对象")
public class SecApIndexQuery {
    /**
     * AP
     */
    @ApiModelProperty("AP")
    private Double ap;
    /**
     * 时间
     */
    @ApiModelProperty("时间")
    private LocalDateTime time;
}
