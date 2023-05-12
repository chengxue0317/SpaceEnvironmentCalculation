package cn.piesat.sec.controller;

import cn.piesat.sec.model.vo.SecEnvElementVO;
import cn.piesat.sec.service.SecSolarWindService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 太阳风速
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 21:11:31
 */
@Api(tags = "太阳风速")
@RestController
@RequestMapping("/secsolarwind")
@RequiredArgsConstructor
public class SecSolarWindController {

    private final SecSolarWindService secSolarWindService;

    @ApiOperation("查询一段时间内的太阳风速数据")
    @PostMapping("/getSolarWindData")
    public SecEnvElementVO getSolarWindData(@RequestParam(value = "startTime", required = false) String startTime,
                                            @RequestParam(value = "endTime", required = false) String endTime) {
        return secSolarWindService.getSolarWindData(startTime, endTime);
    }
}
