package cn.piesat.sec.model.vo.dataparse;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel("电离层TEC数据")
public class IonoTecVO {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty("时间")
    private LocalDateTime time;

    @ApiModelProperty("电离层TEC网格数据")
    private Double tec;
}
