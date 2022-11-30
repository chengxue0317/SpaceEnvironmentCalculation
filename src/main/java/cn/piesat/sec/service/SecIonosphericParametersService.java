package cn.piesat.sec.service;

import cn.piesat.sec.model.vo.IonosphericParametersVO;

import java.util.List;

public interface SecIonosphericParametersService {
    /**
     * 获取全国多站TEC效果图
     *
     * @return TEC效果图
     */
    List<IonosphericParametersVO> getIonosphericStationsTECPngs();

    /**
     * 获取全国TEC效果图
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return TEC效果图
     */
    List<IonosphericParametersVO> getIonosphericTecPngs(String startTime, String endTime);
}
