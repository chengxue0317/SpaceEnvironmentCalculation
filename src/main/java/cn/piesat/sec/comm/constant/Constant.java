package cn.piesat.sec.comm.constant;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 常量类
 *
 * @author wuyazhou
 * @date 2022-11-10
 */
public class Constant {
    /**
     * 编码格式-utf8
     */
    public final static String UTF8 = "utf-8";

    /**
     * 编码格式-GBK
     */
    public final static String GBK = "GBK";

    /**
     * 报文存放文件夹
     */
    public final static String REPORT = "/report";

    /**
     * 缓存大小
     */
    public final static int BUFFSIZE = 10240;

    /**
     * 文件分割符（win、linux都用/分隔）
     */
    public final static String FILE_SEPARATOR = "/";

    /**
     * 表格文字大小
     */
    public final static int TB_FONT_SIZE = 14; // 4号大小

    /**
     * 表格文字大小-偏小
     */
    public final static int TB_FONT_SMALL_SIZE = 10;

    /**
     * 表格行高
     */
    public final static double TB_ROW_HEIGHT = 1;

    /**
     * 第一期开始日期
     */
    public final static String START_PERIOD = "2021-04-26";

    /**
     * 图片后缀
     */
    public final static String PNG = ".png";

    /**
     * 表格最大宽度
     */
    public final static double TB_MAX_WIDTH = 14.63;

    /**
     * 无警报
     */
    public final static String DEFAULT_WARN_STR = "无警报";

    /**
     * 黄色警报
     */
    public final static String WARN_YELLOW = "黄色警报";

    /**
     * 橙色警报
     */
    public final static String WARN_ORANGE = "橙色警报";

    /**
     * 红色警报
     */
    public final static String WARN_RED = "红色警报";

    /**
     * 中国星网
     */
    public final static String UNIT = "中国星网";

    /**
     * 警报事件
     */
    public static final Map<Integer, String> WARN_LEVEL = Collections.unmodifiableMap(new HashMap<Integer, String>() {
        {
            put(1, "黄色");
            put(2, "橙色");
            put(3, "红色");
        }
    });

    /**
     * 图片宽度
     */
    public static final int PIC_WIDTH = 500;

    /**
     * 图片高度
     */
    public static final int PIC_HIGH = 300;

    /**
     * 分号分隔符
     */
    public static final String SEMICOLON = ";";

    /**
     * 数据分割符号
     */
    public static final String DATA_SEPERATOR = " ";
}

