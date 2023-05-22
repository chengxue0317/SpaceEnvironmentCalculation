package cn.piesat.sec.model.dto;

import cn.piesat.kjyy.common.web.annotation.validator.group.UpdateGroup;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 太阳X射线流量DTO
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 12:16:14
 */
@Data
@ApiModel("太阳X射线流量DTO")
public class SecXrayFluxDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * ID主键
    */
    @NotNull(message = "主键不能为空", groups = UpdateGroup.class)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty("ID主键")
    private Long id;
}
