package cn.piesat.sec.model.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 未来三天警报预报表DTO
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-12 15:16:45
 */
@Data
@ApiModel("未来三天警报预报表DTO")
public class SecAlarmForecastDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID主键||DO
     */
    @TableId(type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty("ID主键||DO")
    private Long id;
}
