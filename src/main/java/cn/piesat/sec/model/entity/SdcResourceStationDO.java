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
 * 测站基本信息
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-12-13 14:18:47
 */
@Data
@TableName("SDC_RESOURCE_STATION")
@ApiModel("测站基本信息实体类")
public class SdcResourceStationDO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
    * 主键
    */
    @TableId(type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty("主键")
    private Long id;
    /**
    * 地面站标识
    */
    @ApiModelProperty("地面站标识")
    private String staId;
    /**
    * 地面站名称
    */
    @ApiModelProperty("地面站名称")
    private String staName;
    /**
    * 地面站类型：1-低轨信关站，2-高轨信关站，3-高轨测控站，4-轻型信关站，5-地面监测站，6-激光标校站。
    */
    @ApiModelProperty("地面站类型：1-低轨信关站，2-高轨信关站，3-高轨测控站，4-轻型信关站，5-地面监测站，6-激光标校站。")
    private Integer staType;
    /**
    * 状态：0-可用，1-不可用。
    */
    @ApiModelProperty("状态：0-可用，1-不可用。")
    private Integer status;
    /**
    * 天线数量
    */
    @ApiModelProperty("天线数量")
    private Integer antennaNum;
    /**
    * 地理经度
    */
    @ApiModelProperty("地理经度")
    private Double longitude;
    /**
    * 地理纬度
    */
    @ApiModelProperty("地理纬度")
    private Double latitude;
    /**
    * 海拔高度
    */
    @ApiModelProperty("海拔高度")
    private Double elevation;
    /**
    * 部署地点
    */
    @ApiModelProperty("部署地点")
    private String location;
    /**
    * 创建日期
    */
    @ApiModelProperty("创建日期")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    /**
    * 修改日期
    */
    @ApiModelProperty("修改日期")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
