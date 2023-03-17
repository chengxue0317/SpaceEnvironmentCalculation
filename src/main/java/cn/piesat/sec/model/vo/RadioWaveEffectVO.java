package cn.piesat.sec.model.vo;

import lombok.Data;

@Data
public class RadioWaveEffectVO {
    private String filePath;
    //查看电波传播数据时间
    private String time;
    //观测卫星类型（GPS、GLONASS、SBAS、BDS、GAlileo）
    private String system;
    /**
     * 卫星编号
     * G卫星名称编号：[04,05,06,09,11,12,14,17,19,20,30]
     * R卫星名称编号：[03,04,05,09,10,16,18,19,20]
     * E卫星名称编号：[01,04,07,10,12,19,21,24,26,31,33]
     * C卫星名称编号：[11,14,23,27,28,32,33,37]
     */
    private String prn;
    //时间间隔：1代表1秒
    private String interval;
    //需要查看时长
    private String forecastPeriod;
    //卫星类型代号  {'GPS': 'G', 'GLONASS': 'R', 'SBAS': 'S', 'GAL': 'E', 'BDS': 'C'}  #目前观测数据只有5种卫星一种卫星名称代号代表一种卫星类型
    private String paramsSystem;
    /**
     * P1_channel，P2_channel均为卫星频率
     * G卫星的频率为[C1C,C1W,C2W,C2L,C5Q]
     * C卫星的频率为[C2I,C6I,C7I]
     * R卫星的频率为[C1C,C1P,C2C,C2P]
     * E卫星的频率为[C1C,C6I,C7I]
     */
    private String p1Channel;
    private String p2Channel;
    //开始时间
    private String statTime;
    //结束时间
    private String endTime;
}
