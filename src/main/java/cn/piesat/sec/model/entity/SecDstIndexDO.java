package cn.piesat.sec.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * DST指数现报数据
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 17:54:18
 */
@Data
@ApiModel("DST指数现报数据实体类")
@TableName("SEC_DST_INDEX")
public class SecDstIndexDO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * ID主键
     */
    @ApiModelProperty("ID主键")
    @JsonSerialize(using= ToStringSerializer.class)
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 单位标识
     */
    @ApiModelProperty("单位标识")
    private String unitId;
    /**
     * 设备标识
     */
    @ApiModelProperty("设备标识")
    private String deviceId;
    /**
     * 台站标识
     */
    @ApiModelProperty("台站标识")
    private String staId;
    /**
     * DST
     */
    @ApiModelProperty("DST")
    private Double dst;
    /**
     * 时间
     */
    @ApiModelProperty("时间")
    private LocalDateTime time;
}
