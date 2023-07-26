package cn.piesat.sec.model.vo.dataparse;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel("空间环境每日预报预制备数据")
public class DayForeVo {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty("时间")
    private LocalDateTime time;

    @ApiModelProperty("Ap未来1到60天预报")
    private Double ap1;

    @ApiModelProperty("Ap未来XX小时预报")
    private Double ap2;

    @ApiModelProperty("Ap未来XX小时预报")
    private Double ap3;

    @ApiModelProperty("F10.7未来1到100天小时预报")
    private Double f1071;

    @ApiModelProperty("F10.7未来XX小时预报")
    private Double f1072;

    @ApiModelProperty("F10.7未来XX小时预报")
    private Double f1073;

    @ApiModelProperty("质子事件未来24小时发生概率")
    private String spe1;

    @ApiModelProperty("质子事件未来48小时发生概率")
    private String spe2;

    @ApiModelProperty("质子事件未来72小时发生概率")
    private String spe3;

    @ApiModelProperty("高能电子暴未来24小时发生概率")
    private String ree1;

    @ApiModelProperty("高能电子暴未来48小时发生概率")
    private String ree2;

    @ApiModelProperty("高能电子暴未来72小时发生概率")
    private String ree3;

    @ApiModelProperty("小地磁暴未来24小时发生概率")
    private String gsm1;

    @ApiModelProperty("小地磁暴未来48小时发生概率")
    private String gsm2;

    @ApiModelProperty("小地磁暴未来72小时发生概率")
    private String gsm3;

    @ApiModelProperty("大地磁暴未来24小时发生概率")
    private String gsma1;

    @ApiModelProperty("大地磁暴未来48小时发生概率")
    private String gsma2;

    @ApiModelProperty("大地磁暴未来72小时发生概率")
    private String gsma3;

    @ApiModelProperty("M级以上耀斑未来24小时发生概率")
    private String sxrm1;

    @ApiModelProperty("M级以上耀斑未来48小时发生概率")
    private String sxrm2;

    @ApiModelProperty("M级以上耀斑未来72小时发生概率")
    private String sxrm3;

    @ApiModelProperty("X级以上耀斑未来24小时发生概率")
    private String sxrx1;

    @ApiModelProperty("X级以上耀斑未来48小时发生概率")
    private String sxrx2;

    @ApiModelProperty("X级以上耀斑未来72小时发生概率")
    private String sxrx3;

    @ApiModelProperty("过去24小时空天间环境预报描述")
    private String bef24h = "";

    @ApiModelProperty("未来3天空间环境预报描述")
    private String aft3d = "";
}
