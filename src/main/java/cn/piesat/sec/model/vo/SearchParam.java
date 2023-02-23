package cn.piesat.sec.model.vo;

import lombok.Data;

@Data
public class SearchParam {

    private String createTime;
    private Integer pageNum;
    private Integer pageSize;
}
