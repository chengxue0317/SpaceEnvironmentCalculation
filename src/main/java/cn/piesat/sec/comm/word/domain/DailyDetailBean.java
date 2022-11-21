package cn.piesat.sec.comm.word.domain;

import com.deepoove.poi.data.TableRenderData;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DailyDetailBean extends DocxBean {
    // 过去24小时空间环境综述
    private String env24Hbef;
    // 未来3天空间形势预报
    private String env3Daft;
    // 未来3天空间环境参数与事件概率预报
    private TableRenderData table3Daft;
    // 单位
    private String unit;
    // 大写日期
    private String dateCapital;
    // 未来一天电离层闪烁发生概率预报
    private TableRenderData tableDayAft;
    // 电离层foF2单站72小时预报
    private TableRenderData table3dayAft;
    // 电离层foF2区域分布24小时预报
    private TableRenderData tableDayfoF2;
    //未来一天电离层闪烁发生概率预报
    private String aftdayPath;
    //电离层foF2单站72小时预报
    private String fof272Path;
    //电离层foF2区域分布24小时预报
    private String fof224Path;

    public DailyDetailBean() {
        super();
    }
}
