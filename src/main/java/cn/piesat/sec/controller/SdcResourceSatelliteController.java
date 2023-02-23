package cn.piesat.sec.controller;

import java.io.File;
import java.io.Serializable;
import java.net.Inet4Address;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.piesat.kjyy.common.mybatisplus.annotation.validator.group.AddGroup;
import cn.piesat.kjyy.common.mybatisplus.annotation.validator.group.UpdateGroup;
import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.comm.oss.OSSInstance;
import cn.piesat.sec.model.dto.SdcResourceSatelliteDTO;
import cn.piesat.sec.model.entity.SdcResourceSatelliteDO;
import cn.piesat.sec.model.query.SdcResourceSatelliteQuery;
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

    @Value("${python.path.single_event_effects}")
    private String pythonSingleEventEffects;
    @Value("${python.path.radiation_dose}")
    private String pythonRadiationDose;
    @Value("${server.port}")
    private String port;
    @Value("${picture.url.magnetic_global}")
    private String pictureUrlMagneticGlobal;

    @Value("${python.path.global_radiation_env}")
    private String pythonGlobalRadiationEnv;
    @Value("${picture.url.global_radiation_env}")
    private String pictureUrlGlobalRadiationEnv;

    @Value("${python.path.orbit_reduction}")
    private String pythonOrbitReduction;

    @Value("${python.path.config}")
    private String pythonConfig;

    @Value("${python.path.cross_anomaly}")
    private String pythonCrossAnomaly;

    @Value("${python.path.satellite_radiation_env}")
    private String pythonSatelliteRadiationEnv;
    @Value("${picture.url.satellite_radiation_env}")
    private String pictureUrlSatelliteRadiationEnv;

    @Value("${remote.ip}")
    private String ip;
    @Value("${remote.port}")
    private int portLinux;
    @Value("${remote.user_name}")
    private String userName;
    @Value("${remote.password}")
    private String password;

    @Value("${s3.bucketName}")
    private String bucketName;

    @Value("${picture.path.magnetic_global}")
    private String picturePathMagneticGlobal;
    @Value("${picture.path.satellite_radiation_env}")
    private String picturePathSatelliteRadiationEnv;
    @Value("${picture.path.global_radiation_env}")
    private String picturePathGlobalRadiationEnv;

    @Value("${python.path.surface_incharging}")
    private String surfaceIncharging;

    @Value("${python.path.satellite_time}")
    private String satelliteTime;

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
//        String command = "python3 "+pythonMagneticGlobal+" \"'"+time+"'\" "+height+" "+pictureMagneticGlobal;
        String command = "python3 "+pythonMagneticGlobal+" \""+time+"\" "+height+" "+pictureMagneticGlobal;
        log.info("执行Python命令：{}",command);
//        String result = Connection2Sever.connectLinux(ip, portLinux, userName, password, command);
        String result = ExecUtil.execCmdWithResult(command);
        log.info("Python命令执行结果：{}",result);
