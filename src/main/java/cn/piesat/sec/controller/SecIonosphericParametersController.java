package cn.piesat.sec.controller;

import cn.piesat.sec.comm.properties.SecFileServerProperties;
import cn.piesat.sec.comm.constant.Constant;
import cn.piesat.sec.model.vo.SecEnvElementVO;
import cn.piesat.sec.model.vo.SecIonosphericParametersVO;
import cn.piesat.sec.service.SecIonosphericParametersService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 电离层参数
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 16:35:45
 */
@Api(tags = "电离层参数")
@RestController
@RequestMapping("/ionosphericparameters")
@RequiredArgsConstructor
public class SecIonosphericParametersController {
    private static final Logger logger = LoggerFactory.getLogger(SecIonosphericParametersController.class);
    private final SecIonosphericParametersService secIPS;

    /**
     * 获取电离层闪烁数据
     *
     * @param staId 站点ID
     * @return 电离层参数站点数据
     */
    @ApiOperation("获取电离层闪烁数据")
    @GetMapping("blinkData")
    public SecEnvElementVO getBlinkData(@RequestParam("staId") String staId,
                                        @RequestParam("startTime") String startTime,
                                        @RequestParam("endTime") String endTime) {

        return secIPS.getBlinkData(staId, startTime, endTime);
    }

    /**
     * 获取电离层参数多站最新数据
     *
     * @param type 电离层参数类型
     * @return 电离层参数站点数据
     */
    @ApiOperation("获取电离层参数站点数据")
    @GetMapping("ionosphericparametersStationData")
    public List<SecIonosphericParametersVO> getIonosphericparametersStationData(@RequestParam("type") String type) {
        List<SecIonosphericParametersVO> list = new ArrayList<SecIonosphericParametersVO>();
        if (null == type) {
            return list;
        } else {
            switch (type) {
                case "s4": {
                    SecIonosphericParametersVO v1 = new SecIonosphericParametersVO();
                    v1.setName("长江1号");
                    v1.setSrc("http://127.0.0.1:9999/" + SecFileServerProperties.getS4Stations() + "line.png");
                    list.add(v1);
                    break;
                }
                case "fof2": {
                    // todo 算法联调
                    break;
                }
                default: {
                    list = secIPS.getIonosphericStationsTECPngs();
                    break;
                }
            }
        }
        return list;
    }

    /**
     * 获取电离层参数数据
     *
     * @param type      电离层参数类型
     * @param staId     台站id
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    @ApiOperation("获取电离层参数数据")
    @GetMapping("ionosphericparametersData")
    public List<SecIonosphericParametersVO> getIonosphericparametersData(@RequestParam(value = "type", required = true) String type,
                                                                         @RequestParam(value = "staId", required = false) String staId,
                                                                         @RequestParam("startTime") String startTime,
                                                                         @RequestParam("endTime") String endTime) {
        List list = new ArrayList();
        if (type.toLowerCase(Locale.ROOT).equals("roti")) {
            list = secIPS.getIonosphericRotiPngs(startTime, endTime, staId);
        } else {
            list = secIPS.getIonosphericTecPngs(startTime, endTime);
        }
        return list;
    }

    @GetMapping("ionosphericspngs")
    public void downloadPics(@RequestParam(value = "type", required = true) String type, HttpServletResponse response) {
        String path = null;
        switch (type) {
            case "s4": {
                path = SecFileServerProperties.getProfile().concat(SecFileServerProperties.getS4Stations());
                break;
            }
            case "globleTEC": {
                path = SecFileServerProperties.getProfile().concat(SecFileServerProperties.getTecTimes());
                break;
            }
            case "globleROTI": {
                path = SecFileServerProperties.getProfile().concat(SecFileServerProperties.getRoti());
                break;
            }
            default: {
                path = SecFileServerProperties.getProfile().concat(SecFileServerProperties.getTecStations());
                break;
            }
        }
        ZipOutputStream zout = null;
        try {
            File rootFile = FileUtils.getFile(path);
            List<File> files = FileUtils.listFiles(rootFile, null, true).stream().collect(Collectors.toList());
            // 循环下载
            response.setCharacterEncoding(Constant.UTF8);
            response.setContentType("multipart/form-data;application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(String.valueOf(System.currentTimeMillis()), "UTF-8"));
            zout = new ZipOutputStream(response.getOutputStream());
            byte[] buff = new byte[Constant.BUFFSIZE];
            int len;
            InputStream inputStream = null;
            for (int i = 0; i < files.size(); i++) {
                // 判断文件是否存在
                File file = files.get(i);
                if (!file.exists()) {
                    logger.error(file.getAbsolutePath() + ":====FILE NOT EXISTS！");
                    continue;
                }
                inputStream = FileUtils.openInputStream(file);
                if (null == inputStream) {
                    continue;
                }
                zout.putNextEntry(new ZipEntry(file.getName()));
                while ((len = inputStream.read(buff)) != -1) {
                    zout.write(buff, 0, len);
                }
                zout.flush();
                zout.closeEntry();
                inputStream.close();
            }
            zout.flush();
            zout.finish();

        } catch (Exception e) {
            logger.error(String.format(Locale.ROOT, "File download exception %s", e.getMessage()));
        } finally {
            if (null != zout) {
                try {
                    zout.close();
                } catch (IOException e) {
                    logger.error("--------Failed to close ZipoutputStream. %s", e.getMessage());
                }
            }
            return;
        }
    }
}
