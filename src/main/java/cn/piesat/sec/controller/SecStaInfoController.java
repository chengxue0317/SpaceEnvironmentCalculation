package cn.piesat.sec.controller;

import java.io.Serializable;
import java.util.Arrays;
import cn.piesat.kjyy.common.mybatisplus.annotation.validator.group.AddGroup;
import cn.piesat.kjyy.common.mybatisplus.annotation.validator.group.UpdateGroup;
import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.model.dto.SecStaInfoDTO;
import cn.piesat.sec.model.query.SecStaInfoQuery;
import cn.piesat.sec.service.SecStaInfoService;
import cn.piesat.sec.model.vo.SecStaInfoVO;
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
 * 台站信息表
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-27 10:51:47
 */
@Api(tags = "台站信息表")
@RestController
@RequestMapping("/secstainfo")
@RequiredArgsConstructor
public class SecStaInfoController {

    private final SecStaInfoService secStaInfoService;

    @ApiOperation("分页查询")
    @PostMapping("/list")
    public PageResult list(PageBean pageBean, @RequestBody(required = false) SecStaInfoQuery secStaInfoQuery){
        return secStaInfoService.list(pageBean,secStaInfoQuery);
    }

    @ApiOperation("根据id查询")
    @GetMapping("/info/{id}")
    public SecStaInfoVO info(@PathVariable("id") Serializable id){
        return secStaInfoService.info(id);
    }

    @ApiOperation("保存信息")
    @PostMapping("/save")
    public Boolean save(@Validated(AddGroup.class) @RequestBody SecStaInfoDTO secStaInfoDTO){
        return secStaInfoService.save(secStaInfoDTO);
    }

    @ApiOperation("修改信息")
    @PutMapping("/update")
    public Boolean update(@Validated(UpdateGroup.class) @RequestBody SecStaInfoDTO secStaInfoDTO){
        return secStaInfoService.update(secStaInfoDTO);
    }

    @ApiOperation("批量删除信息")
    @DeleteMapping("/delete")
    public Boolean delete(@RequestBody Serializable[] ids){
        return secStaInfoService.delete(Arrays.asList(ids));
    }

    @ApiOperation("根据id删除信息")
    @DeleteMapping("/delete/{id}")
    public Boolean delete(@PathVariable Serializable id){
        return secStaInfoService.delete(id);
    }
}
