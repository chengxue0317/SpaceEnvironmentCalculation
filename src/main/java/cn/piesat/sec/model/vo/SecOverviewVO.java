package cn.piesat.sec.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 太阳F10.7指数VO
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 20:49:13
 */
@Data
@ApiModel("空间环境预报")
public class SecOverviewVO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 时间
     */
    @ApiModelProperty("时间")
    private String time;

    /**
     * 过去24小时空间天气综述
     */
    @ApiModelProperty("过去空间天气综述")
    private String pastReview;

    /**
     * 未来3天空间天气综述
     */
    @ApiModelProperty("未来空间天气综述")
    private String futureReview;

    /**
     * 报文文件路径
     */
    @ApiModelProperty("报文文件路径")
    private String path;
}
