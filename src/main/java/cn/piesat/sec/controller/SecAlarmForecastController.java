package cn.piesat.sec.controller;

import java.io.Serializable;
import java.util.Arrays;
import cn.piesat.kjyy.common.mybatisplus.annotation.validator.group.AddGroup;
import cn.piesat.kjyy.common.mybatisplus.annotation.validator.group.UpdateGroup;
import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.model.dto.SecAlarmForecastDTO;
import cn.piesat.sec.model.query.SecAlarmForecastQuery;
import cn.piesat.sec.service.SecAlarmForecastService;
import cn.piesat.sec.model.vo.SecAlarmForecastVO;
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
 * 未来三天警报预报表
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-12 14:48:52
 */
@Api(tags = "未来三天警报预报表")
@RestController
@RequestMapping("/secalarmforecast")
@RequiredArgsConstructor
public class SecAlarmForecastController {

    private final SecAlarmForecastService secAlarmForecastService;

    @ApiOperation("分页查询")
    @PostMapping("/list")
    public PageResult list(PageBean pageBean, @RequestBody(required = false) SecAlarmForecastQuery secAlarmForecastQuery){
        return secAlarmForecastService.list(pageBean,secAlarmForecastQuery);
    }

    @ApiOperation("根据id查询")
    @GetMapping("/info/{id}")
    public SecAlarmForecastVO info(@PathVariable("id") Serializable id){
        return secAlarmForecastService.info(id);
    }

    @ApiOperation("保存信息")
    @PostMapping("/save")
    public Boolean save(@Validated(AddGroup.class) @RequestBody SecAlarmForecastDTO secAlarmForecastDTO){
        return secAlarmForecastService.save(secAlarmForecastDTO);
    }

    @ApiOperation("修改信息")
    @PutMapping("/update")
    public Boolean update(@Validated(UpdateGroup.class) @RequestBody SecAlarmForecastDTO secAlarmForecastDTO){
        return secAlarmForecastService.update(secAlarmForecastDTO);
    }

    @ApiOperation("批量删除信息")
    @DeleteMapping("/delete")
    public Boolean delete(@RequestBody Serializable[] ids){
        return secAlarmForecastService.delete(Arrays.asList(ids));
    }

    @ApiOperation("根据id删除信息")
    @DeleteMapping("/delete/{id}")
    public Boolean delete(@PathVariable Serializable id){
        return secAlarmForecastService.delete(id);
    }
}
