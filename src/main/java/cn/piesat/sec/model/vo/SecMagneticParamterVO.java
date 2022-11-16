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
 * 地磁参数数据VO
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 21:52:42
 */
@Data
@ApiModel("地磁参数数据VO")
public class SecMagneticParamterVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID主键
     */
    @TableId(type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty("ID主键")
    private Long id;

    /**
     * 总磁场
     */
    @ApiModelProperty("总磁场")
    private Double bT;

    /**
     * X方向磁场
     */
    @ApiModelProperty("X方向磁场")
    private Double bX;

    /**
     * Y方向磁场
     */
    @ApiModelProperty("Y方向磁场")
    private Double bY;

    /**
     * 磁子午线
     */
    @ApiModelProperty("磁子午线")
    private Double bH;

    /**
     * 磁偏角
     */
    @ApiModelProperty("磁偏角")
    private Double bD;

    /**
     * 垂直分量
     */
    @ApiModelProperty("垂直分量")
    private Double bZ;

    /**
     * 时间
     */
    @ApiModelProperty("时间")
    private LocalDateTime time;
}
