package cn.piesat.sec.controller;

import cn.piesat.sec.model.vo.SecEnvElementVO;
import cn.piesat.sec.service.SecXrayFluxService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 太阳X射线流量
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 12:16:14
 */
@Api(tags = "太阳X射线流量")
@RestController
@RequestMapping("/secxrayflux")
@RequiredArgsConstructor
public class SecXrayFluxController {

    private final SecXrayFluxService secXrayFluxService;

    @ApiOperation("太阳耀斑/X射线流量数据")
    @PostMapping("/solarXrayData")
    public SecEnvElementVO getSolarXrayData(
        @RequestParam(value = "startTime", required = false) String startTime,
        @RequestParam(value = "endTime", required = false) String endTime
    ) {
        return secXrayFluxService.getSolarXrayData(startTime, endTime);
    }
}
