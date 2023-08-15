package cn.piesat.sec.controller;

import cn.piesat.kjyy.common.log.annotation.OpLog;
import cn.piesat.kjyy.common.log.enums.BusinessType;
import cn.piesat.sec.comm.oss.OSSInstance;
import cn.piesat.sec.comm.util.FileUtil;
import cn.piesat.sec.model.vo.SecEnvElementVO;
import cn.piesat.sec.model.vo.SecIonosphericParametersVO;
import cn.piesat.sec.service.SecIonosphericParametersService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @Value("${s3.bucketName}")
    private String bucketName;

    @Value("${piesat.profile}")
    private String profile;

    /**
     * 获取电离层闪烁数据
     *
     * @param satcode      站点ID
     * @param satno        站点ID
     * @param satfrequency 站点ID
     * @return 电离层参数站点数据
     */
    @ApiOperation("获取电离层闪烁数据")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "satcode", value = "卫星分类", dataType = "String", required = true),
            @ApiImplicitParam(name = "satno", value = "卫星编号", dataType = "String", required = true),
            @ApiImplicitParam(name = "satfrequency", value = "通信频段", dataType = "String", required = true),
            @ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "String", required = true),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "String", required = true)
    })
    @OpLog(op = BusinessType.OTHER, description = "电离层闪烁数据")
    @GetMapping("blinkData")
    public SecEnvElementVO getBlinkData(@RequestParam("satcode") String satcode,
                                        @RequestParam("satno") String satno,
                                        @RequestParam("satfrequency") String satfrequency,
                                        @RequestParam("startTime") String startTime,
                                        @RequestParam("endTime") String endTime) {

        return secIPS.getBlinkData(satcode, satno, satfrequency, startTime, endTime);
    }

    /**
     * 获取电离层参数多站最新数据
     *
     * @param type 电离层参数类型
     * @return 电离层参数站点数据
     */
    @ApiOperation("空间环境态势-全国TEC数据")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "altitude", value = "高度", dataType = "String", required = true),
            @ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "String", required = true),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "String", required = true),
            @ApiImplicitParam(name = "type", value = "类型tec/s4/fof2", dataType = "String", required = true)
    })
    @OpLog(op = BusinessType.OTHER, description = "空间环境态势-全国TEC数据")
    @GetMapping("ionosphericparametersStationData")
    public List<SecIonosphericParametersVO> getIonosphericparametersStationData(
            @RequestParam("altitude") String altitude,
            @RequestParam("startTime") String startTime,
            @RequestParam("endTime") String endTime,
            @RequestParam("type") String type) {
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
                    String preview = OSSInstance.getOSSUtil().preview(bucketName, FileUtil.rmPathPreSplit(profile.concat("S4/stations/line.png")));
                    SecIonosphericParametersVO v1 = new SecIonosphericParametersVO();
                    v1.setName("电离层闪烁现报区域分布图");
                    v1.setSrc(preview);
                    list.add(v1);
                    break;
                }
                default: {
                    list = secIPS.getIonosphericChineseTECPngs(altitude, startTime, endTime);
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
     * @param altitude  高度
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    @ApiOperation("电离层参数-获取全国/全球电离层数据")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "type", value = "类型tec/s4/roti", dataType = "String", required = true),
            @ApiImplicitParam(name = "altitude", value = "高度", dataType = "String", required = true),
            @ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "String", required = true),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "String", required = true)
    })
    @OpLog(op = BusinessType.OTHER, description = "电离层参数-获取全国/全球电离层数据")
    @GetMapping("ionosphericparametersData")
    public List<SecIonosphericParametersVO> getIonosphericparametersData(@RequestParam(value = "type", required = true) String type,
                                                                         @RequestParam(value = "altitude", required = false) String altitude,
                                                                         @RequestParam("startTime") String startTime,
                                                                         @RequestParam("endTime") String endTime) {
        List<SecIonosphericParametersVO> list = new ArrayList();
        if (type.toLowerCase(Locale.ROOT).equals("roti")) {
            list = secIPS.getIonosphericRotiPngs(startTime, endTime);
        } else {
            list = secIPS.getIonosphericGlobalTecPngs(altitude, startTime, endTime);
        }
        return list;
    }
}
