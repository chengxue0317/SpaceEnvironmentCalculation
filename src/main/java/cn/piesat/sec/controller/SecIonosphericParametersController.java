package cn.piesat.sec.controller;

import cn.piesat.sec.comm.oss.OSSInstance;
import cn.piesat.sec.comm.properties.SecFileServerProperties;
import cn.piesat.sec.model.vo.SecEnvElementVO;
import cn.piesat.sec.model.vo.SecIonosphericParametersVO;
import cn.piesat.sec.service.SecIonosphericParametersService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    @Autowired
    private SecFileServerProperties secFileServerProperties;

    @Value("${s3.bucketName}")
    private String bucketName;

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
                case "fof2": {
                    // todo 算法联调
                    break;
                }
                case "s4": {
                    String preview = OSSInstance.getOSSUtil().preview(bucketName, "sec/S4/stations/line.png");
                    SecIonosphericParametersVO v1 = new SecIonosphericParametersVO();
                    v1.setName("电离层闪烁现报区域分布图");
                    v1.setSrc(preview);
                    list.add(v1);
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

    @ApiOperation("下载电离层参数文件")
    @GetMapping("ionosphericspngs")
    public void downloadPics(@RequestParam(value = "type", required = true) String type, HttpServletResponse response) {
        String path = null;
        switch (type) {
            case "s4": {
                path = secFileServerProperties.getProfile().concat(secFileServerProperties.getS4Stations());
                break;
            }
            case "globleTEC": {
                path = secFileServerProperties.getProfile().concat(secFileServerProperties.getTecTimes());
                break;
            }
            case "globleROTI": {
                path = secFileServerProperties.getProfile().concat(secFileServerProperties.getRoti());
                break;
            }
            default: {
                path = secFileServerProperties.getProfile().concat(secFileServerProperties.getTecStations());
                break;
            }
        }
        OSSInstance.getOSSUtil().download(bucketName, path, response, true);
    }
}
