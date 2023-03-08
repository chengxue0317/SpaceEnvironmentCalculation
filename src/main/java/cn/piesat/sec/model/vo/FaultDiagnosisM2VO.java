package cn.piesat.sec.model.vo;

import lombok.Data;

@Data
public class FaultDiagnosisM2VO {
    private String type;
    private String fileName;
    private double[] data;
}
