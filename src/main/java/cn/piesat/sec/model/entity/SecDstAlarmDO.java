package cn.piesat.sec.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * ${comments}
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-12 14:47:57
 */
@Data
@TableName("SEC_DST_ALARM")
@ApiModel("${comments}实体类")
public class SecDstAlarmDO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
    * 主键自增||DO
    */
    @TableId(type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty("主键自增||DO")
    private Long id;
    /**
    * 单位标识||DO
    */
    @ApiModelProperty("单位标识||DO")
    private String unitId;
    /**
    * 设备标识||DO
    */
    @ApiModelProperty("设备标识||DO")
    private String deviceId;
    /**
    * 台站标识||DO
    */
    @ApiModelProperty("台站标识||DO")
    private String staId;
    /**
    * 预报时间(UTC)
    */
    @ApiModelProperty("预报时间(UTC)")
    private LocalDateTime publishTime;
    /**
    * 实际警报出现时间(UTC+8)
    */
    @ApiModelProperty("实际警报出现时间(UTC+8)")
    private LocalDateTime thresholdTime;
    /**
    * 持续警报内容
    */
    @ApiModelProperty("持续警报内容")
    private String content;
    /**
    * 警报综述
    */
    @ApiModelProperty("警报综述")
    private Integer level;
    /**
    * 警报级别 0：无警报，1：黄色警报，2：深黄色警报，3：橙色警报，4：红色警报，5：深红
    */
    @ApiModelProperty("警报级别 0：无警报，1：黄色警报，2：深黄色警报，3：橙色警报，4：红色警报，5：深红")
    private String overview;
}
