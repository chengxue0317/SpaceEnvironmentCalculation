package cn.piesat.sec.controller;

import java.io.Serializable;
import java.util.Arrays;

import cn.piesat.kjyy.common.mybatisplus.annotation.validator.group.AddGroup;
import cn.piesat.kjyy.common.mybatisplus.annotation.validator.group.UpdateGroup;
import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.model.dto.SecApIndexDTO;
import cn.piesat.sec.model.query.SecApIndexQuery;
import cn.piesat.sec.model.vo.EnvElementVO;
import cn.piesat.sec.service.SecApIndexService;
import cn.piesat.sec.model.vo.SecApIndexVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * AP指数
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 15:53:01
 */
@Api(tags = "AP指数")
@RestController
@RequestMapping("/secapindex")
@RequiredArgsConstructor
public class SecApIndexController {

    private final SecApIndexService secApIndexService;

    @ApiOperation("查询一段时间内的AP指数数据")
    @PostMapping("/getApData")
    public EnvElementVO getApData(@RequestParam(value = "startTime", required = false) String startTime,
        @RequestParam(value = "endTime", required = false) String endTime) {
        return secApIndexService.getApData(startTime, endTime);
    }

    @ApiOperation("分页查询")
    @PostMapping("/list")
    public PageResult list(PageBean pageBean, @RequestBody(required = false) SecApIndexQuery secApIndexQuery) {
        return secApIndexService.list(pageBean, secApIndexQuery);
    }

    @ApiOperation("根据id查询")
    @GetMapping("/info/{id}")
    public SecApIndexVO info(@PathVariable("id") Serializable id) {
        return secApIndexService.info(id);
    }

    @ApiOperation("保存信息")
    @PostMapping("/save")
    public Boolean save(@Validated(AddGroup.class) @RequestBody SecApIndexDTO secApIndexDTO) {
        return secApIndexService.save(secApIndexDTO);
    }

    @ApiOperation("修改信息")
    @PutMapping("/update")
    public Boolean update(@Validated(UpdateGroup.class) @RequestBody SecApIndexDTO secApIndexDTO) {
        return secApIndexService.update(secApIndexDTO);
    }

    @ApiOperation("批量删除信息")
    @DeleteMapping("/delete")
    public Boolean delete(@RequestBody Serializable[] ids) {
        return secApIndexService.delete(Arrays.asList(ids));
    }

    @ApiOperation("根据id删除信息")
    @DeleteMapping("/delete/{id}")
    public Boolean delete(@PathVariable Serializable id) {
        return secApIndexService.delete(id);
    }
}
