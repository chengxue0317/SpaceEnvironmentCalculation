package cn.piesat.sec.controller;

import cn.piesat.kjyy.common.log.annotation.OpLog;
import cn.piesat.kjyy.common.log.enums.BusinessType;
import cn.piesat.sec.model.entity.SecParticleFluxDO;
import cn.piesat.sec.model.query.SecParticleFluxQuery;
import cn.piesat.sec.model.vo.HeavyIonFluxDataVO;
import cn.piesat.sec.model.vo.SecEnvElementVO;
import cn.piesat.sec.service.SecParticleFluxService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 高能粒子通量数据
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-15 11:11:30
 */
@Api(tags = "高能粒子通量数据")
@RestController
@RequestMapping("/secparticleflux")
@RequiredArgsConstructor
public class SecParticleFluxController {

    private final SecParticleFluxService secParticleFluxService;

    @ApiOperation("质子通量数据")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "String", required = true),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "String", required = true)
    })
    @OpLog(op = BusinessType.OTHER, description = "质子通量数据")
    @PostMapping("/protonFluxData")
    public SecEnvElementVO getProtonIndexData(@RequestParam(value = "startTime", required = false) String startTime,
                                              @RequestParam(value = "endTime", required = false) String endTime) {
        return secParticleFluxService.getProtonFluxData(startTime, endTime);
    }

    @ApiOperation("电子通量数据")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "String", required = true),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "String", required = true)
    })
    @OpLog(op = BusinessType.OTHER, description = "电子通量数据")
    @PostMapping("/electronicFluxData")
    public SecEnvElementVO getElectronicFluxData(@RequestParam(value = "startTime", required = false) String startTime,
                                                 @RequestParam(value = "endTime", required = false) String endTime) {
        return secParticleFluxService.getElectronicFluxData(startTime, endTime);
    }
}
