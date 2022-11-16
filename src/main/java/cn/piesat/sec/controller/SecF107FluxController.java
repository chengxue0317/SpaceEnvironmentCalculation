package cn.piesat.sec.controller;

import java.io.Serializable;
import java.util.Arrays;
import cn.piesat.kjyy.common.mybatisplus.annotation.validator.group.AddGroup;
import cn.piesat.kjyy.common.mybatisplus.annotation.validator.group.UpdateGroup;
import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.model.dto.SecF107FluxDTO;
import cn.piesat.sec.model.query.SecF107FluxQuery;
import cn.piesat.sec.model.vo.EnvElementVO;
import cn.piesat.sec.service.SecF107FluxService;
import cn.piesat.sec.model.vo.SecF107FluxVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 太阳F10.7指数
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 20:49:13
 */
@Api(tags = "太阳F10.7指数")
@RestController
@RequestMapping("/secf107flux")
@RequiredArgsConstructor
public class SecF107FluxController {

    private final SecF107FluxService secF107FluxService;

    @ApiOperation("查询一段时间内的F10.7数据")
    @PostMapping("/getF107Data")
    public EnvElementVO getF107Data(@RequestParam(value = "startTime", required = false) String startTime,
        @RequestParam(value = "endTime", required = false) String endTime) {
        return secF107FluxService.getF107Data(startTime, endTime);
    }

    @ApiOperation("分页查询")
    @PostMapping("/list")
    public PageResult list(PageBean pageBean, @RequestBody(required = false) SecF107FluxQuery secF107FluxQuery){
        return secF107FluxService.list(pageBean,secF107FluxQuery);
    }

    @ApiOperation("根据id查询")
    @GetMapping("/info/{id}")
    public SecF107FluxVO info(@PathVariable("id") Serializable id){
        return secF107FluxService.info(id);
    }

    @ApiOperation("保存信息")
    @PostMapping("/save")
    public Boolean save(@Validated(AddGroup.class) @RequestBody SecF107FluxDTO secF107FluxDTO){
        return secF107FluxService.save(secF107FluxDTO);
    }

    @ApiOperation("修改信息")
    @PutMapping("/update")
    public Boolean update(@Validated(UpdateGroup.class) @RequestBody SecF107FluxDTO secF107FluxDTO){
        return secF107FluxService.update(secF107FluxDTO);
    }

    @ApiOperation("批量删除信息")
    @DeleteMapping("/delete")
    public Boolean delete(@RequestBody Serializable[] ids){
        return secF107FluxService.delete(Arrays.asList(ids));
    }

    @ApiOperation("根据id删除信息")
    @DeleteMapping("/delete/{id}")
    public Boolean delete(@PathVariable Serializable id){
        return secF107FluxService.delete(id);
    }
}
