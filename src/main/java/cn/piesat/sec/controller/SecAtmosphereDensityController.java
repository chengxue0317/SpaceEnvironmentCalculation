package cn.piesat.sec.controller;

import java.io.File;
import java.io.Serializable;
import java.net.Inet4Address;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.piesat.kjyy.common.mybatisplus.annotation.validator.group.AddGroup;
import cn.piesat.kjyy.common.mybatisplus.annotation.validator.group.UpdateGroup;
import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.comm.oss.OSSInstance;
import cn.piesat.sec.model.dto.SecAtmosphereDensityDTO;
import cn.piesat.sec.model.entity.SecAtmosphereDensityDO;
import cn.piesat.sec.model.query.SecAtmosphereDensityQuery;
import cn.piesat.sec.service.SecAtmosphereDensityService;
import cn.piesat.sec.model.vo.SecAtmosphereDensityVO;
import cn.piesat.sec.utils.Connection2Sever;
import cn.piesat.sec.utils.ExecUtil;
import cn.piesat.sec.utils.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 大气密度预报模块
 *
 * @author sw
 * @email shiwang@piesat.cn
 * @date 2022-11-16 14:58:35
 */
@Api(tags = "大气密度预报模块")
@RestController
@RequestMapping("/secatmospheredensity")
@RequiredArgsConstructor
public class SecAtmosphereDensityController {

    private static final Logger log = LoggerFactory.getLogger(SdcResourceSatelliteController.class);

    @Value("${python.path.atmosphere_density}")
    private String pythonAtmosphereDensity;

    @Value("${python.path.atmosphere_density_global}")
    private String pythonAtmosphereDensityGlobal;

    @Value("${remote.ip}")
    private String ip;
    @Value("${remote.port}")
    private int portLinux;
    @Value("${remote.user_name}")
    private String userName;
    @Value("${remote.password}")
    private String password;

    @Value("${server.port}")
    private String port;

    @Value("${picture.url.atmosphere_density_global}")
    private String pictureUrlAtmosphereDensityGlobal;

    @Value("${s3.bucketName}")
    private String bucketName;

    private final SecAtmosphereDensityService secAtmosphereDensityService;

    @ApiOperation("分页查询")
    @PostMapping("/list")
    public PageResult list(PageBean pageBean, @RequestBody(required = false) SecAtmosphereDensityQuery secAtmosphereDensityQuery){
        return secAtmosphereDensityService.list(pageBean,secAtmosphereDensityQuery);
    }

    @ApiOperation("根据id查询")
    @GetMapping("/info/{id}")
    public SecAtmosphereDensityVO info(@PathVariable("id") Serializable id){
        return secAtmosphereDensityService.info(id);
    }

    @ApiOperation("保存信息")
    @PostMapping("/save")
    public Boolean save(@Validated(AddGroup.class) @RequestBody SecAtmosphereDensityDTO secAtmosphereDensityDTO){
        return secAtmosphereDensityService.save(secAtmosphereDensityDTO);
    }

    @ApiOperation("修改信息")
    @PutMapping("/update")
    public Boolean update(@Validated(UpdateGroup.class) @RequestBody SecAtmosphereDensityDTO secAtmosphereDensityDTO){
        return secAtmosphereDensityService.update(secAtmosphereDensityDTO);
    }

    @ApiOperation("批量删除信息")
    @DeleteMapping("/delete")
    public Boolean delete(@RequestBody Serializable[] ids){
        return secAtmosphereDensityService.delete(Arrays.asList(ids));
    }

    @ApiOperation("根据id删除信息")
    @DeleteMapping("/delete/{id}")
    public Boolean delete(@PathVariable Serializable id){
        return secAtmosphereDensityService.delete(id);
    }

