package cn.piesat.sec.controller;

import java.io.Serializable;
import java.util.Arrays;
import cn.piesat.kjyy.common.mybatisplus.annotation.validator.group.AddGroup;
import cn.piesat.kjyy.common.mybatisplus.annotation.validator.group.UpdateGroup;
import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.model.dto.ProtonAlarmDTO;
import cn.piesat.sec.model.query.ProtonAlarmQuery;
import cn.piesat.sec.service.ProtonAlarmService;
import cn.piesat.sec.model.vo.ProtonAlarmVO;
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
 * 太阳质子事件
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-08 23:02:08
 */
@Api(tags = "太阳质子事件")
@RestController
@RequestMapping("/protonalarm")
@RequiredArgsConstructor
public class ProtonAlarmController {

    private final ProtonAlarmService protonAlarmService;

    @ApiOperation("分页查询")
    @PostMapping("/list")
    public PageResult list(PageBean pageBean, @RequestBody(required = false) ProtonAlarmQuery protonAlarmQuery){
        return protonAlarmService.list(pageBean,protonAlarmQuery);
    }

    @ApiOperation("根据id查询")
    @GetMapping("/info/{id}")
    public ProtonAlarmVO info(@PathVariable("id") Serializable id){
        return protonAlarmService.info(id);
    }

    @ApiOperation("保存信息")
    @PostMapping("/save")
    public Boolean save(@Validated(AddGroup.class) @RequestBody ProtonAlarmDTO protonAlarmDTO){
        return protonAlarmService.save(protonAlarmDTO);
    }

    @ApiOperation("修改信息")
    @PutMapping("/update")
    public Boolean update(@Validated(UpdateGroup.class) @RequestBody ProtonAlarmDTO protonAlarmDTO){
        return protonAlarmService.update(protonAlarmDTO);
    }

    @ApiOperation("批量删除信息")
    @DeleteMapping("/delete")
    public Boolean delete(@RequestBody Serializable[] ids){
        return protonAlarmService.delete(Arrays.asList(ids));
    }

    @ApiOperation("根据id删除信息")
    @DeleteMapping("/delete/{id}")
    public Boolean delete(@PathVariable Serializable id){
        return protonAlarmService.delete(id);
    }
}
