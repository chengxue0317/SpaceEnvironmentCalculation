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
 * 太阳X射线耀斑警报事件DTO
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-12 14:46:40
 */
@Data
@ApiModel("太阳X射线耀斑警报事件DTO")
public class SecXrayAlarmDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID主键||DO
     */
    @TableId(type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty("ID主键||DO")
    private Long id;
}
