package cn.piesat.sec.controller;

import cn.piesat.kjyy.common.mybatisplus.annotation.validator.group.AddGroup;
import cn.piesat.kjyy.common.mybatisplus.annotation.validator.group.UpdateGroup;
import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.model.dto.SdcResourceStationDTO;
import cn.piesat.sec.model.query.SdcResourceStationQuery;
import cn.piesat.sec.model.vo.SdcResourceStationVO;
import cn.piesat.sec.service.SdcResourceStationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * 测站基本信息
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-12-13 14:18:47
 */
@Api(tags = "测站基本信息")
@RestController
@RequestMapping("/secResourcestation")
@RequiredArgsConstructor
public class SdcResourceStationController {

    private final SdcResourceStationService sdcResourceStationService;

    @ApiOperation("所有测站数据列表")
    @GetMapping ("/allList")
    public List<SdcResourceStationVO> getAllList() {
        return sdcResourceStationService.getAllList();
    }

    @ApiOperation("分页查询")
    @PostMapping("/list")
    public PageResult list(PageBean pageBean, @RequestBody(required = false) SdcResourceStationQuery sdcResourceStationQuery) {
        return sdcResourceStationService.list(pageBean, sdcResourceStationQuery);
    }

    @ApiOperation("根据id查询")
    @GetMapping("/info/{id}")
    public SdcResourceStationVO info(@PathVariable("id") Serializable id) {
        return sdcResourceStationService.info(id);
    }

    @ApiOperation("保存信息")
    @PostMapping("/save")
    public Boolean save(@Validated(AddGroup.class) @RequestBody SdcResourceStationDTO sdcResourceStationDTO) {
        return sdcResourceStationService.save(sdcResourceStationDTO);
    }

    @ApiOperation("修改信息")
    @PutMapping("/update")
    public Boolean update(@Validated(UpdateGroup.class) @RequestBody SdcResourceStationDTO sdcResourceStationDTO) {
        return sdcResourceStationService.update(sdcResourceStationDTO);
    }

    @ApiOperation("批量删除信息")
    @DeleteMapping("/delete")
    public Boolean delete(@RequestBody Serializable[] ids) {
        return sdcResourceStationService.delete(Arrays.asList(ids));
    }

    @ApiOperation("根据id删除信息")
    @DeleteMapping("/delete/{id}")
    public Boolean delete(@PathVariable Serializable id) {
        return sdcResourceStationService.delete(id);
    }
}
