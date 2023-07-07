package cn.piesat.sec.controller;

import cn.piesat.kjyy.common.web.annotation.validator.group.AddGroup;
import cn.piesat.kjyy.common.web.annotation.validator.group.UpdateGroup;
import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.model.dto.SecShieldose2DTO;
import cn.piesat.sec.model.entity.SecShieldose2DO;
import cn.piesat.sec.model.query.SecShieldose2Query;
import cn.piesat.sec.model.vo.SecShieldose2VO;
import cn.piesat.sec.service.SecShieldose2Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.*;

/**
 * ${comments}
 *
 * @author sw
 * @email shiwang@piesat.cn
 * @date 2022-12-02 13:24:52
 */
@Api(tags = "${comments}")
@RestController
@RequestMapping("/secshieldose2")
@RequiredArgsConstructor
public class SecShieldose2Controller {

    private final SecShieldose2Service secShieldose2Service;

    @ApiOperation("分页查询")
    @PostMapping("/list")
    public PageResult list(PageBean pageBean, @RequestBody(required = false) SecShieldose2Query secShieldose2Query){
        return secShieldose2Service.list(pageBean,secShieldose2Query);
    }

    @ApiOperation("根据id查询")
    @GetMapping("/info/{id}")
    public SecShieldose2VO info(@PathVariable("id") Serializable id){
        return secShieldose2Service.info(id);
    }

    @ApiOperation("保存信息")
    @PostMapping("/save")
    public Boolean save(@Validated(AddGroup.class) @RequestBody SecShieldose2DTO secShieldose2DTO){
        return secShieldose2Service.save(secShieldose2DTO);
    }

    @ApiOperation("修改信息")
    @PutMapping("/update")
    public Boolean update(@Validated(UpdateGroup.class) @RequestBody SecShieldose2DTO secShieldose2DTO){
        return secShieldose2Service.update(secShieldose2DTO);
    }

    @ApiOperation("批量删除信息")
    @DeleteMapping("/delete")
    public Boolean delete(@RequestBody Serializable[] ids){
        return secShieldose2Service.delete(Arrays.asList(ids));
    }

    @ApiOperation("根据id删除信息")
    @DeleteMapping("/delete/{id}")
    public Boolean delete(@PathVariable Serializable id){
        return secShieldose2Service.delete(id);
    }

    @ApiOperation("卫星辐射总剂量计算评估")
    @PostMapping("/getData")
    public Map getData(@RequestBody(required = false) SecShieldose2Query secShieldose2Query){
        QueryWrapper<SecShieldose2DO> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("DOES","DEPTH")
                .eq("SAT_ID",secShieldose2Query.getSatId())
                .eq("MATERIAL",secShieldose2Query.getMaterial())
                .eq("MODE",secShieldose2Query.getMode())
                .between("TIME",secShieldose2Query.getTimeBetween().getLeft(),secShieldose2Query.getTimeBetween().getRight());
        List<SecShieldose2DO> list = secShieldose2Service.list(queryWrapper);
        List<Double> does = new ArrayList<>();
        List<Double> depth = new ArrayList<>();
        list.forEach(e->{
            does.add(e.getDoes());
            depth.add(e.getDepth());
        });
        Map map = new HashMap();
        map.put("does",does);
        map.put("depth",depth);
        return map;
    }
}
