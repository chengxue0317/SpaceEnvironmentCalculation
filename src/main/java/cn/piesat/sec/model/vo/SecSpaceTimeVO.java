package cn.piesat.sec.model.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 时空接口数据文件对象
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 12:16:14
 */
@Data
@ApiModel("时空接口数据文件对象VO")
public class SecSpaceTimeVO {
    @ApiModelProperty("文件类型")
    private String fileType;

    @ApiModelProperty("数据开始时间")
    private String startTime;

    @ApiModelProperty("数据结束时间")
    private String endTime;

    @ApiModelProperty("文件路径")
    private List<String> path;

    @ApiModelProperty("结果消息")
    private String message = "SUCCESS";
}
