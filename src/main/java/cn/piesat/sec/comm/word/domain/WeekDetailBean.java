package cn.piesat.sec.comm.word.domain;

import com.deepoove.poi.data.PictureRenderData;
import com.deepoove.poi.data.TableRenderData;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeekDetailBean extends DocxBean {
    // 过去一周空间环境综述
    private String pastOverView;
    // 未来一周空间环境形势预报
    private String futureOverView;
    // 图片
    private PictureRenderData picA;
    // 图片标题
    private String picTitleA;
    // 表格标题
    private String tableTitle;
    // 表格
    private TableRenderData tableConent;
    // 单位
    private String unit;
    // 大写日期
    private String dateCapital;

    public WeekDetailBean() {
        super();
    }
}