//        String picName = time.replace(":", "-").replace(" ","_").concat("_").concat(height.toString()).concat("km.png");
        String hostAddress = null;
        try {
            hostAddress = Inet4Address.getLocalHost().getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String picName = result.replaceAll("\\s*", "");

        String path = picturePathMagneticGlobal.concat(picName);
        OSSInstance.getOSSUtil().upload(bucketName, path, path);
        return OSSInstance.getOSSUtil().preview(bucketName, path);
//        return hostAddress.concat(":").concat(port).concat("/sec").concat(pictureUrlMagneticGlobal).concat(picName);

    }


    @ApiOperation("轨道磁场分布")
    @GetMapping("/drawOrbitalMagnetic")
    public JSONArray drawOrbitalMagnetic(@RequestParam("beginTime")String beginTime,
                                      @RequestParam("endTime")String endTime,
                                     @RequestParam("satId")String satId){


//        String command = "python3 "+pythonOrbitalMagnetic+" "+satId+" '\""+beginTime+"\"' "+" '\""+endTime+"\"'"+" "+pythonConfig;
        String command = "python3 "+pythonOrbitalMagnetic+" "+satId+" '"+beginTime+"' "+" '"+endTime+"'"+" "+pythonConfig;
        log.info("执行Python命令：{}",command);
//        String result = Connection2Sever.connectLinux(ip, portLinux, userName, password, command);
        String result = ExecUtil.execCmdWithResult(command);
        log.info("Python命令执行结果：{}",result);
        String jsonStr = StrUtil.subBetween(result, "%%", "%%");
        String trim = jsonStr.trim();
        JSONArray jsonArray = JSON.parseArray(trim);
//        List<MagneticOrbitVO> magneticOrbitVOS = new ArrayList<>();
//        for (int i = 0;i<jsonArray.size();i++){
//            JSONObject jsonObject = jsonArray.getJSONObject(i);
//            MagneticOrbitVO magneticOrbitVO = new MagneticOrbitVO();
//            magneticOrbitVO.setTime(jsonObject.get("time").toString());
//            magneticOrbitVO.setHei(Float.parseFloat(jsonObject.get("alt").toString()));
//            magneticOrbitVO.setLon(Float.parseFloat(jsonObject.get("lon").toString()));
//            magneticOrbitVO.setLat(Float.parseFloat(jsonObject.get("lat").toString()));
//            magneticOrbitVO.setMagnetic(Float.parseFloat(jsonObject.get("B").toString()));
//            magneticOrbitVOS.add(magneticOrbitVO);
//        }


//        return magneticOrbitVOS;
        return jsonArray;

    }


    @ApiOperation("单粒子效应评估数据")
    @GetMapping("/getSingleEventEffects")
    public JSONObject drawOrbitalMagnetic2(@RequestParam("beginTime")String beginTime,
                                                     @RequestParam("endTime")String endTime,
                                                     @RequestParam("satId")String satId,
                                                     @RequestParam("material")Integer material,
                                                     @RequestParam("mode")Integer mode){

        String command = "python3 "+pythonSingleEventEffects+" "+" '"+beginTime+"' "+" '"+endTime+"'"+" "+satId+" "+material+" "+mode;
        log.info("执行Python命令：{}",command);
//        String result = Connection2Sever.connectLinux(ip, portLinux, userName, password, command);
        String result = ExecUtil.execCmdWithResult(command);
        log.info("Python命令执行结果：{}",result);
        String jsonStr = StrUtil.subBetween(result, "###", "###");
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        return jsonObject;

    }


    @ApiOperation("卫星辐射总剂量计算评估")
    @GetMapping("/getRadiationDose")
    public JSONObject getRadiationDose(@RequestParam("beginTime")String beginTime,
                                       @RequestParam("endTime")String endTime,
                                       @RequestParam("satId")String satId,
                                       @RequestParam("material")Integer material,
                                       @RequestParam("mode")Integer mode){

        String command = "python3 "+pythonRadiationDose+" "+" '"+beginTime+"' "+" '"+endTime+"'"+" "+satId+" "+material+" "+mode;
        log.info("执行Python命令：{}",command);
//        String result = Connection2Sever.connectLinux(ip, portLinux, userName, password, command);
        String result = ExecUtil.execCmdWithResult(command);
        log.info("Python命令执行结果：{}",result);
        String jsonStr = StrUtil.subBetween(result, "###", "###");
        JSONObject jsonObject = JSON.parseObject(jsonStr.replaceAll("\\s*", ""));
        return jsonObject;

    }

