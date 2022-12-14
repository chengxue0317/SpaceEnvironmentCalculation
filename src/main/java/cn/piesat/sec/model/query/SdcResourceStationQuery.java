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
 * 测站基本信息Query
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-12-13 14:18:47
 */
@Data
@ApiModel("测站基本信息查询对象")
public class SdcResourceStationQuery {
    /**
     * 地面站标识
     */
    @ApiModelProperty("地面站标识")
    @Where(value = Condition.LIKE,column = "STA_ID")
    private String staId;
    /**
     * 地面站名称
     */
    @ApiModelProperty("地面站名称")
    @Where(value = Condition.LIKE,column = "STA_NAME")
    private String staName;
    /**
     * 地面站类型：1-低轨信关站，2-高轨信关站，3-高轨测控站，4-轻型信关站，5-地面监测站，6-激光标校站。
     */
    @ApiModelProperty("地面站类型：1-低轨信关站，2-高轨信关站，3-高轨测控站，4-轻型信关站，5-地面监测站，6-激光标校站。")
    @Where(value = Condition.LIKE,column = "STA_TYPE")
    private Integer staType;
    /**
     * 状态：0-可用，1-不可用。
     */
    @ApiModelProperty("状态：0-可用，1-不可用。")
    @Where(value = Condition.LIKE,column = "STATUS")
    private Integer status;
    /**
     * 创建日期
     */
    @ApiModelProperty("创建日期")
    @Where(value = Condition.BETWEEN,column = "CREATE_TIME")
    private Between<LocalDateTime> createTimeBetween;
}
