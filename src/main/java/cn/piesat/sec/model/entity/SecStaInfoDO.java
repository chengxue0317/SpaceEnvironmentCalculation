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
 * 台站信息表
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-27 10:51:47
 */
@Data
@TableName("SEC_STA_INFO")
@ApiModel("台站信息表实体类")
public class SecStaInfoDO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
    * ID主键
    */
    @TableId(type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty("ID主键")
    private Long id;
    /**
    * 台站标识
    */
    @ApiModelProperty("台站标识")
    private String staId;
    /**
    * 台站名称
    */
    @ApiModelProperty("台站名称")
    private String name;
    /**
    * 台站类型
    */
    @ApiModelProperty("台站类型")
    private Integer type;
    /**
    * 台站经度
    */
    @ApiModelProperty("台站经度")
    private Double lon;
    /**
    * 台站纬度
    */
    @ApiModelProperty("台站纬度")
    private Double lat;
    /**
    * 台站高度
    */
    @ApiModelProperty("台站高度")
    private Double ele;
    /**
    * 台站地址
    */
    @ApiModelProperty("台站地址")
    private String addr;
    /**
    * 创建时间
    */
    @ApiModelProperty("创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    /**
    * 更新时间
    */
    @ApiModelProperty("更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
