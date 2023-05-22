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
 * ${comments}DTO
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-12 14:47:57
 */
@Data
@ApiModel("${comments}DTO")
public class SecDstAlarmDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键自增||DO
     */
    @TableId(type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty("主键自增||DO")
    private Long id;
}
