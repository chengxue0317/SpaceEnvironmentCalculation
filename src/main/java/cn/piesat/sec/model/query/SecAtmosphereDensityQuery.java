package cn.piesat.sec.model.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import cn.piesat.kjyy.common.mybatisplus.annotation.query.Where;
import cn.piesat.kjyy.common.mybatisplus.model.entity.Between;
import cn.piesat.kjyy.common.mybatisplus.annotation.query.OrderBy;
import cn.piesat.kjyy.common.mybatisplus.model.enums.Condition;
import java.time.LocalDateTime;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.util.List;
import lombok.Data;

/**
 * 大气密度预报模块Query
 *
 * @author sw
 * @email shiwang@piesat.cn
 * @date 2022-11-16 15:07:53
 */
@Data
@ApiModel("大气密度预报模块查询对象")
public class SecAtmosphereDensityQuery {
    /**
     * 卫星标识
     */
    @ApiModelProperty("卫星标识")
    @Where(value = Condition.LIKE,column = "SAT_ID")
    private String satId;
    /**
     * 单位标识
     */
    @ApiModelProperty("单位标识")
    private String unitId;
    /**
     * 设备标识
     */
    @ApiModelProperty("设备标识")
    private String deviceId;
    /**
     * 时间（UTC）
     */
    @ApiModelProperty("时间（UTC）")
    @Where(value = Condition.BETWEEN,column = "TIME")
    private Between<LocalDateTime> timeBetween;
}
