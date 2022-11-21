package cn.piesat.sec.comm.word.domain;

import com.deepoove.poi.data.TableRenderData;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DailyShortBean extends DocxBean {
    // 过去24小时预报
    private String textBef24;
    // 未来3天预报
    private String textAft3d;
    // 过去24小时环境警报信息
    private TableRenderData warn24bef;
    // 未来3天太空环境预警信息
    private TableRenderData warn3dayAft;
    // 单位
    private String unit;
    // 报送人
    private String sender;
    // 联系人
    private String concat;
    // 联系电话
    private String phone;

    public DailyShortBean() {
        super();
    }
}
