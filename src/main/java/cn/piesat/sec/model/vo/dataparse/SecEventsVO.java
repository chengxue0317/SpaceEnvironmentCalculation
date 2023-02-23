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
@ApiModel("事件对象")
public class SecEventsVO implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty("时间")
    private LocalDateTime time;

    @ApiModelProperty("事件代码")
    private String code;

    @ApiModelProperty("事件描述")
    private String content;

    @ApiModelProperty("事件级别")
    private int level;

}
