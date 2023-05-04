package cn.piesat.sec.controller;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.Inet4Address;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
import cn.piesat.sec.model.vo.RadioWaveEffectVO;
import cn.piesat.sec.model.vo.SdcResourceSatelliteVO;
import cn.piesat.sec.service.SdcResourceSatelliteService;
import cn.piesat.sec.utils.ExecUtil;
import cn.piesat.sec.utils.SpringUtil;
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
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
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
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

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

    @Value("${python.path.orbital_spectrum}")
    private String orbitalSpectrum;

    @Value("${python.path.magnetic_global_v2}")
    private String pythonMagneticGlobalV2;
    @Value("${picture.path.magnetic_global_v2}")
    private String pictureMagneticGlobalV2;
    @Value("${picture.url.magnetic_global_v2}")
    private String pictureUrlMagneticGlobalV2;

    @Value("${python.path.in_charging}")
    private String incharging;

    @Value("${python.path.satellite_radiation_env_orbit}")
    private String pythonSatelliteRadiationEnvOrbit;

    @Value("${python.path.satellite_radiation_env_orbit_plane}")
    private String pythonSatelliteRadiationEnvOrbitPlane;

    @Value("${python.path.s4_satellite}")
    private String s4Satellite;

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
        queryWrapper.select("SATELLITE_NAME").eq("SAT_TYPE",1).isNotNull("SATELLITE_NAME");
        return sdcResourceSatelliteService.list(queryWrapper);
    }

    @ApiOperation("全球磁场分布")
    @GetMapping("/drawGlobalMagnetic")
    public Map<String, String> drawGlobalMagnetic(@RequestParam("time")String time,
                                     @RequestParam("height")Integer height){

        FileUtil.mkdir(pictureMagneticGlobal);
//        String command = "python3 "+pythonMagneticGlobal+" \"'"+time+"'\" "+height+" "+pictureMagneticGlobal;
        String command = "python3 "+pythonMagneticGlobal+" \""+time+"\" "+height+" "+pictureMagneticGlobal;
        log.info("执行Python命令：{}",command);
//        String result = Connection2Sever.connectLinux(ip, portLinux, userName, password, command);
        String result = ExecUtil.execCmdWithResult(command);
        log.info("Python命令执行结果：{}",result);
        JSONObject jsonObject = JSON.parseObject(result.replace("\\s*", ""));
        Object fig = jsonObject.get("fig");
        Object bar = jsonObject.get("bar");
        String hostAddress = null;
        try {
            hostAddress = Inet4Address.getLocalHost().getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, String> map = new HashMap<>();

        if (SpringUtil.isProd()){
            String path = "/CMS-SDC/OP/TS/";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYYMMdd");
            String pathData = path.concat(LocalDate.now().format(formatter));

            if (fig != null){
                String previewPath = pathData.concat(File.separator).concat(fig.toString());
                String path2 = picturePathMagneticGlobal.concat(fig.toString());
                OSSInstance.getOSSUtil().upload(bucketName, previewPath, path2);
                map.put("fig",OSSInstance.getOSSUtil().preview(bucketName, previewPath));

            }

            if (bar != null){
                String previewPath = pathData.concat(File.separator).concat(bar.toString());
                String path2 = picturePathMagneticGlobal.concat(bar.toString());
                OSSInstance.getOSSUtil().upload(bucketName, previewPath, path2);
                map.put("bar",OSSInstance.getOSSUtil().preview(bucketName, previewPath));
            }

        }else if (SpringUtil.isDev()){

            if (fig != null){
                map.put("fig","http://".concat(hostAddress).concat(":").concat(port).concat("/sec").concat(pictureUrlMagneticGlobal).concat(fig.toString()));
            }

            if (bar != null){
                map.put("bar","http://".concat(hostAddress).concat(":").concat(port).concat("/sec").concat(pictureUrlMagneticGlobal).concat(bar.toString()));
            }
        }
        return map;

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
        JSONObject jsonObject = JSON.parseObject(jsonStr.replaceAll("\n", ""));
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
                                                @RequestParam("resolutionRatio")Integer resolutionRatio,
                                                @RequestParam("startEnergyLevel")Double startEnergyLevel,
                                                @RequestParam("endEnergyLevel")Double endEnergyLevel){

        String command = "python3 "+pythonGlobalRadiationEnv+" '"+time+"' "+height+" "+ionChannel+" "+resolutionRatio+" "+startEnergyLevel+" "+endEnergyLevel;
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

        String mainFigure = null;
        String colorbar = null;
        if (SpringUtil.isProd()){
            String path = "/CMS-SDC/OP/TS/";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYYMMdd");
            String pathData = path.concat(LocalDate.now().format(formatter));

            String mainFigurePreviewPath = pathData.concat("/main_figure").concat(substring).concat(".jpg");
            String mainFigurePath = picturePathGlobalRadiationEnv.concat(substring).concat("/main_figure.jpg");
            OSSInstance.getOSSUtil().upload(bucketName, mainFigurePreviewPath, mainFigurePath);
            mainFigure = OSSInstance.getOSSUtil().preview(bucketName, mainFigurePreviewPath);

            String colorbarPreviewPath = pathData.concat("/colorbar").concat(substring).concat(".jpg");
            String colorbarPath = picturePathGlobalRadiationEnv.concat(substring).concat("/colorbar.jpg");
            OSSInstance.getOSSUtil().upload(bucketName, colorbarPreviewPath, colorbarPath);
            colorbar = OSSInstance.getOSSUtil().preview(bucketName, colorbarPreviewPath);
        }else if (SpringUtil.isDev()){
            mainFigure = "http://".concat(hostAddress).concat(":").concat(port).concat("/sec").concat(pictureUrlGlobalRadiationEnv).concat(substring).concat("/main_figure.jpg");
            colorbar = "http://".concat(hostAddress).concat(":").concat(port).concat("/sec").concat(pictureUrlGlobalRadiationEnv).concat(substring).concat("/colorbar.jpg");
        }
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
        JSONObject jsonObject = JSON.parseObject(jsonStr.replaceAll("\n", ""));
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
        }
        if (StringUtils.isNotEmpty(st)){
            sb.append(" -st ").append("\"").append(st).append("\"");
        }
        if (StringUtils.isNotEmpty(et)){
            sb.append(" -et ").append("\"").append(et).append("\"");
        }
        if (StringUtils.isNotEmpty(a)){
            sb.append(" -a ").append(a);
        }
        if (StringUtils.isNotEmpty(f)){
            sb.append(" -f ").append(f);
        }
        if (StringUtils.isNotEmpty(m)){
            sb.append(" -m ").append(m);
        }
        if (StringUtils.isNotEmpty(c)){
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
    public String getSatelliteRadiationEnv(@RequestParam(value = "dt",required = false)String dt,
                                        @RequestParam(value = "sn",required = false)String sn,
                                        @RequestParam(value = "f",required = false)String f,
                                        @RequestParam(value = "m",required = false)String m,
                                        @RequestParam(value = "c",required = false)String c){

        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotEmpty(dt)){
            sb.append(" -dt ").append("\"").append(dt).append("\"");
        }
        if (StringUtils.isNotEmpty(sn)){
            sb.append(" -sn ").append(sn);
        }
        if (StringUtils.isNotEmpty(f)){
            sb.append(" -f ").append(f);
        }
        if (StringUtils.isNotEmpty(m)){
            sb.append(" -m ").append(m);
        }
        if (StringUtils.isNotEmpty(c)){
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

        if (SpringUtil.isProd()){
            String path = "/CMS-SDC/OP/TS/";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYYMMdd");
            String pathData = path.concat(LocalDate.now().format(formatter));

            String logPlotPreviewPath = pathData.concat("/satellite_radiation").concat(substring).concat(".png");
            String logPlotPath = picturePathSatelliteRadiationEnv.concat(substring).concat("/satellite_radiation.png");
            OSSInstance.getOSSUtil().upload(bucketName, logPlotPreviewPath, logPlotPath);
            return OSSInstance.getOSSUtil().preview(bucketName, logPlotPreviewPath);
        }else if (SpringUtil.isDev()){
            return  "http://".concat(hostAddress).concat(":").concat(port).concat("/sec").concat(pictureUrlSatelliteRadiationEnv).concat(substring).concat("/satellite_radiation.png");
        }
        return "请配置项目环境！";
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
        JSONObject jsonObject = JSON.parseObject(jsonStr.replaceAll("\n", ""));
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
        JSONObject jsonObject = JSON.parseObject(jsonStr.replaceAll("\n", ""));
        return jsonObject;

    }


    @ApiOperation("卫星轨道能谱")
    @GetMapping("/getOrbitalSpectrum")
    public JSONObject getOrbitalSpectrum(@RequestParam("beginTime")String beginTime,
                                         @RequestParam("endTime")String endTime,
                                         @RequestParam("particleType")String particleType,
                                         @RequestParam("satId")String satId,
                                         @RequestParam("startEnergyLevel")Double startEnergyLevel,
                                         @RequestParam("endEnergyLevel")Double endEnergyLevel){

        String command = "python3 "+orbitalSpectrum+" "+beginTime+" "+endTime+" "+satId+" "+particleType+" "+startEnergyLevel+" "+endEnergyLevel;
        log.info("执行Python命令：{}",command);
//        String result = Connection2Sever.connectLinux(ip, portLinux, userName, password, command);
        String result = ExecUtil.execCmdWithResult(command);
        log.info("Python命令执行结果：{}",result);
        String jsonStr = StrUtil.subBetween(result, "###", "###");
        JSONObject jsonObject = JSON.parseObject(jsonStr.replaceAll("\n", ""));
        return jsonObject;

    }

    @Autowired
    private StaticResourceDynamicRegistryController staticResourceDynamicRegistryController;

    @ApiOperation("全球磁场内源场和外源场分布")
    @GetMapping("/drawGlobalMagneticV2")
    public Map<String,List<String>> drawGlobalMagneticV2(@RequestParam("beginTime")String beginTime,
                                     @RequestParam("endTime")String endTime,
                                     @RequestParam("height")Integer height){

        FileUtil.mkdir(pictureMagneticGlobalV2);
        String command = "python3 "+pythonMagneticGlobalV2+" '"+beginTime+"' "+"'"+endTime+"' "+height+" "+pictureMagneticGlobalV2;
        log.info("执行Python命令：{}",command);
        String result = ExecUtil.execCmdWithResult(command);
        log.info("Python命令执行结果：{}",result);
        String hostAddress = null;
        try {
            hostAddress = Inet4Address.getLocalHost().getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String picPath = result.replaceAll("\\s*", "");
//        String picPath = "F:\\code-sw\\20120118000000-20121111000001-10km";
        File[] files = orderByName(picPath);
        staticResourceDynamicRegistryController.registry(pictureUrlMagneticGlobalV2,picPath.concat(File.separator));
        Map<String,List<String>> map = new HashMap<>();
        List<String> extPath = new ArrayList<>();
        List<String> intPath = new ArrayList<>();

        if (SpringUtil.isProd()){
            String path = "/CMS-SDC/OP/TS/";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYYMMdd");
            String pathData = path.concat(LocalDate.now().format(formatter));

            for (File file:files){
                String picName = file.getName();

            String previewPath = pathData.concat(File.separator).concat(picName);
            String path2 = picPath.concat(File.separator).concat(picName);
            OSSInstance.getOSSUtil().upload(bucketName, previewPath, path2);

                if (picName.contains("ext")){
                    extPath.add(OSSInstance.getOSSUtil().preview(bucketName, previewPath));
                }
                if (picName.contains("int")){
                    intPath.add(OSSInstance.getOSSUtil().preview(bucketName, previewPath));
                }

            }
        }else if (SpringUtil.isDev()){
            for (File file:files){
                String picName = file.getName();

                if (picName.contains("ext")){
                    extPath.add("http://".concat(hostAddress).concat(":").concat(port).concat("/sec").concat(pictureUrlMagneticGlobalV2).concat(picName));
                }
                if (picName.contains("int")){
                    intPath.add("http://".concat(hostAddress).concat(":").concat(port).concat("/sec").concat(pictureUrlMagneticGlobalV2).concat(picName));
                }

            }
        }

        map.put("extPath",extPath);
        map.put("intPath",intPath);
        return map;

    }


    public static void main(String[] args) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYYMMdd");
        String nowtime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        System.out.println(LocalDate.now().format(formatter));
        System.out.println(nowtime);
        LocalDateTime now = LocalDateTime.now();
        System.out.println(now);
    }

    public File[] orderByName(String filePath) {
        File file = new File(filePath);
        File[] files = file.listFiles();
        List fileList = Arrays.asList(files);
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (o1.isDirectory() && o2.isFile())
                    return -1;
                if (o1.isFile() && o2.isDirectory())
                    return 1;

                String regEx = "[^0-9]";
                Pattern p = Pattern.compile(regEx);
                Matcher m1 = p.matcher(o1.getName());
                String name1 = m1.replaceAll("").trim();

                Matcher m2 = p.matcher(o2.getName());
                String name2 = m2.replaceAll("").trim();

                Integer v1 = Integer.valueOf(name1);
                Integer v2 = Integer.valueOf(name2);

                return v1.compareTo(v2);
            }
        });
       return files;
    }


    @ApiOperation("卫星深层充电模块")
    @GetMapping("/getIncharging")
    public JSONObject getIncharging(@RequestParam("beginTime")String beginTime,
                                           @RequestParam("endTime")String endTime,
                                           @RequestParam("satId")String satId,
                                           @RequestParam("data")String data){

        String command = "python3 "+incharging+" "+" '"+beginTime+"' "+" '"+endTime+"'"+" "+satId+" "+data;
        log.info("执行Python命令：{}",command);
//        String result = Connection2Sever.connectLinux(ip, portLinux, userName, password, command);
        String result = ExecUtil.execCmdWithResult(command);
        log.info("Python命令执行结果：{}",result);
        String jsonStr = StrUtil.subBetween(result, "###", "###");
        JSONObject jsonObject = JSON.parseObject(jsonStr.replaceAll("\n", ""));
        return jsonObject;

    }


    @ApiOperation("卫星沿轨道辐射环境")
    @GetMapping("/getSatelliteRadiationEnvByOrbit")
    public JSONArray getSatelliteRadiationEnvByOrbit(@RequestParam(value = "name",required = false)String name,
                                                        @RequestParam(value = "start",required = false)String start,
                                                        @RequestParam(value = "end",required = false)String end,
                                                        @RequestParam(value = "whatf",required = false)String whatf,
                                                        @RequestParam(value = "whichm",required = false)String whichm){

        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotEmpty(name)){
            sb.append(" -n ").append("'").append(name).append("'");
        }
        if (StringUtils.isNotEmpty(start)){
            sb.append(" -s ").append("\"").append(start).append("\"");
        }
        if (StringUtils.isNotEmpty(end)){
            sb.append(" -e ").append("\"").append(end).append("\"");
        }
        if (StringUtils.isNotEmpty(whatf)){
            sb.append(" -f ").append(whatf);
        }
        if (StringUtils.isNotEmpty(whichm)){
            sb.append(" -m ").append(whichm);
        }
        String command = "python3 "+pythonSatelliteRadiationEnvOrbit+sb.toString();
        log.info("执行Python命令：{}",command);
