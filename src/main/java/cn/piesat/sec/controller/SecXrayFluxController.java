package cn.piesat.sec.controller;

import java.io.Serializable;
import java.util.Arrays;
import cn.piesat.kjyy.common.mybatisplus.annotation.validator.group.AddGroup;
import cn.piesat.kjyy.common.mybatisplus.annotation.validator.group.UpdateGroup;
import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.model.dto.SecXrayFluxDTO;
import cn.piesat.sec.model.query.SecXrayFluxQuery;
import cn.piesat.sec.model.vo.EnvElementVO;
import cn.piesat.sec.service.SecXrayFluxService;
import cn.piesat.sec.model.vo.SecXrayFluxVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 太阳X射线流量
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 12:16:14
 */
@Api(tags = "太阳X射线流量")
@RestController
@RequestMapping("/secxrayflux")
@RequiredArgsConstructor
public class SecXrayFluxController {

    private final SecXrayFluxService secXrayFluxService;

    @ApiOperation("获取太阳X射线流量")
    @PostMapping("/getSolarXrayData")
    public EnvElementVO getSolarXrayData(
        @RequestParam(value = "startTime", required = false) String startTime,
        @RequestParam(value = "endTime", required = false) String endTime
    ) {
        return secXrayFluxService.getSolarXrayData(startTime, endTime);
    }

    @ApiOperation("分页查询")
    @PostMapping("/list")
    public PageResult list(PageBean pageBean, @RequestBody(required = false) SecXrayFluxQuery secXrayFluxQuery){
        return secXrayFluxService.list(pageBean,secXrayFluxQuery);
    }

    @ApiOperation("根据id查询")
    @GetMapping("/info/{id}")
    public SecXrayFluxVO info(@PathVariable("id") Serializable id){
        return secXrayFluxService.info(id);
    }

    @ApiOperation("保存信息")
    @PostMapping("/save")
    public Boolean save(@Validated(AddGroup.class) @RequestBody SecXrayFluxDTO secXrayFluxDTO){
        return secXrayFluxService.save(secXrayFluxDTO);
    }

    @ApiOperation("修改信息")
    @PutMapping("/update")
    public Boolean update(@Validated(UpdateGroup.class) @RequestBody SecXrayFluxDTO secXrayFluxDTO){
        return secXrayFluxService.update(secXrayFluxDTO);
    }

    @ApiOperation("批量删除信息")
    @DeleteMapping("/delete")
    public Boolean delete(@RequestBody Serializable[] ids){
        return secXrayFluxService.delete(Arrays.asList(ids));
    }

    @ApiOperation("根据id删除信息")
    @DeleteMapping("/delete/{id}")
    public Boolean delete(@PathVariable Serializable id){
        return secXrayFluxService.delete(id);
    }
}
