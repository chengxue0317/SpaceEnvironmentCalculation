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
@ApiModel("AP对象")
public class GmapVO implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty("时间")
    private LocalDateTime time;

    @ApiModelProperty("事件代码")
    private Double ap;
}
