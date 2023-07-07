package cn.piesat.sec.controller;

import cn.piesat.kjyy.common.log.annotation.OpLog;
import cn.piesat.kjyy.common.log.enums.BusinessType;
import cn.piesat.sec.model.vo.SecEnvElementVO;
import cn.piesat.sec.service.SecDstIndexService;
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
 * DST指数现报数据
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 17:54:18
 */
@Api(tags = "DST指数")
@RestController
@RequestMapping("/secdstindex")
@RequiredArgsConstructor
public class SecDstIndexController {

    private final SecDstIndexService secDstIndexService;

    @ApiOperation("DST数据")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "String", required = true),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "String", required = true)
    })
    @OpLog(op = BusinessType.OTHER, description = "地磁DST数据")
    @PostMapping("/dstData")
    public SecEnvElementVO getDstData(@RequestParam(value = "startTime", required = false) String startTime,
                                      @RequestParam(value = "endTime", required = false) String endTime) {
        return secDstIndexService.getDstData(startTime, endTime);
    }
}
