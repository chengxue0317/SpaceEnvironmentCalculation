package cn.piesat.sec.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class SecEnvOverviewDO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * ID主键
     */
    @ApiModelProperty("ID主键")
    @JsonSerialize(using= ToStringSerializer.class)
    @TableId(type = IdType.AUTO)
    private Long id;

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
}
