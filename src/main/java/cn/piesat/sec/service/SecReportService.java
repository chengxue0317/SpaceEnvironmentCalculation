package cn.piesat.sec.service;

import io.swagger.annotations.ApiOperation;

import java.util.Map;

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
    @ApiOperation("日报简报制作")
    String makeShortDayReport();

    /**
     * 日报制作
     *
     * @return
     */
    @ApiOperation("日报制作")
    String makeDayReport();

    /**
     * 周报制作
     *
     * @return
     */
    @ApiOperation("周报制作")
    String makeWeekReport();

    /**
     * 月报制作
     *
     * @return
     */
    @ApiOperation("月报制作")
    String makeMonthReport();

    /**
     * 报文制作
     *
     * @param type 报文类型
     * @return 报文路径
     */
    @ApiOperation("报文制作")
    String makeReport(String type);
}
