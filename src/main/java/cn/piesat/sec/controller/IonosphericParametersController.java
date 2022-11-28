package cn.piesat.sec.controller;

import cn.piesat.sec.model.vo.IonosphericParametersVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.ClientInfoStatus;
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
public class IonosphericParametersController {
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
    @PostMapping("getIonosphericparametersData")
    public List<IonosphericParametersVO> getIonosphericparametersData(@RequestParam("type") String type,
                                                                      @RequestParam(value = "staId", required = false) String staId,
                                                                      @RequestParam("startTime") String startTime,
                                                                      @RequestParam("endTime") String endTime) {
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
                    // todo 算法联调
                    break;
                }
            }
        }
        List list = new ArrayList();
        IonosphericParametersVO v1 = new IonosphericParametersVO();
        v1.setName("长江1号");
        v1.setSrc("http://127.0.0.1:9999/dtec_01.png");
        IonosphericParametersVO v2 = new IonosphericParametersVO();
        v2.setName("北京7号北京7号北京7号北京7号北京7号北京7号");
        v2.setSrc("http://127.0.0.1:9999/ROTI_01.png");
        IonosphericParametersVO v3 = new IonosphericParametersVO();
        v3.setName("沪太8号");
        v3.setSrc("http://127.0.0.1:9999/tec_01.png");
        list.add(v1);
        list.add(v2);
        list.add(v3);
        return list;
    }
}
