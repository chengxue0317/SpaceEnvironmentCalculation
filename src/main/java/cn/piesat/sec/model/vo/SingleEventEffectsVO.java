package cn.piesat.sec.model.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SingleEventEffectsVO {

    /**
     * 1mm
     */
    private List<Double> one;
    /**
     * 2mm
     */
    private List<Double> two;
    /**
     * 3mm
     */
    private List<Double> three;
    /**
     * 4mm
     */
    private List<Double> four;
    /**
     * 5mm
     */
    private List<Double> five;
    /**
     * 6mm
     */
    private List<Double> six;
    /**
     * 时间（UTC）
     */
    private List<LocalDateTime> time;
}
