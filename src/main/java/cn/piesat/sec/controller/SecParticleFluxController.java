package cn.piesat.sec.controller;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.piesat.kjyy.common.mybatisplus.annotation.validator.group.AddGroup;
import cn.piesat.kjyy.common.mybatisplus.annotation.validator.group.UpdateGroup;
import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.model.dto.SecParticleFluxDTO;
import cn.piesat.sec.model.entity.SecParticleFluxDO;
import cn.piesat.sec.model.query.SecParticleFluxQuery;
import cn.piesat.sec.model.vo.HeavyIonFluxDataVO;
import cn.piesat.sec.model.vo.SecEnvElementVO;
import cn.piesat.sec.service.SecParticleFluxService;
import cn.piesat.sec.model.vo.SecParticleFluxVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @ApiOperation("查询一段时间内的质子通量数据")
    @PostMapping("/getProtonFluxData")
    public SecEnvElementVO getProtonIndexData(@RequestParam(value = "startTime", required = false) String startTime,
                                              @RequestParam(value = "endTime", required = false) String endTime,
                                              @RequestParam(value = "satId", required = false) String satId) {
        return secParticleFluxService.getProtonFluxData(startTime, endTime, satId);
    }

    @ApiOperation("查询一段时间内的电子通量数据")
    @PostMapping("/getElectronicFluxData")
    public SecEnvElementVO getElectronicFluxData(@RequestParam(value = "startTime", required = false) String startTime,
                                                 @RequestParam(value = "endTime", required = false) String endTime,
                                                 @RequestParam(value = "satId", required = false) String satId) {
        return secParticleFluxService.getElectronicFluxData(startTime, endTime, satId);
    }

    @ApiOperation("查询一段时间内的重离子通量数据")
    @PostMapping("/getHeavyIonFluxData")
    public HeavyIonFluxDataVO getHeavyIonFluxData(@RequestBody(required = false) SecParticleFluxQuery secParticleFluxQuery) {
        QueryWrapper<SecParticleFluxDO> queryWrapper = new QueryWrapper<>();
        String satId = secParticleFluxQuery.getSatId();
        queryWrapper.select("He","Li","C","Mg","Ar","Fe","TIME")
                .eq(StringUtils.isNotBlank(satId),"SAT_ID", satId)
                .between("TIME",secParticleFluxQuery.getTimeBetween().getLeft(),secParticleFluxQuery.getTimeBetween().getRight());
        List<SecParticleFluxDO> list = secParticleFluxService.list(queryWrapper);
        HeavyIonFluxDataVO heavyIonFluxDataVO = new HeavyIonFluxDataVO();
        List<Double> he = new ArrayList<>();
        List<Double> li = new ArrayList<>();
        List<Double> c = new ArrayList<>();
        List<Double> mg = new ArrayList<>();
        List<Double> ar = new ArrayList<>();
        List<Double> fe = new ArrayList<>();
        List<LocalDateTime> time = new ArrayList<>();
        list.forEach(e->{
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

    @ApiOperation("分页查询")
    @PostMapping("/list")
    public PageResult list(PageBean pageBean, @RequestBody(required = false) SecParticleFluxQuery secParticleFluxQuery){
        return secParticleFluxService.list(pageBean,secParticleFluxQuery);
    }

    @ApiOperation("根据id查询")
    @GetMapping("/info/{id}")
    public SecParticleFluxVO info(@PathVariable("id") Serializable id){
        return secParticleFluxService.info(id);
    }

    @ApiOperation("保存信息")
    @PostMapping("/save")
    public Boolean save(@Validated(AddGroup.class) @RequestBody SecParticleFluxDTO secParticleFluxDTO){
        return secParticleFluxService.save(secParticleFluxDTO);
    }

    @ApiOperation("修改信息")
    @PutMapping("/update")
    public Boolean update(@Validated(UpdateGroup.class) @RequestBody SecParticleFluxDTO secParticleFluxDTO){
        return secParticleFluxService.update(secParticleFluxDTO);
    }

    @ApiOperation("批量删除信息")
    @DeleteMapping("/delete")
    public Boolean delete(@RequestBody Serializable[] ids){
        return secParticleFluxService.delete(Arrays.asList(ids));
    }

    @ApiOperation("根据id删除信息")
    @DeleteMapping("/delete/{id}")
    public Boolean delete(@PathVariable Serializable id){
        return secParticleFluxService.delete(id);
    }
}
