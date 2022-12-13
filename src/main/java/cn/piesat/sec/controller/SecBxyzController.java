package cn.piesat.sec.controller;

import cn.piesat.kjyy.common.mybatisplus.annotation.validator.group.AddGroup;
import cn.piesat.kjyy.common.mybatisplus.annotation.validator.group.UpdateGroup;
import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.model.dto.SecBxyzDTO;
import cn.piesat.sec.model.query.SecMagneticParamterQuery;
import cn.piesat.sec.model.vo.SecEnvElementVO;
import cn.piesat.sec.model.vo.SecMagneticParamterVO;
import cn.piesat.sec.service.SecBxyzService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 地磁参数数据
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 21:52:42
 */
@Api(tags = "地磁参数数据")
@RestController
@RequestMapping("/secmagneticparamter")
@RequiredArgsConstructor
public class SecBxyzController {

    private final SecBxyzService secBxyzService;

    @ApiOperation("查询一段时间内的磁场数据")
    @GetMapping("/btxyzData")
    public SecEnvElementVO getBtxyzData(@RequestParam(value = "startTime", required = false) String startTime,
                                        @RequestParam(value = "endTime", required = false) String endTime) {
        return secBxyzService.getBtxyzData(startTime, endTime);
    }
    @ApiOperation("分页查询")
    @PostMapping("/list")
    public PageResult list(PageBean pageBean, @RequestBody(required = false) SecMagneticParamterQuery secMagneticParamterQuery){
        return secBxyzService.list(pageBean,secMagneticParamterQuery);
    }

    @ApiOperation("根据id查询")
    @GetMapping("/info/{id}")
    public SecMagneticParamterVO info(@PathVariable("id") Serializable id){
        return secBxyzService.info(id);
    }

    @ApiOperation("保存信息")
    @PostMapping("/save")
    public Boolean save(@Validated(AddGroup.class) @RequestBody SecBxyzDTO secBxyzDTO){
        return secBxyzService.save(secBxyzDTO);
    }

    @ApiOperation("修改信息")
    @PutMapping("/update")
    public Boolean update(@Validated(UpdateGroup.class) @RequestBody SecBxyzDTO secBxyzDTO){
        return secBxyzService.update(secBxyzDTO);
    }

    @ApiOperation("批量删除信息")
    @DeleteMapping("/delete")
    public Boolean delete(@RequestBody Serializable[] ids){
        return secBxyzService.delete(Arrays.asList(ids));
    }

    @ApiOperation("根据id删除信息")
    @DeleteMapping("/delete/{id}")
    public Boolean delete(@PathVariable Serializable id){
        return secBxyzService.delete(id);
    }
}
