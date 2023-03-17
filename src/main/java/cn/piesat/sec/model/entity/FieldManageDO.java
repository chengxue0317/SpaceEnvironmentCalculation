package cn.piesat.sec.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 字段管理表
 *
 * @author sw
 * @email shiwang@piesat.cn
 * @date 2023-02-20 18:04:54
 */
@Data
@ApiModel("字段管理表实体类")
@TableName("SEC_FIELD_MANAGE")
public class FieldManageDO implements Serializable {
    private static final long serialVersionUID = 1L;
    @JsonSerialize(using= ToStringSerializer.class)
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 字段
     */
    @ApiModelProperty("字段")
    private String fieldName;
    /**
     * 数据类型
     */
    @ApiModelProperty("数据类型")
    private String dataType;
    /**
     * 精度
     */
    @ApiModelProperty("精度")
    private Integer precision;
    /**
     * 录入时间
     */
    @ApiModelProperty("录入时间")
    private LocalDateTime createTime;
    /**
     * 非空约束
     */
    @ApiModelProperty("非空约束")
    private Integer notNullConstraint;
    /**
     * 注释
     */
    @ApiModelProperty("注释")
    private String annotation;
    /**
     * 默认值
     */
    @ApiModelProperty("默认值")
    private String defaultValue;
}
