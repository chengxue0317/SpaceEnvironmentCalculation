package cn.piesat.sec.controller;

import cn.piesat.kjyy.common.log.annotation.OpLog;
import cn.piesat.kjyy.common.log.enums.BusinessType;
import cn.piesat.sec.model.vo.SecEnvElementVO;
import cn.piesat.sec.service.SecApIndexService;
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
 * AP指数
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 15:53:01
 */
@Api(tags = "Ap指数")
@RestController
@RequestMapping("/secapindex")
@RequiredArgsConstructor
public class SecApIndexController {

    private final SecApIndexService secApIndexService;

    @ApiOperation("Ap指数数据")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "String", required = true),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "String", required = true)
    })
    @OpLog(op = BusinessType.OTHER, description = "Ap指数数据")
    @PostMapping("/apData")
    public SecEnvElementVO getApData(@RequestParam(value = "startTime", required = false) String startTime,
                                     @RequestParam(value = "endTime", required = false) String endTime) {
        return secApIndexService.getApData(startTime, endTime);
    }
}
