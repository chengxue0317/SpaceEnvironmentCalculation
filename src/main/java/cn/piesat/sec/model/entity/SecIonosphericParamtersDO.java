package cn.piesat.sec.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 地磁参数数据
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 21:52:42
 */
@Data
@TableName("SEC_IONOSPHERIC_ST")
@ApiModel("电离层参数实体类")
public class SecIonosphericParamtersDO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * ID主键
     */
    @TableId(type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty("ID主键")
    private Long id;
    /**
     * L_S4
     */
    @ApiModelProperty("L_S4")
    private String ls4;
    /**
     * S_S4
     */
    @ApiModelProperty("S_S4")
    private String ss4;
    /**
     * UHF_S4
     */
    @ApiModelProperty("UHF_S4")
    private String uhfs4;
    /**
     * 时间
     */
    @ApiModelProperty("时间")
    private LocalDateTime time;
}