//        String result = Connection2Sever.connectLinux(ip, portLinux, userName, password, command);
        String result = ExecUtil.execCmdWithResult(command);
        log.info("Python命令执行结果：{}",result);
        JSONArray jsonArray = JSON.parseArray(result.replaceAll("\n", ""));
        return jsonArray;
    }


    @ApiOperation("卫星轨道面辐射环境")
    @GetMapping("/getSatelliteRadiationEnvByOrbitPlane")
    public String getSatelliteRadiationEnvByOrbitPlane(@RequestParam(value = "name",required = false)String name,
                                                                    @RequestParam(value = "start",required = false)String start,
                                                                    @RequestParam(value = "whatf",required = false)String whatf,
                                                                    @RequestParam(value = "whichm",required = false)String whichm,
                                                                    @RequestParam(value = "channel",required = false)String channel){

        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotEmpty(name)){
            sb.append(" -n ").append("'").append(name).append("'");
        }
        if (StringUtils.isNotEmpty(start)){
            sb.append(" -s ").append("\"").append(start).append("\"");
        }
        if (StringUtils.isNotEmpty(whatf)){
            sb.append(" -f ").append(whatf);
        }
        if (StringUtils.isNotEmpty(whichm)){
            sb.append(" -m ").append(whichm);
        }
        if (StringUtils.isNotEmpty(channel)){
            sb.append(" -c ").append(channel);
        }
        String command = "python3 "+pythonSatelliteRadiationEnvOrbitPlane+sb.toString();
        log.info("执行Python命令：{}",command);
