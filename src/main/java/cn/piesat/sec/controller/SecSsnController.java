package cn.piesat.sec.controller;

import java.io.Serializable;
import java.util.Arrays;
import cn.piesat.kjyy.common.mybatisplus.annotation.validator.group.AddGroup;
import cn.piesat.kjyy.common.mybatisplus.annotation.validator.group.UpdateGroup;
import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.model.dto.SecSsnDTO;
import cn.piesat.sec.model.query.SecSsnQuery;
import cn.piesat.sec.model.vo.SecEnvElementVO;
import cn.piesat.sec.service.SecSsnService;
import cn.piesat.sec.model.vo.SecSsnVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 太阳黑子数
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-13 20:02:03
 */
@Api(tags = "太阳黑子数")
@RestController
@RequestMapping("/secssn")
@RequiredArgsConstructor
public class SecSsnController {

    private final SecSsnService secSsnService;

    @ApiOperation("获取太阳黑子数")
    @PostMapping("/getSunSpotData")
    public SecEnvElementVO getSunSpotData(
        @RequestParam(value = "startTime", required = false) String startTime,
        @RequestParam(value = "endTime", required = false) String endTime
    ) {
        return secSsnService.getSunSpotData(startTime, endTime);
    }

    @ApiOperation("分页查询")
    @PostMapping("/list")
    public PageResult list(PageBean pageBean, @RequestBody(required = false) SecSsnQuery secSsnQuery){
        return secSsnService.list(pageBean,secSsnQuery);
    }

    @ApiOperation("根据id查询")
    @GetMapping("/info/{id}")
    public SecSsnVO info(@PathVariable("id") Serializable id){
        return secSsnService.info(id);
    }

    @ApiOperation("保存信息")
    @PostMapping("/save")
    public Boolean save(@Validated(AddGroup.class) @RequestBody SecSsnDTO secSsnDTO){
        return secSsnService.save(secSsnDTO);
    }

    @ApiOperation("修改信息")
    @PutMapping("/update")
    public Boolean update(@Validated(UpdateGroup.class) @RequestBody SecSsnDTO secSsnDTO){
        return secSsnService.update(secSsnDTO);
    }

    @ApiOperation("批量删除信息")
    @DeleteMapping("/delete")
    public Boolean delete(@RequestBody Serializable[] ids){
        return secSsnService.delete(Arrays.asList(ids));
    }

    @ApiOperation("根据id删除信息")
    @DeleteMapping("/delete/{id}")
    public Boolean delete(@PathVariable Serializable id){
        return secSsnService.delete(id);
    }
}
