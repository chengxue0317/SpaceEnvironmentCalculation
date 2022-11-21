package cn.piesat.sec.comm.word.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocxBean {
    // 文件标题
    private String mainTitle;
    // 当前年份
    private String year;
    // 第几期
    private int whichIssue;
    // 子标题
    private String subTitle;
    // xx年xx月xx日
    private String dateZH;

    public DocxBean(){
    }
}
