package cn.piesat.sec.controller;

import cn.piesat.kjyy.common.log.annotation.OpLog;
import cn.piesat.kjyy.common.log.enums.BusinessType;
import cn.piesat.sec.comm.oss.OSSInstance;
import cn.piesat.sec.comm.properties.SecFileServerProperties;
import cn.piesat.sec.model.vo.SecEnvOverviewVO;
import cn.piesat.sec.service.SecEnvOverviewService;
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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Locale;

/**
 * desc
 *
 * @author wuyazhou
 * @date 2022-11-10
 */
@Api(tags = "空间环境预报")
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/secenvoverview")
@RequiredArgsConstructor
public class SecEnvOverviewController {
    private static final Logger logger = LoggerFactory.getLogger(SecEnvOverviewController.class);

    private final SecEnvOverviewService secEnvOverviewService;

    private final SecReportService secReportService;

    @Autowired
    private SecFileServerProperties secFileServerProperties;

    @Value("${s3.bucketName}")
    private String bucketName;

    @ApiOperation("空间环境预报数据")
    @OpLog(op = BusinessType.OTHER, description = "空间环境预报数据")
    @PostMapping("/getEnvOverview")
    public List<SecEnvOverviewVO> getEnvOverview() {
        return secEnvOverviewService.getEnvOverview();
    }

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
    @PostMapping(value = "/downFile")
    public synchronized void downFile(
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "path", required = false) String path,
            HttpServletResponse response) {
        if (StringUtils.isEmpty(path)) {
            path = secReportService.makeReport(type);
        } else {
            path = secFileServerProperties.getProfile().concat(path);
        }
        // 判断文件是否存在
        boolean exist = OSSInstance.getOSSUtil().doesObjectExist(bucketName, path);
        if (exist) {
            OSSInstance.getOSSUtil().download(bucketName, path, response);
        } else {
            logger.error(String.format(Locale.ROOT, "=======File not exists !!!-- %s ", path));
        }
    }
}
