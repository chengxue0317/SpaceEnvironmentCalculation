package cn.piesat.sec.controller;

import java.io.Serializable;
import java.util.Arrays;
import cn.piesat.kjyy.common.mybatisplus.annotation.validator.group.AddGroup;
import cn.piesat.kjyy.common.mybatisplus.annotation.validator.group.UpdateGroup;
import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.model.dto.SecDstAlarmDTO;
import cn.piesat.sec.model.query.SecDstAlarmQuery;
import cn.piesat.sec.service.SecDstAlarmService;
import cn.piesat.sec.model.vo.SecDstAlarmVO;
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
 * ${comments}
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-12 14:47:57
 */
@Api(tags = "${comments}")
@RestController
@RequestMapping("/secdstalarm")
@RequiredArgsConstructor
public class SecDstAlarmController {

    private final SecDstAlarmService secDstAlarmService;

    @ApiOperation("分页查询")
    @PostMapping("/list")
    public PageResult list(PageBean pageBean, @RequestBody(required = false) SecDstAlarmQuery secDstAlarmQuery){
        return secDstAlarmService.list(pageBean,secDstAlarmQuery);
    }

    @ApiOperation("根据id查询")
    @GetMapping("/info/{id}")
    public SecDstAlarmVO info(@PathVariable("id") Serializable id){
        return secDstAlarmService.info(id);
    }

    @ApiOperation("保存信息")
    @PostMapping("/save")
    public Boolean save(@Validated(AddGroup.class) @RequestBody SecDstAlarmDTO secDstAlarmDTO){
        return secDstAlarmService.save(secDstAlarmDTO);
    }

    @ApiOperation("修改信息")
    @PutMapping("/update")
    public Boolean update(@Validated(UpdateGroup.class) @RequestBody SecDstAlarmDTO secDstAlarmDTO){
        return secDstAlarmService.update(secDstAlarmDTO);
    }

    @ApiOperation("批量删除信息")
    @DeleteMapping("/delete")
    public Boolean delete(@RequestBody Serializable[] ids){
        return secDstAlarmService.delete(Arrays.asList(ids));
    }

    @ApiOperation("根据id删除信息")
    @DeleteMapping("/delete/{id}")
    public Boolean delete(@PathVariable Serializable id){
        return secDstAlarmService.delete(id);
    }
}
