package cn.piesat.sec.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * ${comments}VO
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-12 14:47:57
 */
@Data
@ApiModel("${comments}VO")
public class SecDstAlarmVO implements Serializable {
    private static final long serialVersionUID = 1L;

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
