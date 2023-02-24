package cn.piesat.sec.model.vo.dataparse;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@ApiModel("空间环境周报预制备数据")
public class WeekForeVo implements Serializable {


    private static final long serialVersionUID = 1L;
    @ApiModelProperty("时间")
    private LocalDateTime time;
    @ApiModelProperty("空间环境周报描述")
    private String sumfore;

}