    @ApiOperation("大气密度曲线图")
    @PostMapping("/getData")
    public Map getData(@RequestBody(required = false) SecAtmosphereDensityQuery secAtmosphereDensityQuery){
        QueryWrapper<SecAtmosphereDensityDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("TIME","DENSITY").eq("SAT_ID",secAtmosphereDensityQuery.getSatId()).between("TIME",secAtmosphereDensityQuery.getTimeBetween().getLeft(),secAtmosphereDensityQuery.getTimeBetween().getRight());
        List<SecAtmosphereDensityDO> list = secAtmosphereDensityService.list(queryWrapper);
        List<LocalDateTime> times = new ArrayList<>();
        List<Double> densitys = new ArrayList<>();
        list.forEach(e->{
            densitys.add(e.getDensity());
            times.add(e.getTime());
//            times.add(e.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        });
        Map map = new HashMap();
        map.put("times",times);
        map.put("densitys",densitys);
        return map;
    }


    @ApiOperation("大气密度曲线图")
    @PostMapping("/getDataByArithmetic")
    public JSONObject getDataByArithmetic(@RequestParam("beginTime")String beginTime,
                        @RequestParam("endTime")String endTime,
                        @RequestParam("satId")String satId,
                        @RequestParam("sign")Integer sign) throws Exception {
        String command = "python3 "+pythonAtmosphereDensity+" "+" '"+beginTime+"' "+" '"+endTime+"'"+" "+satId+" "+sign;
        log.info("执行Python命令：{}",command);
        String result = ExecUtil.execCmdWithResult(command);
//        String result = Connection2Sever.connectLinux(ip, portLinux, userName, password, command);
        log.info("Python命令执行结果：{}",result);
        String jsonStr = StrUtil.subBetween(result, "###", "###");
        JSONObject jsonObject = JSON.parseObject(jsonStr.replaceAll("\n", ""));
        Object obj = jsonObject.get("colorbar");
        if (obj != null){
            String colorbar = obj.toString();
            String hostAddress = null;
            try {
                hostAddress = Inet4Address.getLocalHost().getHostAddress();
            } catch (Exception e) {
                e.printStackTrace();
            }
            String substring = colorbar.substring(colorbar.lastIndexOf(File.separator)+1);

            if (SpringUtil.isProd()){
                String path = "/CMS-SDC/OP/TS/";
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYYMMdd");
                String pathData = path.concat(LocalDate.now().format(formatter));

                String colorbarPreviewPath = pathData.concat("/colorbar").concat(substring).concat(".jpg");
                String colorbarPath = colorbar.concat("/colorbar.jpg");
                OSSInstance.getOSSUtil().upload(bucketName, colorbarPreviewPath, colorbarPath);
                jsonObject.put("colorbar",OSSInstance.getOSSUtil().preview(bucketName, colorbarPreviewPath));

            }else if (SpringUtil.isDev()){
                jsonObject.put("colorbar","http://".concat(hostAddress).concat(":").concat(port).concat("/sec").concat(pictureUrlAtmosphereDensityGlobal).concat(substring).concat("/colorbar.jpg"));
            }

        }

        return jsonObject;
    }

    @ApiOperation("全球大气密度曲线图")
    @PostMapping("/getDataByArithmeticGlobal")
    public Map<String, String> getDataByArithmeticGlobal(@RequestParam("time")String time,
                                                @RequestParam("height")Integer height){
        String command = "python3 "+pythonAtmosphereDensityGlobal+" '"+time+"' "+height;
        log.info("执行Python命令：{}",command);
        String result = ExecUtil.execCmdWithResult(command);
//        String result = Connection2Sever.connectLinux(ip, portLinux, userName, password, command);
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
            String mainFigurePath = jsonStr.concat("/main_figure.jpg");
            OSSInstance.getOSSUtil().upload(bucketName, mainFigurePreviewPath, mainFigurePath);
            mainFigure = OSSInstance.getOSSUtil().preview(bucketName, mainFigurePreviewPath);

            String colorbarPreviewPath = pathData.concat("/colorbar").concat(substring).concat(".jpg");
            String colorbarPath = jsonStr.concat("/colorbar.jpg");
            OSSInstance.getOSSUtil().upload(bucketName, colorbarPreviewPath, colorbarPath);
            colorbar = OSSInstance.getOSSUtil().preview(bucketName, colorbarPreviewPath);
        }else if (SpringUtil.isDev()){
            //图片映射方式有两种：1.动态映射 2.半映射半拼串
            mainFigure = "http://".concat(hostAddress).concat(":").concat(port).concat("/sec").concat(pictureUrlAtmosphereDensityGlobal).concat(substring).concat("/main_figure.jpg");
            colorbar = "http://".concat(hostAddress).concat(":").concat(port).concat("/sec").concat(pictureUrlAtmosphereDensityGlobal).concat(substring).concat("/colorbar.jpg");
        }

        map.put("mainFigure",mainFigure);
        map.put("colorbar",colorbar);
        return map;
    }


    @ApiOperation("根据id查询")
    @GetMapping("/test")
    public String info(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        String uri = request.getRequestURI();//返回请求行中的资源名称
        String url = request.getRequestURL().toString();//获得客户端发送请求的完整url
        String ip = request.getRemoteAddr();//返回发出请求的IP地址
        String params = request.getQueryString();//返回请求行中的参数部分
        String host=request.getRemoteHost();//返回发出请求的客户机的主机名
        int port =request.getRemotePort();//返回发出请求的客户机的端口号。

        String hostAddress = null;
        try {
            hostAddress = Inet4Address.getLocalHost().getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hostAddress;
    }

}
