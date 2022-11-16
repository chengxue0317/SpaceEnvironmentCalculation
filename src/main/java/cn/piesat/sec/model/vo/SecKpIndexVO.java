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
 * KP指数VO
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 16:57:26
 */
@Data
@ApiModel("KP指数VO")
public class SecKpIndexVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID主键
     */
    @TableId(type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty("ID主键")
    private Long id;

    /**
     * KP1
     */
    @ApiModelProperty("KP1")
    private Double kp1;

    /**
     * KP2
     */
    @ApiModelProperty("KP2")
    private Double kp2;

    /**
     * KP3
     */
    @ApiModelProperty("KP3")
    private Double kp3;

    /**
     * KP4
     */
    @ApiModelProperty("KP4")
    private Double kp4;

    /**
     * KP5
     */
    @ApiModelProperty("KP5")
    private Double kp5;

    /**
     * KP6
     */
    @ApiModelProperty("KP6")
    private Double kp6;

    /**
     * KP7
     */
    @ApiModelProperty("KP7")
    private Double kp7;

    /**
     * KP8
     */
    @ApiModelProperty("KP8")
    private Double kp8;

    /**
     * 时间
     */
    @ApiModelProperty("时间")
    private LocalDateTime time;
}
