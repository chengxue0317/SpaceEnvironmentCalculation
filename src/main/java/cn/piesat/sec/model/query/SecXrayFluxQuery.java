package cn.piesat.sec.model.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 太阳X射线流量Query
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 12:16:14
 */
@Data
@ApiModel("太阳X射线流量查询对象")
public class SecXrayFluxQuery {
    /**
     * 0.1~0.8毫微米通道流量
     */
    @ApiModelProperty("0.1~0.8毫微米通道流量")
    private Double longer;
    /**
     * 0.05~0.4毫微米通道流量
     */
    @ApiModelProperty("0.05~0.4毫微米通道流量")
    private Double shorter;
}
