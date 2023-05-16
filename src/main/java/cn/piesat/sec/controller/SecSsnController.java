package cn.piesat.sec.controller;

import java.io.Serializable;
import java.util.Arrays;

import cn.piesat.kjyy.common.mybatisplus.annotation.validator.group.AddGroup;
import cn.piesat.kjyy.common.mybatisplus.annotation.validator.group.UpdateGroup;
import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.model.dto.SecSsnDTO;
import cn.piesat.sec.model.query.SecSsnQuery;
import cn.piesat.sec.model.vo.SecEnvElementVO;
import cn.piesat.sec.service.SecSsnService;
import cn.piesat.sec.model.vo.SecSsnVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/solarSpotData")
    public SecEnvElementVO getSunSpotData(
            @RequestParam(value = "startTime", required = false) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime
    ) {
        return secSsnService.getSunSpotData(startTime, endTime);
    }
}