//    @ApiOperation("辐射带高能粒子分布")
//    @GetMapping("/getRadiationEnergeticParticle")
//    public String getRadiationEnergeticParticle(@RequestParam("beginTime")String beginTime,
//                                   @RequestParam("endTime")String endTime,
//                                   @RequestParam("satId")String satId,
//                                   @RequestParam("material")Integer material,
//                                   @RequestParam("mode")Integer mode){
//
//        String command = "python3 "+pythonRadiationDose+" "+" '"+beginTime+"' "+" '"+endTime+"'"+" "+satId+" "+material+" "+mode;
//        log.info("执行Python命令：{}",command);
////        String result = Connection2Sever.connectLinux(ip, portLinux, userName, password, command);
//        String result = ExecUtil.execCmdWithResult(command);
//        log.info("Python命令执行结果：{}",result);
//        String jsonStr = StrUtil.subBetween(result, "###", "###");
//        JSONObject jsonObject = JSON.parseObject(jsonStr);
//        return jsonStr;
//
//    }


    @ApiOperation("全球辐射环境")
    @GetMapping("/getGlobalRadiationEnv")
    public Map<String, String> getGlobalRadiationEnv(@RequestParam("time")String time,
                                                @RequestParam("height")Integer height,
                                                @RequestParam("ionChannel")Integer ionChannel,
                                                @RequestParam("resolutionRatio")Integer resolutionRatio){

        String command = "python3 "+pythonGlobalRadiationEnv+" '"+time+"' "+height+" "+ionChannel+" "+resolutionRatio;
        log.info("执行Python命令：{}",command);
//        String result = Connection2Sever.connectLinux(ip, portLinux, userName, password, command);
        String result = ExecUtil.execCmdWithResult(command);
        log.info("Python命令执行结果：{}",result);
        String jsonStr = StrUtil.subBetween(result, "###", "###");

        String hostAddress = null;
        try {
            hostAddress = Inet4Address.getLocalHost().getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, String> map = new HashMap<>();
        String substring = jsonStr.substring(jsonStr.lastIndexOf(File.separator)+1);

        String mainFigurePath = picturePathGlobalRadiationEnv.concat(substring).concat("/main_figure.jpg");
        OSSInstance.getOSSUtil().upload(bucketName, mainFigurePath, mainFigurePath);
        String mainFigure = OSSInstance.getOSSUtil().preview(bucketName, mainFigurePath);

        String colorbarPath = picturePathGlobalRadiationEnv.concat(substring).concat("/colorbar.jpg");
        OSSInstance.getOSSUtil().upload(bucketName, colorbarPath, colorbarPath);
        String colorbar = OSSInstance.getOSSUtil().preview(bucketName, colorbarPath);

//        String mainFigure = hostAddress.concat(":").concat(port).concat("/sec").concat(pictureUrlGlobalRadiationEnv).concat(substring).concat("/main_figure.jpg");
//        String colorbar = hostAddress.concat(":").concat(port).concat("/sec").concat(pictureUrlGlobalRadiationEnv).concat(substring).concat("/colorbar.jpg");
        map.put("mainFigure",mainFigure);
        map.put("colorbar",colorbar);
        return map;

    }


    @ApiOperation("轨道衰变效应")
    @GetMapping("/getOrbitReduction")
    public JSONObject getOrbitReduction(@RequestParam("beginTime")String beginTime,
                                                     @RequestParam("endTime")String endTime,
                                                     @RequestParam("satId")String satId){

        String command = "python3 "+pythonOrbitReduction+" "+" '"+beginTime+"' "+" '"+endTime+"'"+" "+satId;
        log.info("执行Python命令：{}",command);
//        String result = Connection2Sever.connectLinux(ip, portLinux, userName, password, command);
        String result = ExecUtil.execCmdWithResult(command);
        log.info("Python命令执行结果：{}",result);
        String jsonStr = StrUtil.subBetween(result, "###", "###");
        JSONObject jsonObject = JSON.parseObject(jsonStr.replaceAll("\\s*", ""));
        return jsonObject;

    }

    @ApiOperation("穿越南大西洋异常区")
    @GetMapping("/getCrossAnomaly")
    public JSONObject getCrossAnomaly(@RequestParam(value = "s",required = false)String s,
                                      @RequestParam(value = "st",required = false)String st,
                                      @RequestParam(value = "et",required = false)String et,
                                      @RequestParam(value = "a",required = false)String a,
                                      @RequestParam(value = "f",required = false)String f,
                                      @RequestParam(value = "m",required = false)String m,
                                      @RequestParam(value = "c",required = false)String c){

        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotEmpty(s)){
            sb.append(" -s ").append("'").append(s).append("'");
        }else if (StringUtils.isNotEmpty(st)){
            sb.append(" -st ").append(st);
        }else if (StringUtils.isNotEmpty(et)){
            sb.append(" -et ").append(et);
        }else if (StringUtils.isNotEmpty(a)){
            sb.append(" -a ").append(a);
        }else if (StringUtils.isNotEmpty(f)){
            sb.append(" -f ").append(f);
        }else if (StringUtils.isNotEmpty(m)){
            sb.append(" -m ").append(m);
        }else if (StringUtils.isNotEmpty(c)){
            sb.append(" -c ").append(c);
        }
        String command = "python3 "+pythonCrossAnomaly+sb.toString();
        log.info("执行Python命令：{}",command);
//        String result = Connection2Sever.connectLinux(ip, portLinux, userName, password, command);
        String result = ExecUtil.execCmdWithResult(command);
        log.info("Python命令执行结果：{}",result);
        String resultHandle = result.replaceAll("\n", "");
        if ("Null".equals(resultHandle)){
            return null;
        }
        JSONObject jsonObject = JSON.parseObject(resultHandle);
        return jsonObject;

    }

    @ApiOperation("卫星辐射环境")
    @GetMapping("/getSatelliteRadiationEnv")
    public Map<String, String> getSatelliteRadiationEnv(@RequestParam(value = "s",required = false)String s,
                                        @RequestParam(value = "st",required = false)String st,
                                        @RequestParam(value = "et",required = false)String et,
                                        @RequestParam(value = "a",required = false)String a,
                                        @RequestParam(value = "f",required = false)String f,
                                        @RequestParam(value = "m",required = false)String m,
                                        @RequestParam(value = "c",required = false)String c){

        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotEmpty(s)){
            sb.append(" -s ").append("'").append(s).append("'");
        }else if (StringUtils.isNotEmpty(st)){
            sb.append(" -st ").append(st);
        }else if (StringUtils.isNotEmpty(et)){
            sb.append(" -et ").append(et);
        }else if (StringUtils.isNotEmpty(a)){
            sb.append(" -a ").append(a);
        }else if (StringUtils.isNotEmpty(f)){
            sb.append(" -f ").append(f);
        }else if (StringUtils.isNotEmpty(m)){
            sb.append(" -m ").append(m);
        }else if (StringUtils.isNotEmpty(c)){
            sb.append(" -c ").append(c);
        }
        String command = "python3 "+pythonSatelliteRadiationEnv+sb.toString();
        log.info("执行Python命令：{}",command);
