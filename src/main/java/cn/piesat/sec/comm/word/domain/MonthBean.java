package cn.piesat.sec.comm.word.domain;

import com.deepoove.poi.data.PictureRenderData;
import com.deepoove.poi.data.TableRenderData;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MonthBean extends DocxBean {
    // 过去一个月标题
    private String pastTitle;
    // 过去一个月概述
    private String pastOverview;
    // 表名
    private String tableTitle1;
    // 表对象
    private TableRenderData table1;
    // 表名
    private String tableTitle2;
    // 表对象
    private TableRenderData table2;
    // 下一个月标题
    private String futureTitle;
    // 未来一个月概述
    private String futureOverview;

    private PictureRenderData pic1;
    private String picTitle1;

    private PictureRenderData pic2;
    private String picTitle2;

    private PictureRenderData pic3;
    private String picTitle3;

    private PictureRenderData pic4;
    private String picTitle4;

    private PictureRenderData pic5;
    private String picTitle5;

    private PictureRenderData pic6;
    private String picTitle6;
    // 单位
    private String unit;
    // 大写日期
    private String dateCapital;
}
