package cn.piesat.sec.model.vo;

import cn.piesat.sec.model.entity.AlarmEventDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 警报事件VO
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-08 23:02:08
 */
@Data
@ApiModel("警报事件VO")
public class AlarmEventForecastVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 警报时间(UTC+8)
     */
    @ApiModelProperty("警报时间(UTC+8)")
    private String time;

    /**
     * 警报类别
     */
    @ApiModelProperty("太阳X射线")
    private AlarmEventDO xray;

    /**
     * 警报等级
     */
    @ApiModelProperty("高能质子")
    private AlarmEventDO proton;

    /**
     * 警报内容
     */
    @ApiModelProperty("高能电子")
    private AlarmEventDO electron;

    /**
     * 警报综述
     */
    @ApiModelProperty("地磁暴")
    private AlarmEventDO geomagnetic;
}
