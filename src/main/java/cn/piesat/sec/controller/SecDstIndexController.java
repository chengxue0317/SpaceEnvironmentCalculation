package cn.piesat.sec.controller;

import java.util.Arrays;

import cn.piesat.kjyy.common.mybatisplus.annotation.validator.group.AddGroup ;
import cn.piesat.kjyy.common.mybatisplus.annotation.validator.group.UpdateGroup;
import cn.piesat.kjyy.core.model.dto.PageBean ;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.model.entity.SecDstIndexDO;
import cn.piesat.sec.model.vo.EnvElementVO;
import cn.piesat.sec.service.SecDstIndexService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * DST指数现报数据
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 17:54:18
 */
@Api(tags = "DST指数现报数据")
@RestController
@RequestMapping("/secdstindex")
@RequiredArgsConstructor
public class SecDstIndexController {

    private final SecDstIndexService secDstIndexService;

    @ApiOperation("查询一段时间内的DST数据")
    @PostMapping("/getDstData")
    public EnvElementVO getDstData(@RequestParam(value = "startTime", required = false) String startTime,
        @RequestParam(value = "endTime", required = false) String endTime) {
        return secDstIndexService.getDstData(startTime, endTime);
    }
    /**
     * 列表
     */
    @ApiOperation("分页查询")
    @PostMapping("/list")
    public PageResult list(PageBean pageBean, @RequestBody(required = false) SecDstIndexDO secDstIndexDO){
        return secDstIndexService.list(pageBean,secDstIndexDO);

    }
    /**
     * 信息
     */
    @ApiOperation("根据id查询")
    @GetMapping("/info/{id}")
    public SecDstIndexDO info(@PathVariable("id") Long id){
        return secDstIndexService.info(id);
    }
    /**
     * 保存
     */
    @ApiOperation("保存信息")
    @PostMapping("/save")
    public Boolean save(@Validated(AddGroup.class) @RequestBody SecDstIndexDO secDstIndexDO){
        return secDstIndexService.add(secDstIndexDO);
    }

    /**
     * 修改
     */
    @ApiOperation("修改信息")
    @PutMapping("/update")
    public Boolean update(@Validated(UpdateGroup.class) @RequestBody SecDstIndexDO secDstIndexDO){
        return secDstIndexService.update(secDstIndexDO);
    }

    /**
     * 删除
     */
    @ApiOperation("批量删除信息")
    @DeleteMapping("/delete")
    public Boolean delete(@RequestBody Long[] ids){
        return secDstIndexService.delete(Arrays.asList(ids));
    }
    /**
     * 删除
     */
    @ApiOperation("根据id删除信息")
    @DeleteMapping("/delete/{id}")
    public Boolean delete(@PathVariable Long id){
        return secDstIndexService.delete(id);
    }
}
