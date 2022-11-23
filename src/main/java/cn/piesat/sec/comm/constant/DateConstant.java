package cn.piesat.sec.comm.constant;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 日期常量
 */
public class DateConstant {
    /**
     * 日期中文常量
     */
    public static final Map<Integer, String> DATE_CONST = Collections.unmodifiableMap(new HashMap<Integer, String>() {
        {
            put(0, "○");
            put(1, "一");
            put(2, "二");
            put(3, "三");
            put(4, "四");
            put(5, "五");
            put(6, "六");
            put(7, "七");
            put(8, "八");
            put(9, "九");
            put(10, "十");
            put(11, "十一");
            put(12, "十二");
            put(13, "十三");
            put(14, "十四");
            put(15, "十五");
            put(16, "十六");
            put(17, "十七");
            put(18, "十八");
            put(19, "十九");
            put(20, "二十");
            put(21, "二十一");
            put(22, "二十二");
            put(23, "二十三");
            put(24, "二十四");
            put(25, "二十五");
            put(26, "二十六");
            put(27, "二十七");
            put(28, "二十八");
            put(29, "二十九");
            put(30, "三十");
            put(31, "三十一");
        }
    });

    /**
     * 时间格式
     */
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 日期格式
     */
    public static final String DATE_PATTERN = "yyyy-MM-dd";
}
