package cn.piesat.sec.model.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 电离层参数VO
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 16:35:45
 */
@Data
@Getter
@Setter
@ApiModel("电离层参数VO")
public class SecIonosphericParametersVO {
    /**
     * 图片名称
     */
    @ApiModelProperty("图片名称")
    private String name;

    /**
     * 图片地址
     */
    @ApiModelProperty("图片地址")
    private String src;
}
