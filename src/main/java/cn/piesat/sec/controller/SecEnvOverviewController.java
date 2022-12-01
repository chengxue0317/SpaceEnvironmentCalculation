package cn.piesat.sec.controller;

import cn.piesat.sec.comm.conf.SecFileServerConfig;
import cn.piesat.sec.comm.constant.Constant;
import cn.piesat.sec.model.vo.SecEnvOverviewVO;
import cn.piesat.sec.service.SecEnvOverviewService;
import cn.piesat.sec.service.SecReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

/**
 * desc
 *
 * @author wuyazhou
 * @date 2022-11-10
 */
@Api(tags = "警报事件")
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/secenvoverview")
@RequiredArgsConstructor
public class SecEnvOverviewController {
    private static final Logger logger = LoggerFactory.getLogger(SecEnvOverviewController.class);

    private final SecEnvOverviewService secEnvOverviewService;

    private final SecReportService secReportService;

    @ApiOperation("查询一段时间内的F10.7数据")
    @PostMapping("/getEnvOverview")
    public List<SecEnvOverviewVO> getEnvOverview() {
        return secEnvOverviewService.getEnvOverview();
    }

    /**
     * @param path 文件路径
     * @param type 文件类型
     * @return 文件下载的url
     */
    @PostMapping(value = "/downFile")
    public synchronized void downFile(
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "path", required = false) String path,
            HttpServletResponse response){
        if(StringUtils.isEmpty(path)) {
            path = secReportService.makeReport(type);
        } else{
            path = SecFileServerConfig.getProfile().concat(path);
        }
        // 判断文件是否存在
        File file = FileUtils.getFile(path);
        Boolean exist = file.exists();
        if (exist) {
            //1.设置文件ContentType类型，这样设置，会自动判断下载文件类型
            response.setContentType("multipart/form-data");
            response.setHeader("Content-Disposition", "attachment;fileName=" + file.getName());
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setCharacterEncoding(Constant.UTF8);

            try (InputStream inputStream = FileUtils.openInputStream(file);
                 ) {
                ServletOutputStream out = response.getOutputStream();
                byte[] buffer = new byte[Constant.BUFFSIZE];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    out.write(buffer, 0, length);
                }
                out.flush();
            } catch (Exception e) {
                logger.error(String.format(Locale.ROOT, "--download--%s: %s", path, e.getMessage()));
            }
        }
    }

}
