package cn.piesat.sec.service;

import cn.piesat.sec.model.vo.SecEnvElementVO;
import cn.piesat.sec.model.vo.SecIonosphericParametersVO;

import java.util.List;

public interface SecIonosphericParametersService {
    /**
     * 获取闪烁数据
     *
     * @param staId     站点id
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    SecEnvElementVO getBlinkData(String staId, String startTime, String endTime);

    /**
     * 获取全国多站TEC效果图
     *
     * @return TEC效果图
     */
    List<SecIonosphericParametersVO> getIonosphericStationsTECPngs();

    /**
     * 获取全国TEC效果图
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return TEC效果图
     */
    List<SecIonosphericParametersVO> getIonosphericTecPngs(String startTime, String endTime);

    /**
     * 获取站点时段ROTI效果图
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param staId     站点id
     * @return ROTI效果图
     */
    List<SecIonosphericParametersVO> getIonosphericRotiPngs(String startTime, String endTime, String staId);
}
