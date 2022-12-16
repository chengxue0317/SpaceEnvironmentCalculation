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
 * ${comments}Query
 *
 * @author sw
 * @email shiwang@piesat.cn
 * @date 2022-12-02 13:24:52
 */
@Data
@ApiModel("${comments}查询对象")
public class SecShieldose2Query {
    /**
     * 卫星标识
     */
    @ApiModelProperty("卫星标识")
    private String satId;
    /**
     * 材料
     */
    @ApiModelProperty("材料")
    private String material;
    /**
     * 模型
     */
    @ApiModelProperty("模型")
    private String mode;
    /**
     * 时间
     */
    @ApiModelProperty("时间")
    @Where(value = Condition.BETWEEN,column = "TIME")
    private Between<LocalDateTime> timeBetween;
}
