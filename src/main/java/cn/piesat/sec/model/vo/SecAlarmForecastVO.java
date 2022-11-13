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
 * 未来三天警报预报表VO
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-12 15:16:45
 */
@Data
@ApiModel("未来三天警报预报表VO")
public class SecAlarmForecastVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 未来第3天质子事件
     */
    @ApiModelProperty("未来3天质子事件")
    private String spe1;

    /**
     * 
     */
    @ApiModelProperty("")
    private String spe2;

    /**
     * 
     */
    @ApiModelProperty("")
    private String spe3;

    /**
     * 未来第3天高能电子暴事件
     */
    @ApiModelProperty("未来3天高能电子暴事件")
    private String ree1;

    /**
     * 
     */
    @ApiModelProperty("")
    private String ree2;

    /**
     * 
     */
    @ApiModelProperty("")
    private String ree3;

    /**
     * 未来第3天小地磁暴事件
     */
    @ApiModelProperty("未来3天小地磁暴事件")
    private String gsmi1;

    /**
     * 
     */
    @ApiModelProperty("")
    private String gsmi2;

    /**
     * 
     */
    @ApiModelProperty("")
    private String gsmi3;

    /**
     * 未来第3天大地磁暴事件
     */
    @ApiModelProperty("未来3天大地磁暴事件")
    private String gsma1;

    /**
     * 
     */
    @ApiModelProperty("")
    private String gsma2;

    /**
     * 
     */
    @ApiModelProperty("")
    private String gsma3;

    /**
     * 未来第1天发生耀斑事件描述
     */
    @ApiModelProperty("未来第1天发生耀斑事件描述")
    private String sxr1;

    /**
     * 未来第2天发生耀斑事件描述
     */
    @ApiModelProperty("未来第2天发生耀斑事件描述")
    private String sxr2;

    /**
     * 未来第3天发生耀斑事件描述
     */
    @ApiModelProperty("未来第3天发生耀斑事件描述")
    private String sxr3;

    /**
     * 时间(UTC+8)
     */
    @ApiModelProperty("时间(UTC+8)")
    private LocalDateTime time;
}
