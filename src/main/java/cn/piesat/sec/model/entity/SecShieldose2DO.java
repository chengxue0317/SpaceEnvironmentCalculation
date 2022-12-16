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
 * @author sw
 * @email shiwang@piesat.cn
 * @date 2022-12-02 13:24:52
 */
@Data
@TableName("SEC_SHIELDOSE_2")
@ApiModel("${comments}实体类")
public class SecShieldose2DO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
    * $column.comments
    */
    @TableId(type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty("$column.comments")
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
