package cn.piesat.sec.comm.constant;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;

/**
 * 常量类
 *
 * @author wuyazhou
 * @date 2022-11-10
 */
@Api(tags = "常量类")
public class Constant {
    /**
     * 编码格式
     */
    @ApiModelProperty("编码格式")
    public static String UTF8 = "utf-8";

    /**
     * 文件后缀
     */
    @ApiModelProperty("文件后缀")
    public static String SUFFIX_07 = ".docx";

    /**
     * 存放数据相对根文件夹
     */
    @ApiModelProperty("存放数据相对根文件夹")
    public static String BASE_DIR = "/sec/";

    /**
     * docx模板文件存放路径
     */
    @ApiModelProperty("docx模板文件存放路径")
    public static String MODEL_PATH = BASE_DIR + "doc/model/";

    /**
     * 报文存放文件夹
     */
    @ApiModelProperty("报文存放文件夹")
    public static String REPORT = "report";

    /**
     * 日报文件名称
     */
    @ApiModelProperty("报文存放文件夹")
    public static String DAILY_REPORT_NAME = "report";

    /**
     * 缓存大小
     */
    @ApiModelProperty("缓存大小")
    public static int BUFFSIZE = 10240;

    /**
     * 文件分割符（win、linux都用/分隔）
     */
    @ApiModelProperty("文件分割符")
    public static String FILE_SEPARATOR = "/";

    /**
     * 表格文字大小
     */
    @ApiModelProperty("表格文字大小")
    public static int TB_FONT_SIZE = 14; // 4号大小

    /**
     * 表格文字大小-偏小
     */
    @ApiModelProperty("表格文字大小-偏小")
    public static int TB_FONT_SMALL_SIZE = 10;

    /**
     * 表格行高
     */
    @ApiModelProperty("表格行高")
    public static double TB_ROW_HEIGHT = 1;

    /**
     * 第一期开始日期
     */
    @ApiModelProperty("第一期开始日期")
    public static String START_PERIOD = "2021-04-26";

    /**
     * 图片后缀
     */
    @ApiModelProperty("图片后缀")
    public static String PNG = ".png";

    /**
     * 表格最大宽度
     */
    @ApiModelProperty("表格最大宽度")
    public static double TB_MAX_WIDTH = 14.63;

    /**
     * 无警报
     */
    @ApiModelProperty("无警报")
    public static String DEFAULT_WARN_STR = "无警报";

    /**
     * 黄色警报
     */
    @ApiModelProperty("黄色警报")
    public static String WARN_YELLOW = "黄色警报";

    /**
     * 橙色警报
     */
    @ApiModelProperty("橙色警报")
    public static String WARN_ORANGE = "橙色警报";

    /**
     * 红色警报
     */
    @ApiModelProperty("红色警报")
    public static String WARN_RED = "红色警报";

    /**
     * 中国星网
     */
    @ApiModelProperty("中国星网")
    public static String UNIT = "中国星网";

}

