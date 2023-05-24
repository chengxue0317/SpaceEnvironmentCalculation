package cn.piesat.sec.comm.constant;

public class KafkaConstant {
    /**
     * 空间环境给时空发送电离层文件消息主题
     */
    public static final String THEME_TEC = "CMS-SDC-SEF";

    /**
     * 空间环境给时空发送ap/kp/f107环境文件消息主题
     */
    public static final String THEME_SPACE_ENV = "CMS-SDC-SED";

    /**
     * 空间环境接收时空发送文件消息主题
     */
    public static final String THEME_CMS_STIP_SEG = "CMS-STIP-SEG";

    /**
     * 卫星高能粒子数据
     */
    public static final String THEME_CSS_IIS_HEPartical = "CSS-IIS-HEPartical";

    /**
     * 空间环境每日预报制备数据,1次/天
     */
    public static final String THEME_CSS_IIS_DayFore = "CSS-IIS-DayFore";

    /**
     * 空间环境周报制备数据 1次/周
     */
        public static final String THEME_CSS_IIS_WeekFore = "CSS-IIS-WeekFore";

    /**
     * 空间环境月报制备数据 1月/次
     */
    public static final String THEME_CSS_IIS_MonthFore = "CSS-IIS-MonthFore";

    /**
     * 空间环境数据更新信息，按需发送，数据帧
     */
    public static final String THEME_CSS_IIS_UpdateMsg = "CSS-IIS-UpdateMsg";

    /**
     * 气象实况数据 更新频次24次 00-23逐小时实况数据
     */
    public static final String THEME_CSS_IIS_WeatherReal = "CSS-IIS-WeatherReal";

    /**
     * 空间环境事件数据
     */
    public static final String THEME_CSS_IIS_Event = "CSS-IIS-Event";

    /**
     * 太阳F10.7指数数据
     */
    public static final String THEME_CSS_IIS_F107 = "CSS-IIS-F107";

    /**
     * 地磁AP数据
     */
    public static final String THEME_CSS_IIS_AP = "CSS-IIS-AP";

    /**
     * 地磁KP指数数据
     */
    public static final String THEME_CSS_IIS_KP = "CSS-IIS-KP";

    /**
     * 电离层TEC数据
     */
    public static final String THEME_CSS_IIS_TEC = "CSS-IIS-TEC";


}
