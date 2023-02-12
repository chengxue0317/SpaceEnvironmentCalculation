package cn.piesat.sec.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("数据文件对象")
public class SecSpaceFileVO {
    @ApiModelProperty("文件类型")
    private String fileType;

    @ApiModelProperty("文件路径")
    private String filePath;
}
