package cn.piesat.sec.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class PageVo<T> {

    private Integer total;        //总记录数
    private List<T> list;    //结果集
    private Integer pageNum;    // 第几页
    private Integer pageSize;    // 每页记录数
    private Integer pages;        // 总页数

}

