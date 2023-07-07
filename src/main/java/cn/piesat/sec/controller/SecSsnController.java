package cn.piesat.sec.controller;

import cn.piesat.kjyy.common.log.annotation.OpLog;
import cn.piesat.kjyy.common.log.enums.BusinessType;
import cn.piesat.sec.model.vo.SecEnvElementVO;
import cn.piesat.sec.service.SecSsnService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 太阳黑子数
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-13 20:02:03
 */
@Api(tags = "太阳黑子数")
@RestController
@RequestMapping("/secssn")
@RequiredArgsConstructor
public class SecSsnController {

    private final SecSsnService secSsnService;

    @ApiOperation("获取太阳黑子数")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "String", required = false),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "String", required = false)
    })
    @OpLog(op = BusinessType.OTHER, description = "获取太阳黑子数")
    @PostMapping("/solarSpotData")
    public SecEnvElementVO getSunSpotData(
            @RequestParam(value = "startTime", required = false) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime
    ) {
        return secSsnService.getSunSpotData(startTime, endTime);
    }
}
