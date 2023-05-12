package cn.piesat.sec.controller;

import cn.piesat.sec.model.vo.SecEnvElementVO;
import cn.piesat.sec.service.SecKpIndexService;
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
 * KP指数
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 16:57:26
 */
@Api(tags = "Kp指数")
@RestController
@RequestMapping("/seckpindex")
@RequiredArgsConstructor
public class SecKpIndexController {

    private final SecKpIndexService secKpIndexService;

    @ApiOperation("Kp指数数据")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "String", required = true),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "String", required = true)
    })
    @PostMapping("/kpData")
    public SecEnvElementVO getKpData(@RequestParam(value = "startTime", required = false) String startTime,
                                     @RequestParam(value = "endTime", required = false) String endTime) {
        return secKpIndexService.getKpData(startTime, endTime);
    }
}
