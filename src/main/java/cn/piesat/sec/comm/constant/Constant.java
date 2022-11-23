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
     * 编码格式
     */
    public static String UTF8 = "utf-8";

    /**
     * 文件后缀
     */
    public static String SUFFIX_07 = ".docx";

    /**
     * 存放数据相对根文件夹
     */
    public static String BASE_DIR = "/sec/";

    /**
     * docx模板文件存放路径
     */
    public static String MODEL_PATH = BASE_DIR + "doc/model/";

    /**
     * 报文存放文件夹
     */
    public static String REPORT = "report";

    /**
     * 日报文件名称
     */
    public static String DAILY_REPORT_NAME = "report";

    /**
     * 缓存大小
     */
    public static int BUFFSIZE = 10240;

    /**
     * 文件分割符（win、linux都用/分隔）
     */
    public static String FILE_SEPARATOR = "/";

    /**
     * 表格文字大小
     */
    public static int TB_FONT_SIZE = 14; // 4号大小

    /**
     * 表格文字大小-偏小
     */
    public static int TB_FONT_SMALL_SIZE = 10;

    /**
     * 表格行高
     */
    public static double TB_ROW_HEIGHT = 1;

    /**
     * 第一期开始日期
     */
    public static String START_PERIOD = "2021-04-26";

    /**
     * 图片后缀
     */
    public static String PNG = ".png";

    /**
     * 表格最大宽度
     */
    public static double TB_MAX_WIDTH = 14.63;

    /**
     * 无警报
     */
    public static String DEFAULT_WARN_STR = "无警报";

    /**
     * 黄色警报
     */
    public static String WARN_YELLOW = "黄色警报";

    /**
     * 橙色警报
     */
    public static String WARN_ORANGE = "橙色警报";

    /**
     * 红色警报
     */
    public static String WARN_RED = "红色警报";

    /**
     * 中国星网
     */
    public static String UNIT = "中国星网";

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
}

