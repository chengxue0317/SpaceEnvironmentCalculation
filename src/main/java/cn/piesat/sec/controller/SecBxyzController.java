package cn.piesat.sec.controller;

import cn.piesat.sec.model.vo.SecEnvElementVO;
import cn.piesat.sec.service.SecBxyzService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 地磁参数数据
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 21:52:42
 */
@Api(tags = "地磁参数数据")
@RestController
@RequestMapping("/secmagneticparamter")
@RequiredArgsConstructor
public class SecBxyzController {

    private final SecBxyzService secBxyzService;

    @ApiOperation("查询一段时间内的磁场数据")
    @GetMapping("/btxyzData")
    public SecEnvElementVO getBtxyzData(@RequestParam(value = "startTime", required = false) String startTime,
                                        @RequestParam(value = "endTime", required = false) String endTime) {
        return secBxyzService.getBtxyzData(startTime, endTime);
    }
}
