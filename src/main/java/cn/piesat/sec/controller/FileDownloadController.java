package cn.piesat.sec.controller;

import cn.piesat.kjyy.common.log.annotation.OpLog;
import cn.piesat.kjyy.common.log.enums.BusinessType;
import cn.piesat.sec.comm.oss.OSSInstance;
import cn.piesat.sec.comm.properties.SecFileServerProperties;
import cn.piesat.sec.comm.util.FileUtil;
import cn.piesat.sec.service.SecReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 文件下载
 *
 * @author wuyazhou
 * @date 2022-11-10
 */
@Api(tags = "文件下载")
@CrossOrigin(origins = "*")
@Controller
@RequestMapping("/fileDownload")
@RequiredArgsConstructor
public class FileDownloadController {
    private static final Logger logger = LoggerFactory.getLogger(SecEnvOverviewController.class);

    private final SecReportService secReportService;

    @Autowired
    private SecFileServerProperties secFileServerProperties;

    @Resource
    private HttpServletResponse response;

    @Value("${s3.bucketName}")
    private String bucketName;

    /**
     * @param path 文件路径
     * @param type 文件类型
     * @return 文件下载的url
     */
    @ApiOperation("环境预报报文下载")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "type", value = "文件类型day/week/month", dataType = "String", required = false),
            @ApiImplicitParam(name = "path", value = "文件路径", dataType = "String", required = false)
    })
    @OpLog(op = BusinessType.OTHER, description = "环境预报报文下载")
    @PostMapping(value = "/downReportFiles")
    public synchronized void downFile(
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "path", required = false) String path) {
        if (StringUtils.isEmpty(path)) {
            path = secReportService.makeReport(type);
        }
        // 判断文件是否存在
        boolean exist = OSSInstance.getOSSUtil().doesObjectExist(bucketName, path);
        if (exist) {
            OSSInstance.getOSSUtil().download(bucketName, path, response);
        } else {
            logger.error(String.format(Locale.ROOT, "=======File not exists !!!-- %s ", path));
        }
    }

    @ApiOperation("下载电离层参数文件")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "type", value = "类型globleTEC/chineseROTI/chineseTec/s4", dataType = "String", required = true),
            @ApiImplicitParam(name = "altitude", value = "高度", dataType = "String", required = false),
            @ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "String", required = true),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "String", required = true)
    })
    @OpLog(op = BusinessType.OTHER, description = "下载电离层参数文件")
    @PostMapping("/ionosphericspngs")
    public void downloadPics(@RequestParam(value = "type") String type,
                             @RequestParam(value = "altitude", required = false) String altitude,
                             @RequestParam("startTime") String startTime,
                             @RequestParam("endTime") String endTime) {
        String path;
        List<String> pathList = new ArrayList<>();
        switch (type) {
            case "s4": {
                break;
            }
            case "globleTEC": {
                List<String> fileNames = FileUtil.picsNames(altitude, startTime, endTime);
                path = secFileServerProperties.getProfile().concat(secFileServerProperties.getTecGlobal());
                for (String name : fileNames) {
                    pathList.add(path.concat(name));
                }
                break;
            }
            case "chineseROTI": {
                List<String> fileNames = FileUtil.picturesNamesMinutes(startTime, endTime);
                path = secFileServerProperties.getProfile().concat(secFileServerProperties.getRotiPics());
                for (String name : fileNames) {
                    pathList.add(path.concat(name));
                }
                break;
            }
            default: {
                List<String> fileNames = FileUtil.picsNames(altitude, startTime, endTime);
                path = secFileServerProperties.getProfile().concat(secFileServerProperties.getTecChina());
                for (String name : fileNames) {
                    pathList.add(path.concat(name));
                }
                break;
            }
        }
        System.out.println("下载========" + StringUtils.join(pathList, "=v="));
        OSSInstance.getOSSUtil().download(bucketName, pathList, response);
    }
}
