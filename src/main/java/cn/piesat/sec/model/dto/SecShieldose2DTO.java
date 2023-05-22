package cn.piesat.sec.model.dto;

import cn.piesat.kjyy.common.web.annotation.validator.group.AddGroup;
import cn.piesat.kjyy.common.web.annotation.validator.group.UpdateGroup;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ${comments}DTO
 *
 * @author sw
 * @email shiwang@piesat.cn
 * @date 2022-12-02 13:24:52
 */
@Data
@ApiModel("${comments}DTO")
public class SecShieldose2DTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * ID
     */
    @NotNull(message = "主键不能为空", groups = UpdateGroup.class)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty("ID")
    private Integer id;
    /**
     * 卫星标识
    */
    @ApiModelProperty("卫星标识")
    @Length(max = 50 , message = "长度必须小于等于50" ,groups ={AddGroup.class,UpdateGroup.class} )
    private String satId;
    /**
     * 材料
    */
    @ApiModelProperty("材料")
    @Length(max = 50 , message = "长度必须小于等于50" ,groups ={AddGroup.class, UpdateGroup.class} )
    private String material;
    /**
     * 模型
    */
    @ApiModelProperty("模型")
    @Length(max = 50 , message = "长度必须小于等于50" ,groups ={AddGroup.class,UpdateGroup.class} )
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
