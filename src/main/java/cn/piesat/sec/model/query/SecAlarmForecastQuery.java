package cn.piesat.sec.model.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 未来三天警报预报表Query
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-12 15:16:45
 */
@Data
@ApiModel("未来三天警报预报表查询对象")
public class SecAlarmForecastQuery {
    /**
     * 时间(UTC+8)
     */
    @ApiModelProperty("时间(UTC+8)")
    private LocalDateTime time;
}
