package cn.piesat.sec.controller;

import cn.piesat.sec.model.vo.SecEnvElementVO;
import cn.piesat.sec.service.SecF107FluxService;
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
 * 太阳F10.7指数
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 20:49:13
 */
@Api(tags = "太阳F10.7指数")
@RestController
@RequestMapping("/secf107flux")
@RequiredArgsConstructor
public class SecF107FluxController {

    private final SecF107FluxService secF107FluxService;

    @ApiOperation("F10.7数据")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "String", required = true),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "String", required = true)
    })
    @PostMapping("/f107Data")
    public SecEnvElementVO getF107Data(@RequestParam(value = "startTime", required = false) String startTime,
                                       @RequestParam(value = "endTime", required = false) String endTime) {
        return secF107FluxService.getF107Data(startTime, endTime);
    }
}
