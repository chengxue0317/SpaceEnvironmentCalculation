package cn.piesat.sec.controller;

import java.io.Serializable;
import java.util.Arrays;
import cn.piesat.kjyy.common.mybatisplus.annotation.validator.group.AddGroup;
import cn.piesat.kjyy.common.mybatisplus.annotation.validator.group.UpdateGroup;
import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.model.dto.SecSolarWindDTO;
import cn.piesat.sec.model.query.SecSolarWindQuery;
import cn.piesat.sec.model.vo.EnvElementVO;
import cn.piesat.sec.service.SecSolarWindService;
import cn.piesat.sec.model.vo.SecSolarWindVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    public EnvElementVO getSolarWindData(@RequestParam(value = "startTime", required = false) String startTime,
        @RequestParam(value = "endTime", required = false) String endTime) {
        return secSolarWindService.getSolarWindData(startTime, endTime);
    }

    @ApiOperation("分页查询")
    @PostMapping("/list")
    public PageResult list(PageBean pageBean, @RequestBody(required = false) SecSolarWindQuery secSolarWindQuery){
        return secSolarWindService.list(pageBean,secSolarWindQuery);
    }

    @ApiOperation("根据id查询")
    @GetMapping("/info/{id}")
    public SecSolarWindVO info(@PathVariable("id") Serializable id){
        return secSolarWindService.info(id);
    }

    @ApiOperation("保存信息")
    @PostMapping("/save")
    public Boolean save(@Validated(AddGroup.class) @RequestBody SecSolarWindDTO secSolarWindDTO){
        return secSolarWindService.save(secSolarWindDTO);
    }

    @ApiOperation("修改信息")
    @PutMapping("/update")
    public Boolean update(@Validated(UpdateGroup.class) @RequestBody SecSolarWindDTO secSolarWindDTO){
        return secSolarWindService.update(secSolarWindDTO);
    }

    @ApiOperation("批量删除信息")
    @DeleteMapping("/delete")
    public Boolean delete(@RequestBody Serializable[] ids){
        return secSolarWindService.delete(Arrays.asList(ids));
    }

    @ApiOperation("根据id删除信息")
    @DeleteMapping("/delete/{id}")
    public Boolean delete(@PathVariable Serializable id){
        return secSolarWindService.delete(id);
    }
}
