package cn.piesat.sec.controller;

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
    @PostMapping("/electronicFluxData")
    public SecEnvElementVO getElectronicFluxData(@RequestParam(value = "startTime", required = false) String startTime,
                                                 @RequestParam(value = "endTime", required = false) String endTime) {
        return secParticleFluxService.getElectronicFluxData(startTime, endTime);
    }

    @ApiOperation("查询一段时间内的重离子通量数据")
    @PostMapping("/getHeavyIonFluxData")
    public HeavyIonFluxDataVO getHeavyIonFluxData(@RequestBody(required = false) SecParticleFluxQuery secParticleFluxQuery) {
        QueryWrapper<SecParticleFluxDO> queryWrapper = new QueryWrapper<>();
        String satId = secParticleFluxQuery.getSatId();
        queryWrapper.select("He", "Li", "C", "Mg", "Ar", "Fe", "TIME")
                .eq(StringUtils.isNotBlank(satId), "SAT_ID", satId)
                .between("TIME", secParticleFluxQuery.getTimeBetween().getLeft(), secParticleFluxQuery.getTimeBetween().getRight());
        List<SecParticleFluxDO> list = secParticleFluxService.list(queryWrapper);
        HeavyIonFluxDataVO heavyIonFluxDataVO = new HeavyIonFluxDataVO();
        List<Double> he = new ArrayList<>();
        List<Double> li = new ArrayList<>();
        List<Double> c = new ArrayList<>();
        List<Double> mg = new ArrayList<>();
        List<Double> ar = new ArrayList<>();
        List<Double> fe = new ArrayList<>();
        List<LocalDateTime> time = new ArrayList<>();
        list.forEach(e -> {
            time.add(e.getTime());
            he.add(e.getHe());
            li.add(e.getLi());
            c.add(e.getC());
            mg.add(e.getMg());
            ar.add(e.getAr());
            fe.add(e.getFe());
        });
        heavyIonFluxDataVO.setTime(time);
        heavyIonFluxDataVO.setHe(he);
        heavyIonFluxDataVO.setLi(li);
        heavyIonFluxDataVO.setC(c);
        heavyIonFluxDataVO.setMg(mg);
        heavyIonFluxDataVO.setAr(ar);
        heavyIonFluxDataVO.setFe(fe);
        return heavyIonFluxDataVO;
    }
}
