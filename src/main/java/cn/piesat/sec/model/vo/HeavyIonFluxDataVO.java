package cn.piesat.sec.model.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
public class HeavyIonFluxDataVO {

    private List<Double> he;
    /**
     * 24~220 MEV LI离子积分通量
     */
    private List<Double> li;
    /**
     * 60~570 MEV HE离子积分通量
     */
    private List<Double> c;
    /**
     * 0.2~1.2 GEV MG离子积分通量
     */
    private List<Double> mg;
    /**
     * 0.3~2.0 MEV AR离子积分通量
     */
    private List<Double> ar;
    /**
     * 0.5~2.0 FE 离子积分通量
     */
    private List<Double> fe;
    /**
     * 时间（UTC）
     */
    private List<LocalDateTime> time;
}
