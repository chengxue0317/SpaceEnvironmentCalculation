package cn.piesat.sec.service;

/**
 * 报文制作接口
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 21:52:42
 */
public interface SecReportService {
    /**
     * 简版日报制作
     *
     * @return
     */
    String makeShortDayReport();

    /**
     * 日报制作
     *
     * @return
     */
    String makeDayReport();

    /**
     * 周报制作
     *
     * @return
     */
    String makeWeekReport();

    /**
     * 月报制作
     *
     * @return
     */
    String makeMonthReport();

    /**
     * 报文制作
     *
     * @param type 报文类型
     * @return 报文路径
     */
    String makeReport(String type);
}
