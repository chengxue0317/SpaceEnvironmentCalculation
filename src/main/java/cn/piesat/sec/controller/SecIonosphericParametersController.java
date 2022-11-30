package cn.piesat.sec.controller;

import cn.piesat.sec.model.vo.IonosphericParametersVO;
import cn.piesat.sec.service.SecIonosphericParametersService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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
    private final SecIonosphericParametersService secIPS;

    /**
     * 获取电离层参数多站最新数据
     *
     * @param type 电离层参数类型
     * @return 电离层参数站点数据
     */
    @ApiOperation("获取电离层参数站点数据")
    @GetMapping("ionosphericparametersStationData")
    public List<IonosphericParametersVO> getIonosphericparametersStationData(@RequestParam("type") String type) {
        List<IonosphericParametersVO> list = new ArrayList<IonosphericParametersVO>();
        if (null == type) {
            return list;
        } else {
            switch (type) {
                case "s4": {
                    IonosphericParametersVO v1 = new IonosphericParametersVO();
                    v1.setName("长江1号");
                    v1.setSrc("http://127.0.0.1:9999/dtec_01.png");
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
    public List<IonosphericParametersVO> getIonosphericparametersData(@RequestParam("type") String type,
                                                                      @RequestParam(value = "staId", required = false) String staId,
                                                                      @RequestParam("startTime") String startTime,
                                                                      @RequestParam("endTime") String endTime) {
        List list = new ArrayList();
        if (null == type) {
            // todo
        } else {
            switch (type) {
                case "s4": {
                    // todo 算法联调
                    break;
                }
                case "roti": {
                    // todo 算法联调
                    break;
                }
                default: {
                    list = secIPS.getIonosphericTecPngs(startTime, endTime);
                    break;
                }
            }
        }

        return list;
    }
}
