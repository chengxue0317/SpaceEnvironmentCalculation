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
 * @author sw
 * @email shiwang@piesat.cn
 * @date 2022-12-02 13:24:52
 */
@Data
@ApiModel("${comments}VO")
public class SecShieldose2VO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty("ID")
    private Integer id;
    /**
     * 卫星标识
     */
    @ApiModelProperty("卫星标识")
    private String satId;

    /**
     * 材料
     */
    @ApiModelProperty("材料")
    private String material;

    /**
     * 模型
     */
    @ApiModelProperty("模型")
    private String mode;

    /**
     * 辐射剂量
     */
    @ApiModelProperty("辐射剂量")
    private Double does;

    /**
     * 隐蔽深度
     */
    @ApiModelProperty("隐蔽深度")
    private Double depth;

    /**
     * 时间
     */
    @ApiModelProperty("时间")
    private LocalDateTime time;
}
