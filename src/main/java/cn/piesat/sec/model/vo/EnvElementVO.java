package cn.piesat.sec.model.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class EnvElementVO {
    private String titleText;
    private String titleText1;
    private String titleText2;
    private List<Object> dataX = new ArrayList<>();
    private List<Object> dataY = new ArrayList<>();
    private List<Object> dataX1 = new ArrayList<>();
    private List<Object> dataY1 = new ArrayList<>();
    private List<Object> dataX2 = new ArrayList<>();
    private List<Object> dataY2 = new ArrayList<>();
    private List<Object> dataY3 = new ArrayList<>();
    private List<Object> dataY4 = new ArrayList<>();
    private List<Object> dataY5 = new ArrayList<>();
}