//        String result = Connection2Sever.connectLinux(ip, portLinux, userName, password, command);
        String result = ExecUtil.execCmdWithResult(command);
        log.info("Python命令执行结果：{}",result);
        String resultHandle = result.replaceAll("\\s*", "");
        if (StringUtils.isEmpty(resultHandle)){
            return "无数据！";
        }
        String hostAddress = null;
        try {
            hostAddress = Inet4Address.getLocalHost().getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String substring = resultHandle.substring(resultHandle.lastIndexOf(File.separator)+1);

        if (SpringUtil.isProd()){
            String path = "/CMS-SDC/OP/TS/";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYYMMdd");
            String pathData = path.concat(LocalDate.now().format(formatter));

            String logPlotPreviewPath = pathData.concat("/profile").concat(substring).concat(".png");
            String logPlotPath = picturePathSatelliteRadiationEnv.concat(substring).concat("/profile.png");
            OSSInstance.getOSSUtil().upload(bucketName, logPlotPreviewPath, logPlotPath);
            return OSSInstance.getOSSUtil().preview(bucketName, logPlotPreviewPath);
        }else if (SpringUtil.isDev()){
            return "http://".concat(hostAddress).concat(":").concat(port).concat("/sec").concat(pictureUrlSatelliteRadiationEnv).concat(substring).concat("/profile.png");
        }


        return "请配置项目环境！";

    }


    @ApiOperation("电波传播影响")
    @GetMapping("/getRadioWaveEffect")
    public JSONObject getRadioWaveEffect(@RequestParam("time")String time,
                                         @RequestParam("system")String system,
                                         @RequestParam("prn")String prn,
                                         @RequestParam("interval")String interval,
                                         @RequestParam("forecastPeriod")String forecastPeriod,
                                         @RequestParam("paramsSystem")String paramsSystem,
                                         @RequestParam("p1Channel")String p1Channel,
                                         @RequestParam("p2Channel")String p2Channel,
                                         @RequestParam("statTime")String statTime,
                                         @RequestParam("endTime")String endTime){

        String command = "python3 "+s4Satellite+" '/export/S4_satellite/data/' '"+endTime+"' '"+forecastPeriod+"' '"+interval+"' '"+p1Channel+"' '"+p2Channel+"' '"+prn+"' '"+statTime+"' '"+system+"' '"+time+"' '"+paramsSystem+"'";
        log.info("执行Python命令：{}",command);
//        String result = Connection2Sever.connectLinux(ip, portLinux, userName, password, command);
        String result = ExecUtil.execCmdWithResult(command);
        log.info("Python命令执行结果：{}",result);
        JSONObject jsonObject = JSON.parseObject(result.replaceAll("\n", ""));
        return jsonObject;

    }


}
