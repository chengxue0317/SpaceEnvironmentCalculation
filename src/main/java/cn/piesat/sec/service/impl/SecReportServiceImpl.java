package cn.piesat.sec.service.impl;

import cn.piesat.sec.comm.conf.SecConfig;
import cn.piesat.sec.comm.constant.Constant;
import cn.piesat.sec.comm.constant.DateConstant;
import cn.piesat.sec.comm.util.DateUtil;
import cn.piesat.sec.comm.util.FileUtil;
import cn.piesat.sec.comm.word.CommonWordUtil;
import cn.piesat.sec.comm.word.DailyShorUtil;
import cn.piesat.sec.comm.word.MonthUtil;
import cn.piesat.sec.comm.word.WeekDetailUtil;
import cn.piesat.sec.comm.word.domain.DailyShortBean;
import cn.piesat.sec.comm.word.domain.MonthBean;
import cn.piesat.sec.comm.word.domain.WeekDetailBean;
import cn.piesat.sec.dao.mapper.*;
import cn.piesat.sec.model.entity.*;
import cn.piesat.sec.model.vo.SecEnvOverviewVO;
import cn.piesat.sec.service.SecReportService;
import com.deepoove.poi.data.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class SecReportServiceImpl implements SecReportService {
    private static final Logger logger = LoggerFactory.getLogger(SecReportServiceImpl.class);

    @Autowired
    private SecAlarmEventMapper secAlarmEventMapper;

    @Autowired
    private SecEnvOverviewMapper secEnvOverviewMapper;

    @Autowired
    private SecXrayAlarmMapper secxrayalarmmapper;

    @Autowired
    private SecSsnMapper secSsnMapper;

    @Autowired
    private SecF107FluxMapper secF107FluxMapper;

    @Autowired
    private SecProtonAlarmMapper secProtonAlarmMapper;

    @Autowired
    private SecApIndexMapper secApIndexMapper;

    @Autowired
    private SecKpIndexMapper secKpIndexMapper;

    @Override
    public synchronized String makeReport(String type) {
        String reportPath;
        switch (type.toLowerCase(Locale.ROOT)) {
            case "shortday": {
                reportPath = makeShortDayReport();
                break;
            }
            case "week": {
                reportPath = makeWeekReport();
                break;
            }
            case "month": {
                reportPath = makeMonthReport();
                break;
            }
            default: {
                reportPath = makeDayReport();
                break;
            }
        }
        return reportPath;
    }

    @Override
    public String makeShortDayReport() {
        String targetDir = SecConfig.getProfile() + FileUtil.getAdayFilePath(0) + Constant.REPORT + Constant.FILE_SEPARATOR;
        FileUtil.mkdirs(targetDir); // 如果文件夹不存在则创建文件夹
        String fileName = "空间环境日报" + DateUtil.parseDate(LocalDateTime.now(), "yyyyMMdd") + ".docx"; // 输出文件名称和路径
        String tarPath = targetDir + fileName;
        DailyShortBean dailyshotBean = new DailyShortBean();
        dailyshotBean.setWhichIssue(DateUtil.getDailyPeriodical());
        dailyshotBean.setUnit(Constant.UNIT);
        dailyshotBean.setDateZH(DateUtil.getToDayZH());
        String yesterday = DateUtil.getDaysLater(-1);
        String startTime = yesterday + " 00:00:00";
        String endTime = yesterday + " 23:59:59";

        String[][] arr = bef24EnvDefaultWarnData();
        Map<String, Object> fourthAlramEventsLevel = checkBef24WarnInfo(startTime, endTime);

        if (null != fourthAlramEventsLevel && fourthAlramEventsLevel.size() > 0) {
            // 过去24小时、未来3天综述
            setOverviewTxt(dailyshotBean, fourthAlramEventsLevel);
            setBef24Table(arr, fourthAlramEventsLevel);
        }

        //动态生成表格[过去24小时环境警报信息]数据
        double[] colCmWidths = new double[]{1.8, 7.2, 6}; // 设置列宽
        TableRenderData tbrd = Tables.ofWidth(Constant.TB_MAX_WIDTH, colCmWidths).center().create();
        List<RowRenderData> rows = CommonWordUtil.createTableData(arr);
        tbrd.setRows(rows);
        dailyshotBean.setWarn24bef(tbrd);

        // 动态生成表格[未来3天太空环境预警信息]
        String[][] arr2 = aft3dEnvDefaultWarnData();
        double[] colCmWidths2 = new double[]{1.8, 4.5, 3, 3, 3}; // 设置列宽
        TableRenderData tbrd2 = Tables.ofWidth(Constant.TB_MAX_WIDTH, colCmWidths2).center().create();
        List<RowRenderData> rows2 = DailyShorUtil.createTableData(arr2);
        tbrd2.setRows(rows2);
        dailyshotBean.setWarn3dayAft(tbrd2);

        FileUtil.delDirFiles(FileUtils.getFile(tarPath), true); // 如果文件已经存在则删除文件
        InputStream model = this.getClass().getResourceAsStream("/word/dailyShort.docx");
        DailyShorUtil.createDailyDetailDocx(model, tarPath, dailyshotBean);
        // 数据入库
        try {
            // 更新数据库数据
            secAlarmEventMapper.updatePath(startTime, tarPath.replace(SecConfig.getProfile(), ""), "day");
        } catch (Exception e) {
            logger.error(String.format(Locale.ROOT, "-----method makeShortDayReport----Insert message data exception  %s", e.getMessage()));
        } finally {
            return tarPath;
        }
    }

    @Override
    public String makeDayReport() {
        return makeShortDayReport();
    }

    @Override
    public String makeWeekReport() {
        String targetDir = SecConfig.getProfile() + FileUtil.getAdayFilePath(0) + Constant.REPORT + Constant.FILE_SEPARATOR;
        FileUtil.mkdirs(targetDir); // 如果文件夹不存在则创建文件夹
        String fileName = "空间环境周报" + DateUtil.parseDate(LocalDateTime.now(), "yyyyMMdd") + ".docx"; // 输出文件名称和路径
        String tarPath = targetDir + fileName;
        WeekDetailBean weekDetailBean = new WeekDetailBean();
        weekDetailBean.setYear(DateUtil.getToDay().substring(0, 4));
        weekDetailBean.setWhichIssue(DateUtil.getWeekPeriodical());
        weekDetailBean.setSubTitle(Constant.UNIT);
        weekDetailBean.setDateZH(DateUtil.getToDayZH());

        // 周报存在周五这天数据，统计一周数据是周五到周四，所以是在周五这天出上周统计结果
        String pointDay = DateUtil.getApointedDay(5, DateConstant.DATE_PATTERN);
        String startTime = pointDay + " 00:00:00";
        String endTime = pointDay + " 23:59:59";
        setOverviewData(weekDetailBean, startTime, endTime);

        // 图片
        String picOutPath = targetDir + "week.png";
        File pic = FileUtils.getFile(picOutPath);
        if (pic.exists()) {
            weekDetailBean.setPicTitleA("图1  MMM卫星环境");
            weekDetailBean.setPicA(new PictureRenderData(500, 300, picOutPath));
        }

        //表格(查询最近一周周五~周四的数据)
        List<String> lastFriday2Thursday = DateUtil.getLastFriday2Thursday(null, DateConstant.DATE_PATTERN);
        String begin = lastFriday2Thursday.get(0);
        String end = lastFriday2Thursday.get(1);
        List<String> dateList = DateUtil.getDateList(begin, 7, DateConstant.DATE_PATTERN);
        String[][] table = new String[9][];
        try {
            begin += " 00:00:00";
            end += " 23:59:59";
            setTableData(weekDetailBean, begin, end, dateList, table);
            weekDetailBean.setUnit("中国星网");
            weekDetailBean.setDateCapital(DateUtil.getToDayCapital());
            InputStream model = this.getClass().getResourceAsStream("/word/weekDetail.docx");
            WeekDetailUtil.createDailyDetailDocx(model, tarPath, weekDetailBean);
            // 更新数据库数据
            secAlarmEventMapper.updatePath(pointDay, tarPath.replace(SecConfig.getProfile(), ""), "week");
        } catch (Exception e) {
            logger.error(String.format(Locale.ROOT, "-----method makeShortDayReport----Insert message data exception  %s", e.getMessage()));
        } finally {
            return tarPath;
        }
    }

    @Override
    public String makeMonthReport() {
        String targetDir = SecConfig.getProfile() + FileUtil.getAdayFilePath(0) + Constant.REPORT + Constant.FILE_SEPARATOR;
        FileUtil.mkdirs(targetDir);
        String fileName = "空间环境月报" + DateUtil.parseDate(LocalDateTime.now(), "yyyyMMdd") + ".docx"; // 输出文件名称和路径
        String tarPath = targetDir + fileName;
        MonthBean monthBean = new MonthBean();
        monthBean.setYear(DateUtil.getToDay().substring(0, 4));
        monthBean.setWhichIssue(DateUtil.getWeekPeriodical());
        monthBean.setSubTitle(Constant.UNIT);
        monthBean.setDateZH(DateUtil.getToDayZH());
        InputStream model = this.getClass().getResourceAsStream("/word/monthDetail.docx");
        MonthUtil.createDailyMonthDocx(model, tarPath, monthBean);
        return tarPath;
    }

    private void setTableData(WeekDetailBean weekDetailBean, String startTime, String endTime, List<String> dateList, String[][] table) throws Exception {
        Map<String, SecXrayAlarmDO> xrayMap = new HashMap<>();
        Map<String, SecSsnDO> ssnMap = new HashMap<>();
        Map<String, SecF107FluxDO> f107Map = new HashMap<>();
        Map<String, SecProtonAlarmDO> protonMap = new HashMap<>();
        Map<String, SecApIndexDO> apMap = new HashMap<>();
        Map<String, SecKpIndexDO> kpMap = new HashMap<>();

        // 查询一周的M级以上耀斑
        getXrayWeekData(startTime, endTime, xrayMap);
        // 查询一周的太阳黑子数
        getSsnWeeksnData(startTime, endTime, ssnMap);
        // 查询一周的F10.7
        getf107WeeksnData(startTime, endTime, f107Map);
        // 查询一周是否发生质子事件
        getProtonWeeksnData(startTime, endTime, protonMap);
        // 高能电子通量
        // 查询一周的AP
        getApWeeleksnData(startTime, endTime, apMap);
        // 查询一周的KP
        getKpWeeleksnData(startTime, endTime, kpMap);
        setTableData(weekDetailBean, dateList, table, ssnMap, f107Map, protonMap, apMap, kpMap);
    }

    private void setOverviewData(WeekDetailBean weekDetailBean, String startTime, String endTime) {
        try {
            List<SecEnvOverviewVO> weekOverview = secEnvOverviewMapper.getWeekOverview(startTime, endTime);
            if (CollectionUtils.isNotEmpty(weekOverview)) {
                SecEnvOverviewVO secEnvOverviewVO = weekOverview.get(0);
                weekDetailBean.setPastOverView(secEnvOverviewVO.getBefweek());
                weekDetailBean.setFutureOverView(secEnvOverviewVO.getBefweek());
            }
        } catch (Exception e) {
            logger.error(String.format(Locale.ROOT, "", e.getMessage()));
        }
    }

    private void setTableData(WeekDetailBean weekDetailBean, List<String> dateList, String[][] table, Map<String, SecSsnDO> ssnMap, Map<String, SecF107FluxDO> f107Map, Map<String, SecProtonAlarmDO> protonMap, Map<String, SecApIndexDO> apMap, Map<String, SecKpIndexDO> kpMap) {
        String[] row0 = new String[]{"日期", "M级以上X射线耀斑", "", "", "", "F10.7射电流量", "黑子数", "是否发生质子事件", "高能电子通量(Electrons/cm2-day-sr)", "地磁Ap指数(Kp)"};
        String[] row1 = new String[]{"", "开始", "最大", "结束", "级别", "", "", "", "", ""};
        table[0] = row0;
        table[1] = row1;
        weekDetailBean.setTableTitle("表1 太阳地磁活动数据表");
        for (int i = 0; i < 7; i++) {
            String time = dateList.get(i);
            SecF107FluxDO secF107FluxDo = f107Map.get(time);
            String f107 = secF107FluxDo == null ? "" : String.valueOf(secF107FluxDo.getF107());
            SecSsnDO ssnDo = ssnMap.get(time);
            String ssn = ssnDo == null ? "0" : String.valueOf(ssnDo.getSsn());
            SecProtonAlarmDO protonDo = protonMap.get(time);
            String proton = protonDo == null ? "否" : protonDo.getLevel() > 0 ? "是" : "否";
            SecApIndexDO apDo = apMap.get(time);
            String ap = apDo == null ? "" : String.valueOf(apDo.getAp());
            SecKpIndexDO kpDo = kpMap.get(time);
            String kp = kpDo == null ? "" : "" + kpDo.getKp1() + kpDo.getKp2() + kpDo.getKp3()
                    + kpDo.getKp4() + kpDo.getKp5() + kpDo.getKp6() + kpDo.getKp7() + kpDo.getKp8();
            ap += kp.length() > 0 ? "(" + kp + ")" : "";
            ap = ap.replaceAll(".0", "");
            String[] row = new String[]{time, "", "", "", "", f107, ssn, proton, "", ap};
            table[i + 2] = row;
        }
        List<RowRenderData> tableData = WeekDetailUtil.createTableData(table);
        MergeCellRule mgRule = MergeCellRule.builder()
                .map(MergeCellRule.Grid.of(0, 1), MergeCellRule.Grid.of(0, 4))
                .map(MergeCellRule.Grid.of(0, 0), MergeCellRule.Grid.of(1, 0))
                .map(MergeCellRule.Grid.of(0, 5), MergeCellRule.Grid.of(1, 5))
                .map(MergeCellRule.Grid.of(0, 6), MergeCellRule.Grid.of(1, 6))
                .map(MergeCellRule.Grid.of(0, 7), MergeCellRule.Grid.of(1, 7))
                .map(MergeCellRule.Grid.of(0, 8), MergeCellRule.Grid.of(1, 8))
                .map(MergeCellRule.Grid.of(0, 9), MergeCellRule.Grid.of(1, 9))
                .build();
        TableRenderData tb = Tables.ofWidth(17).width(17, new double[]{2, 1.25, 1.25, 1.25, 1.25, 2, 1, 2, 2.5, 2.5}).center().mergeRule(mgRule).create();
        tb.setRows(tableData);
        weekDetailBean.setTableConent(tb);
    }

    private void getKpWeeleksnData(String startTime, String endTime, Map<String, SecKpIndexDO> kpMap) throws Exception {
        List<SecKpIndexDO> kpList = secKpIndexMapper.getKPData(startTime, endTime);
        if (CollectionUtils.isNotEmpty(kpList)) {
            for (SecKpIndexDO kpdo : kpList) {
                kpMap.put(DateUtil.parseDate(kpdo.getTime(), DateConstant.DATE_TIME_PATTERN).substring(0, 10), kpdo);
            }
        }
    }

    private void getApWeeleksnData(String startTime, String endTime, Map<String, SecApIndexDO> apMap) throws Exception {
        List<SecApIndexDO> apList = secApIndexMapper.getAPData(startTime, endTime);
        if (CollectionUtils.isNotEmpty(apList)) {
            for (SecApIndexDO apdo : apList) {
                apMap.put(DateUtil.parseDate(apdo.getTime(), DateConstant.DATE_TIME_PATTERN).substring(0, 10), apdo);
            }
        }
    }

    private void getProtonWeeksnData(String startTime, String endTime, Map<String, SecProtonAlarmDO> protonMap) {
        List<SecProtonAlarmDO> protonList = secProtonAlarmMapper.getSecProton(startTime, endTime);
        if (CollectionUtils.isNotEmpty(protonList)) {
            for (SecProtonAlarmDO protondo : protonList) {
                protonMap.put(DateUtil.parseDate(protondo.getThresholdTime(), DateConstant.DATE_TIME_PATTERN).substring(0, 10), protondo);
            }
        }
    }

    private void getf107WeeksnData(String startTime, String endTime, Map<String, SecF107FluxDO> f107Map) throws Exception {
        List<SecF107FluxDO> f107FluxList = secF107FluxMapper.getF107Data(startTime, endTime);
        if (CollectionUtils.isNotEmpty(f107FluxList)) {
            for (SecF107FluxDO fluxdo : f107FluxList) {
                f107Map.put(DateUtil.parseDate(fluxdo.getTime(), DateConstant.DATE_TIME_PATTERN).substring(0, 10), fluxdo);
            }
        }
    }

    private void getSsnWeeksnData(String startTime, String endTime, Map<String, SecSsnDO> ssnMap) throws Exception {
        List<SecSsnDO> sunSpotDataList = secSsnMapper.getSunSpotData(startTime, endTime);
        if (CollectionUtils.isNotEmpty(sunSpotDataList)) {
            for (SecSsnDO sndo : sunSpotDataList) {
                ssnMap.put(DateUtil.parseDate(sndo.getTime(), DateConstant.DATE_TIME_PATTERN).substring(0, 10), sndo);
            }
        }
    }

    private void getXrayWeekData(String startTime, String endTime, Map<String, SecXrayAlarmDO> xrayMap) throws Exception {
        List<SecXrayAlarmDO> xrayAlarmList = secxrayalarmmapper.getXrayAlarmList(startTime, endTime);
        if (CollectionUtils.isNotEmpty(xrayAlarmList)) {
            for (SecXrayAlarmDO ddo : xrayAlarmList) {
                xrayMap.put(DateUtil.parseDate(ddo.getThresholdTime(), DateConstant.DATE_TIME_PATTERN).substring(0, 10), ddo);
            }
        }
    }

    private void setBef24Table(String[][] arr, Map<String, Object> fourthAlramEventsLevel) {
        arr[1][2] = checkWarnLevel(fourthAlramEventsLevel.get("xray"));
        arr[2][2] = checkWarnLevel(fourthAlramEventsLevel.get("proton"));
        arr[3][2] = checkWarnLevel(fourthAlramEventsLevel.get("ele"));
        arr[4][2] = checkWarnLevel(fourthAlramEventsLevel.get("dst"));
    }

    private void setOverviewTxt(DailyShortBean dailyshotBean, Map<String, Object> fourthAlramEventsLevel) {
        Object bef24hObj = fourthAlramEventsLevel.get("BEF24H");
        Object aft3dayObj = fourthAlramEventsLevel.get("AFT3DAY");
        String bef24h = bef24hObj == null ? "" : bef24hObj.toString().replaceAll("</br>", "\n    ");
        String aft3day = aft3dayObj == null ? "" : aft3dayObj.toString().replaceAll("</br>", "\n    ");
        dailyshotBean.setTextBef24(bef24h);
        dailyshotBean.setTextAft3d(aft3day);
    }

    @NotNull
    private String[][] bef24EnvDefaultWarnData() {
        String[][] arr = new String[5][];
        arr[0] = new String[]{"序号", "环境事件", "警报等级"};
        arr[1] = new String[]{"1", "太阳X射线耀斑", Constant.DEFAULT_WARN_STR};
        arr[2] = new String[]{"2", "太阳质子事件", Constant.DEFAULT_WARN_STR};
        arr[3] = new String[]{"3", "高能电子暴", Constant.DEFAULT_WARN_STR};
        arr[4] = new String[]{"4", "地磁暴", Constant.DEFAULT_WARN_STR};
        return arr;
    }

    @NotNull
    private String[][] aft3dEnvDefaultWarnData() {
        String[][] arr2 = new String[5][];
        String pattern = "MM月dd日";
        String aft24 = DateUtil.getDaysLater(0, pattern);
        String aft48 = DateUtil.getDaysLater(1, pattern);
        String aft72 = DateUtil.getDaysLater(2, pattern);
        List<SecAlarmForecastDO> alarmEvent3daysForecast = secAlarmEventMapper.getAlarmEvent3daysForecast();
        if (CollectionUtils.isNotEmpty(alarmEvent3daysForecast)) {
            SecAlarmForecastDO afdo = alarmEvent3daysForecast.get(0);
            arr2[0] = new String[]{"序号", "环境事件", aft24, aft48, aft72};
            arr2[1] = new String[]{"1", "太阳X射线耀斑", checkWarnLevel(afdo.getSxr1()), checkWarnLevel(afdo.getSxr2()), checkWarnLevel(afdo.getSxr3())};
            arr2[2] = new String[]{"2", "太阳质子事件", checkWarnLevel(afdo.getSpe1()), checkWarnLevel(afdo.getSpe2()), checkWarnLevel(afdo.getSpe3())};
            arr2[3] = new String[]{"3", "高能电子暴", checkWarnLevel(afdo.getRee1()), checkWarnLevel(afdo.getRee2()), checkWarnLevel(afdo.getRee3())};
            arr2[4] = new String[]{"4", "地磁暴", checkWarnLevel(afdo.getGsma1()), checkWarnLevel(afdo.getGsma2()), checkWarnLevel(afdo.getGsma3())};
        } else {
            arr2[1] = new String[]{"1", "太阳X射线耀斑", Constant.DEFAULT_WARN_STR, Constant.DEFAULT_WARN_STR, Constant.DEFAULT_WARN_STR};
            arr2[2] = new String[]{"2", "太阳质子事件", Constant.DEFAULT_WARN_STR, Constant.DEFAULT_WARN_STR, Constant.DEFAULT_WARN_STR};
            arr2[3] = new String[]{"3", "高能电子暴", Constant.DEFAULT_WARN_STR, Constant.DEFAULT_WARN_STR, Constant.DEFAULT_WARN_STR};
            arr2[4] = new String[]{"4", "地磁暴", Constant.DEFAULT_WARN_STR, Constant.DEFAULT_WARN_STR, Constant.DEFAULT_WARN_STR};
        }
        return arr2;
    }


    private Map<String, Object> checkBef24WarnInfo(String startTime, String endTime) {
        Map<String, Object> fourthAlramEventsLevel = null;
        try {
            fourthAlramEventsLevel = secAlarmEventMapper.getFourthAlramEventsBef24h(startTime, endTime);
        } catch (Exception e) {
            logger.error(String.format(Locale.ROOT, "---------Get Daily Newsletter Data Abnormal  %s", e.getMessage()));
            fourthAlramEventsLevel = new HashMap<>();
        } finally {
            return fourthAlramEventsLevel;
        }
    }

    private String checkWarnLevel(Object level) {
        if (level == null) {
            return Constant.DEFAULT_WARN_STR;
        } else {
            Integer lev = Integer.parseInt(level.toString());
            if (lev < 1) {
                return Constant.WARN_YELLOW;
            } else if (lev >= 1 && lev < 2) {
                return Constant.WARN_ORANGE;
            } else if (lev >= 2 && lev < 4) {
                return Constant.WARN_RED;
            } else {
                return Constant.DEFAULT_WARN_STR;
            }

        }

    }

    private String checkWarnLevel(String content) {
        if (content == null) {
            return Constant.DEFAULT_WARN_STR;
        } else {
            if (content.indexOf("红") > -1) {
                return Constant.WARN_RED;
            } else if (content.indexOf("橙") > -1) {
                return Constant.WARN_ORANGE;
            } else if (content.indexOf("黄") > -1) {
                return Constant.WARN_YELLOW;
            } else {
                return Constant.DEFAULT_WARN_STR;
            }
        }
    }

}
