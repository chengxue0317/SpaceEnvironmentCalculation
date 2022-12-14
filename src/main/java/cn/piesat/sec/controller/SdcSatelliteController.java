package cn.piesat.sec.controller;

import cn.piesat.sec.model.vo.SdcResourceSatelliteVO;
import cn.piesat.sec.service.SdcResourceSatelliteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 卫星基本信息
 *
 * @author sw
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 16:35:45
 */
@Api(tags = "卫星基本信息")
@RestController
@RequestMapping("/secsatellite")
@RequiredArgsConstructor
public class SdcSatelliteController {
    private final SdcResourceSatelliteService sdcResourceSatelliteService;

    @ApiOperation("查询所有卫星数据")
    @GetMapping("/allSatellites")
    public List<SdcResourceSatelliteVO> getAllSatellites() {
        return sdcResourceSatelliteService.getAllList();
    }
}
