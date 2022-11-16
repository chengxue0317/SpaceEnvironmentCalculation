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
 * 太阳X射线流量VO
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 12:16:14
 */
@Data
@ApiModel("太阳X射线流量VO")
public class SecXrayFluxVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID主键
     */
    @TableId(type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty("ID主键")
    private Long id;

    /**
     * 0.1~0.8毫微米通道流量
     */
    @ApiModelProperty("0.1~0.8毫微米通道流量")
    private Double longer;

    /**
     * 0.05~0.4毫微米通道流量
     */
    @ApiModelProperty("0.05~0.4毫微米通道流量")
    private Double shorter;

    /**
     * 时间
     */
    @ApiModelProperty("时间")
    private LocalDateTime time;
}
