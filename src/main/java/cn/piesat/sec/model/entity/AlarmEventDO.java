package cn.piesat.sec.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 太阳质子事件
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-08 23:02:08
 */
@Data
@TableName("SEC_PROTON_ALARM")
@ApiModel("太阳质子事件实体类")
public class AlarmEventDO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
    * 实际警报出现时间(UTC+8)
    */
    @ApiModelProperty("实际警报出现时间(UTC+8)")
    private LocalDateTime thresholdTime;
    /**
     * 警报类型
     */
    @ApiModelProperty("警报类型")
    private String type;
    /**
    * 警报内容
    */
    @ApiModelProperty("警报内容")
    private String content;
    /**
     * 警报预览
     */
    @ApiModelProperty("警报综述")
    private String overview;
    /**
    * 警报级别0~4分5个等级
    */
    @ApiModelProperty("警报级别0~4分5个等级")
    private Integer level;
}
