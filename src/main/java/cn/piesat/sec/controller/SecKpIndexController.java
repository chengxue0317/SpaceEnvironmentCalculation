package cn.piesat.sec.controller;

import java.io.Serializable;
import java.util.Arrays;
import cn.piesat.kjyy.common.mybatisplus.annotation.validator.group.AddGroup;
import cn.piesat.kjyy.common.mybatisplus.annotation.validator.group.UpdateGroup;
import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.model.dto.SecKpIndexDTO;
import cn.piesat.sec.model.query.SecKpIndexQuery;
import cn.piesat.sec.model.vo.EnvElementVO;
import cn.piesat.sec.service.SecKpIndexService;
import cn.piesat.sec.model.vo.SecKpIndexVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * KP指数
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 16:57:26
 */
@Api(tags = "KP指数")
@RestController
@RequestMapping("/seckpindex")
@RequiredArgsConstructor
public class SecKpIndexController {

    private final SecKpIndexService secKpIndexService;

    @ApiOperation("查询一段时间内的KP指数数据")
    @PostMapping("/getKpData")
    public EnvElementVO getKpData(@RequestParam(value = "startTime", required = false) String startTime,
        @RequestParam(value = "endTime", required = false) String endTime) {
        return secKpIndexService.getKpData(startTime, endTime);
    }

    @ApiOperation("分页查询")
    @PostMapping("/list")
    public PageResult list(PageBean pageBean, @RequestBody(required = false) SecKpIndexQuery secKpIndexQuery){
        return secKpIndexService.list(pageBean,secKpIndexQuery);
    }

    @ApiOperation("根据id查询")
    @GetMapping("/info/{id}")
    public SecKpIndexVO info(@PathVariable("id") Serializable id){
        return secKpIndexService.info(id);
    }

    @ApiOperation("保存信息")
    @PostMapping("/save")
    public Boolean save(@Validated(AddGroup.class) @RequestBody SecKpIndexDTO secKpIndexDTO){
        return secKpIndexService.save(secKpIndexDTO);
    }

    @ApiOperation("修改信息")
    @PutMapping("/update")
    public Boolean update(@Validated(UpdateGroup.class) @RequestBody SecKpIndexDTO secKpIndexDTO){
        return secKpIndexService.update(secKpIndexDTO);
    }

    @ApiOperation("批量删除信息")
    @DeleteMapping("/delete")
    public Boolean delete(@RequestBody Serializable[] ids){
        return secKpIndexService.delete(Arrays.asList(ids));
    }

    @ApiOperation("根据id删除信息")
    @DeleteMapping("/delete/{id}")
    public Boolean delete(@PathVariable Serializable id){
        return secKpIndexService.delete(id);
    }
}
