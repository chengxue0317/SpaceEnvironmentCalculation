package cn.piesat.sec.model.dto;

import cn.piesat.kjyy.common.mybatisplus.annotation.validator.group.AddGroup;
import cn.piesat.kjyy.common.mybatisplus.annotation.validator.group.UpdateGroup;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 太阳质子事件DTO
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-08 23:02:08
 */
@Data
@ApiModel("太阳质子事件DTO")
public class ProtonAlarmDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * ID主键
    */
    @NotNull(message = "主键不能为空", groups = UpdateGroup.class)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty("ID主键")
    private Long id;
    /**
     * 预报时间(UTC+8)
    */
    @ApiModelProperty("预报时间(UTC+8)")
    private LocalDateTime publishTime;
    /**
     * 实际警报出现时间(UTC+8)
    */
    @ApiModelProperty("实际警报出现时间(UTC+8)")
    private LocalDateTime thresholdTime;
    /**
     * 警报内容
    */
    @ApiModelProperty("警报内容")
    @Length(max = 512 , message = "长度必须小于等于512" ,groups ={AddGroup.class,UpdateGroup.class} )
    private String content;
    /**
     * 警报级别0~4分5个等级
    */
    @ApiModelProperty("警报级别0~4分5个等级")
    private Integer level;
}
