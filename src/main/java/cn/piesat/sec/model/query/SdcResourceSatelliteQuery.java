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
 * 卫星基本信息Query
 *
 * @author sw
 * @email shiwang@piesat.cn
 * @date 2022-11-14 16:35:45
 */
@Data
@ApiModel("卫星基本信息查询对象")
public class SdcResourceSatelliteQuery {
    /**
     * 卫星标识
     */
    @ApiModelProperty("卫星标识")
    @Where(value = Condition.LIKE,column = "SAT_ID")
    private String satId;
    /**
     * 卫星名称
     */
    @ApiModelProperty("卫星名称")
    @Where(value = Condition.LIKE,column = "SAT_NAME")
    private String satName;
    /**
     * 卫星状态：1-入轨，2-入网，3-离网，4-离轨
     */
    @ApiModelProperty("卫星状态：1-入轨，2-入网，3-离网，4-离轨")
    @Where(value = Condition.LIKE,column = "SAT_STATUS")
    private Integer satStatus;
    /**
     * 轨道类型(1-倾斜轨，2-近极轨，3-高轨)
     */
    @ApiModelProperty("轨道类型(1-倾斜轨，2-近极轨，3-高轨)")
    @Where(value = Condition.LIKE,column = "ORBITAL_TYPE")
    private Integer orbitalType;
    /**
     * 创建日期
     */
    @ApiModelProperty("创建日期")
    @Where(value = Condition.BETWEEN,column = "CREATE_TIME")
    private Between<LocalDateTime> createTimeBetween;
}
