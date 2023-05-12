package cn.piesat.sec.service;

import cn.piesat.sec.model.vo.SecEnvElementVO;
import cn.piesat.sec.model.vo.SecIonosphericParametersVO;

import java.util.List;

public interface SecIonosphericParametersService {
    /**
     * 获取闪烁数据
     *
     * @param satcode      卫星名称编号
     * @param satno        卫星名称编号
     * @param satfrequency 卫星频率
     * @param startTime    开始时间
     * @param endTime      结束时间
     * @return
     */
    SecEnvElementVO getBlinkData(String satcode, String satno, String satfrequency, String startTime, String endTime);

    /**
     * 获取全国多站TEC效果图
     *
     * @param altitude  高度
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return TEC效果图
     */
    List<SecIonosphericParametersVO> getIonosphericChineseTECPngs(String altitude, String startTime, String endTime);

    /**
     * 获取全国/全球TEC效果图
     *
     * @param altitude  高度
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return TEC效果图
     */
    List<SecIonosphericParametersVO> getIonosphericGlobalTecPngs(String altitude, String startTime, String endTime);

    /**
     * 获取站点时段ROTI效果图
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return ROTI效果图
     */
    List<SecIonosphericParametersVO> getIonosphericRotiPngs(String startTime, String endTime);
}