//        String result = Connection2Sever.connectLinux(ip, portLinux, userName, password, command);
        String result = ExecUtil.execCmdWithResult(command);
        log.info("Python命令执行结果：{}",result);
        String resultHandle = result.replaceAll("\\s*", "");
        String hostAddress = null;
        try {
            hostAddress = Inet4Address.getLocalHost().getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, String> map = new HashMap<>();
        String substring = resultHandle.substring(resultHandle.lastIndexOf(File.separator)+1);

        String logPlotPath = picturePathSatelliteRadiationEnv.concat(substring).concat("/log_plot.png");
        OSSInstance.getOSSUtil().upload(bucketName, logPlotPath, logPlotPath);
        String logPlot = OSSInstance.getOSSUtil().preview(bucketName, logPlotPath);

        String colorbarPath = picturePathSatelliteRadiationEnv.concat(substring).concat("/color_bar.png");
        OSSInstance.getOSSUtil().upload(bucketName, colorbarPath, colorbarPath);
        String colorbar = OSSInstance.getOSSUtil().preview(bucketName, colorbarPath);

//        String logPlot = hostAddress.concat(":").concat(port).concat("/sec").concat(pictureUrlSatelliteRadiationEnv).concat(substring).concat("/log_plot.png");
//        String colorbar = hostAddress.concat(":").concat(port).concat("/sec").concat(pictureUrlSatelliteRadiationEnv).concat(substring).concat("/color_bar.png");
        map.put("logPlot",logPlot);
        map.put("colorbar",colorbar);
        return map;

    }


    @ApiOperation("卫星表面充电模块")
    @GetMapping("/getSurfaceIncharging")
    public JSONObject getSurfaceIncharging(@RequestParam("beginTime")String beginTime,
                                           @RequestParam("endTime")String endTime,
                                           @RequestParam("satId")String satId,
                                           @RequestParam("material")Integer material){

        String command = "python3 "+surfaceIncharging+" "+" '"+beginTime+"' "+" '"+endTime+"'"+" "+satId+" "+material;
        log.info("执行Python命令：{}",command);
//        String result = Connection2Sever.connectLinux(ip, portLinux, userName, password, command);
        String result = ExecUtil.execCmdWithResult(command);
        log.info("Python命令执行结果：{}",result);
        String jsonStr = StrUtil.subBetween(result, "###", "###");
        JSONObject jsonObject = JSON.parseObject(jsonStr.replaceAll("\\s*", ""));
        return jsonObject;

    }


    @ApiOperation("确定卫星时间范围")
    @GetMapping("/getSatelliteTime")
    public JSONObject getSatelliteTime(@RequestParam("satId")String satId){

        String command = "python3 "+satelliteTime+" "+satId;
        log.info("执行Python命令：{}",command);
//        String result = Connection2Sever.connectLinux(ip, portLinux, userName, password, command);
        String result = ExecUtil.execCmdWithResult(command);
        log.info("Python命令执行结果：{}",result);
        String jsonStr = StrUtil.subBetween(result, "###", "###");
        JSONObject jsonObject = JSON.parseObject(jsonStr.replaceAll("\\s*", ""));
        return jsonObject;

    }
}
