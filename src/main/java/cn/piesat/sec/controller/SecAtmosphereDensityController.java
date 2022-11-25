package cn.piesat.sec.controller;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.piesat.kjyy.common.mybatisplus.annotation.validator.group.AddGroup;
import cn.piesat.kjyy.common.mybatisplus.annotation.validator.group.UpdateGroup;
import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.model.dto.SecAtmosphereDensityDTO;
import cn.piesat.sec.model.entity.SecAtmosphereDensityDO;
import cn.piesat.sec.model.query.SecAtmosphereDensityQuery;
import cn.piesat.sec.service.SecAtmosphereDensityService;
import cn.piesat.sec.model.vo.SecAtmosphereDensityVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 大气密度预报模块
 *
 * @author sw
 * @email shiwang@piesat.cn
 * @date 2022-11-16 14:58:35
 */
@Api(tags = "大气密度预报模块")
@RestController
@RequestMapping("/secatmospheredensity")
@RequiredArgsConstructor
public class SecAtmosphereDensityController {

    private final SecAtmosphereDensityService secAtmosphereDensityService;

    @ApiOperation("分页查询")
    @PostMapping("/list")
    public PageResult list(PageBean pageBean, @RequestBody(required = false) SecAtmosphereDensityQuery secAtmosphereDensityQuery){
        return secAtmosphereDensityService.list(pageBean,secAtmosphereDensityQuery);
    }

    @ApiOperation("根据id查询")
    @GetMapping("/info/{id}")
    public SecAtmosphereDensityVO info(@PathVariable("id") Serializable id){
        return secAtmosphereDensityService.info(id);
    }

    @ApiOperation("保存信息")
    @PostMapping("/save")
    public Boolean save(@Validated(AddGroup.class) @RequestBody SecAtmosphereDensityDTO secAtmosphereDensityDTO){
        return secAtmosphereDensityService.save(secAtmosphereDensityDTO);
    }

    @ApiOperation("修改信息")
    @PutMapping("/update")
    public Boolean update(@Validated(UpdateGroup.class) @RequestBody SecAtmosphereDensityDTO secAtmosphereDensityDTO){
        return secAtmosphereDensityService.update(secAtmosphereDensityDTO);
    }

    @ApiOperation("批量删除信息")
    @DeleteMapping("/delete")
    public Boolean delete(@RequestBody Serializable[] ids){
        return secAtmosphereDensityService.delete(Arrays.asList(ids));
    }

    @ApiOperation("根据id删除信息")
    @DeleteMapping("/delete/{id}")
    public Boolean delete(@PathVariable Serializable id){
        return secAtmosphereDensityService.delete(id);
    }

    @ApiOperation("大气密度曲线图")
    @PostMapping("/getData")
    public Map getData(@RequestBody(required = false) SecAtmosphereDensityQuery secAtmosphereDensityQuery){
        QueryWrapper<SecAtmosphereDensityDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("TIME","DENSITY").between("TIME",secAtmosphereDensityQuery.getTimeBetween().getLeft(),secAtmosphereDensityQuery.getTimeBetween().getRight());
        List<SecAtmosphereDensityDO> list = secAtmosphereDensityService.list(queryWrapper);
        List<String> times = new ArrayList<>();
        List<Double> densitys = new ArrayList<>();
        list.forEach(e->{
            densitys.add(e.getDensity());
            times.add(e.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        });
        Map map = new HashMap();
        map.put("times",times);
        map.put("densitys",densitys);
        return map;
    }
}
