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
public class SecEnvOverviewVO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 时间
     */
    @ApiModelProperty("时间")
    private String time;

    /**
     * 过去24小时空间天气综述
     */
    @ApiModelProperty("过去24小时空间天气综述")
    private String bef24h;

    /**
     * 未来3天空间天气综述
     */
    @ApiModelProperty("未来3天空间天气综述")
    private String aft3day;

    /**
     * 过去一周空间天气综述
     */
    @ApiModelProperty("过去一周空间天气综述")
    private String befweek;

    /**
     * 未来一周空间天气综述
     */
    @ApiModelProperty("未来一周空间天气综述")
    private String aftweek;

    /**
     * 过去一月空间天气综述
     */
    @ApiModelProperty("过去一月空间天气综述")
    private String befmonth;

    /**
     * 未来一月空间天气综述
     */
    @ApiModelProperty("未来一月空间天气综述")
    private String aftmonth;

    /**
     * 日报路径
     */
    @ApiModelProperty("日报路径")
    private String pathday;

    /**
     * 未来一月空间天气综述
     */
    @ApiModelProperty("周报路径")
    private String pathweek;
    /**
     * 未来一月空间天气综述
     */
    @ApiModelProperty("月报路径")
    private String pathmonth;
}
