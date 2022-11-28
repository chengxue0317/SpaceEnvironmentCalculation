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
 * 台站信息表Query
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-27 10:51:47
 */
@Data
@ApiModel("台站信息表查询对象")
public class SecStaInfoQuery {
    /**
     * 台站标识
     */
    @ApiModelProperty("台站标识")
    private String staId;
    /**
     * 台站名称
     */
    @ApiModelProperty("台站名称")
    private String name;
}
