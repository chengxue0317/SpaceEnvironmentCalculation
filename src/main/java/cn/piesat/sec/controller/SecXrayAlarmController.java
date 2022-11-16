package cn.piesat.sec.controller;

import java.io.Serializable;
import java.util.Arrays;
import cn.piesat.kjyy.common.mybatisplus.annotation.validator.group.AddGroup;
import cn.piesat.kjyy.common.mybatisplus.annotation.validator.group.UpdateGroup;
import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.model.dto.SecXrayAlarmDTO;
import cn.piesat.sec.model.query.SecXrayAlarmQuery;
import cn.piesat.sec.service.SecXrayAlarmService;
import cn.piesat.sec.model.vo.SecXrayAlarmVO;
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
 * 太阳X射线耀斑警报事件
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-12 14:46:40
 */
@Api(tags = "太阳X射线耀斑警报事件")
@RestController
@RequestMapping("/secxrayalarm")
@RequiredArgsConstructor
public class SecXrayAlarmController {

    private final SecXrayAlarmService secXrayAlarmService;

    @ApiOperation("分页查询")
    @PostMapping("/list")
    public PageResult list(PageBean pageBean, @RequestBody(required = false) SecXrayAlarmQuery secXrayAlarmQuery){
        return secXrayAlarmService.list(pageBean,secXrayAlarmQuery);
    }

    @ApiOperation("根据id查询")
    @GetMapping("/info/{id}")
    public SecXrayAlarmVO info(@PathVariable("id") Serializable id){
        return secXrayAlarmService.info(id);
    }

    @ApiOperation("保存信息")
    @PostMapping("/save")
    public Boolean save(@Validated(AddGroup.class) @RequestBody SecXrayAlarmDTO secXrayAlarmDTO){
        return secXrayAlarmService.save(secXrayAlarmDTO);
    }

    @ApiOperation("修改信息")
    @PutMapping("/update")
    public Boolean update(@Validated(UpdateGroup.class) @RequestBody SecXrayAlarmDTO secXrayAlarmDTO){
        return secXrayAlarmService.update(secXrayAlarmDTO);
    }

    @ApiOperation("批量删除信息")
    @DeleteMapping("/delete")
    public Boolean delete(@RequestBody Serializable[] ids){
        return secXrayAlarmService.delete(Arrays.asList(ids));
    }

    @ApiOperation("根据id删除信息")
    @DeleteMapping("/delete/{id}")
    public Boolean delete(@PathVariable Serializable id){
        return secXrayAlarmService.delete(id);
    }
}