package cn.piesat.sec.controller;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.piesat.kjyy.common.mybatisplus.annotation.validator.group.AddGroup;
import cn.piesat.kjyy.common.mybatisplus.annotation.validator.group.UpdateGroup;
import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.model.dto.SdcResourceSatelliteDTO;
import cn.piesat.sec.model.entity.SdcResourceSatelliteDO;
import cn.piesat.sec.model.query.SdcResourceSatelliteQuery;
import cn.piesat.sec.model.vo.MagneticOrbitVO;
import cn.piesat.sec.model.vo.SdcResourceSatelliteVO;
import cn.piesat.sec.service.SdcResourceSatelliteService;
import cn.piesat.sec.utils.ExecUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 卫星基本信息
 *
 * @author sw
 * @email shiwang@piesat.cn
 * @date 2022-11-14 16:35:45
 */
@Api(tags = "卫星基本信息")
@RestController
@RequestMapping("/sdcresourcesatellite")
@RequiredArgsConstructor
public class SdcResourceSatelliteController {

    private static final Logger log = LoggerFactory.getLogger(SdcResourceSatelliteController.class);

    @Value("${python.path.magnetic_global}")
    private String pythonMagneticGlobal;
    @Value("${picture.path.magnetic_global}")
    private String pictureMagneticGlobal;
    @Value("${python.path.orbital_magnetic}")
    private String pythonOrbitalMagnetic;

    private final SdcResourceSatelliteService sdcResourceSatelliteService;


    @ApiOperation("分页查询")
    @PostMapping("/list")
    public PageResult list(PageBean pageBean, @RequestBody(required = false) SdcResourceSatelliteQuery sdcResourceSatelliteQuery){
        return sdcResourceSatelliteService.list(pageBean,sdcResourceSatelliteQuery);
    }

    @ApiOperation("根据id查询")
    @GetMapping("/info/{id}")
    public SdcResourceSatelliteVO info(@PathVariable("id") Serializable id){
        return sdcResourceSatelliteService.info(id);
    }

    @ApiOperation("保存信息")
    @PostMapping("/save")
    public Boolean save(@Validated(AddGroup.class) @RequestBody SdcResourceSatelliteDTO sdcResourceSatelliteDTO){
        return sdcResourceSatelliteService.save(sdcResourceSatelliteDTO);
    }

    @ApiOperation("修改信息")
    @PutMapping("/update")
    public Boolean update(@Validated(UpdateGroup.class) @RequestBody SdcResourceSatelliteDTO sdcResourceSatelliteDTO){
        return sdcResourceSatelliteService.update(sdcResourceSatelliteDTO);
    }

    @ApiOperation("批量删除信息")
    @DeleteMapping("/delete")
    public Boolean delete(@RequestBody Serializable[] ids){
        return sdcResourceSatelliteService.delete(Arrays.asList(ids));
    }

    @ApiOperation("根据id删除信息")
    @DeleteMapping("/delete/{id}")
    public Boolean delete(@PathVariable Serializable id){
        return sdcResourceSatelliteService.delete(id);
    }

    @ApiOperation("获取卫星下拉框列表")
    @GetMapping("/getSatellites")
    public List<SdcResourceSatelliteDO> getSatellites(){
        QueryWrapper<SdcResourceSatelliteDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("SAT_NAME").isNotNull("SAT_NAME");
        return sdcResourceSatelliteService.list(queryWrapper);
    }

    @ApiOperation("全球磁场分布")
    @GetMapping("/drawGlobalMagnetic")
    public String drawGlobalMagnetic(@RequestParam("time")String time,
                                     @RequestParam("height")Integer height){

        FileUtil.mkdir(pictureMagneticGlobal);
        String command = "python "+pythonMagneticGlobal+" '"+time+"' "+height+" "+pictureMagneticGlobal;
        log.info("执行Python命令：{}",command);
        String result = ExecUtil.execCmdWithResult(command);
        log.info("Python命令执行结果：{}",result);
        String picName = time.replace(":", "-").replace(" ","_").concat("_").concat(height.toString()).concat("km.png");
        return picName;

    }


    @ApiOperation("轨道磁场分布")
    @GetMapping("/drawOrbitalMagnetic")
    public List<MagneticOrbitVO> drawOrbitalMagnetic(@RequestParam("beginTime")String beginTime,
                                      @RequestParam("endTime")String endTime,
                                     @RequestParam("satId")String satId){


        String command = "python "+pythonOrbitalMagnetic+" "+satId+" '"+beginTime+"' "+" '"+endTime+"'";
        log.info("执行Python命令：{}",command);
        String result = ExecUtil.execCmdWithResult(command);
        log.info("Python命令执行结果：{}",result);
        String jsonStr = StrUtil.subBetween(result, "%%", "%%");
        JSONArray jsonArray = JSON.parseArray(jsonStr);
        List<MagneticOrbitVO> magneticOrbitVOS = new ArrayList<>();
        for (int i = 0;i<jsonArray.size();i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            MagneticOrbitVO magneticOrbitVO = new MagneticOrbitVO();
            magneticOrbitVO.setTime(jsonObject.get("时间").toString());
            magneticOrbitVO.setHei(Float.parseFloat(jsonObject.get("高度km").toString()));
            magneticOrbitVO.setLon(Float.parseFloat(jsonObject.get("经度deg").toString()));
            magneticOrbitVO.setLat(Float.parseFloat(jsonObject.get("纬度deg").toString()));
            magneticOrbitVO.setMagnetic(Float.parseFloat(jsonObject.get("磁场强度nT").toString()));
            magneticOrbitVOS.add(magneticOrbitVO);
        }


        return magneticOrbitVOS;

    }

}
