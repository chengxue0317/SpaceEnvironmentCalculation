package cn.piesat.sec.comm.word.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MonthBean extends DocxBean {
    // 过去一个月标题
    private String pastTitle;
    // 过去一个月概述
    private String pastOverview;
    // 下一个月标题
    private String futureTitle;
    // 未来一个月概述
    private String futureOverview;
    // 单位
    private String unit;
    // 大写日期
    private String dateCapital;
}
