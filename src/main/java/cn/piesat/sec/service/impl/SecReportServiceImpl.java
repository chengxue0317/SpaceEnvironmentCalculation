package cn.piesat.sec.service.impl;

import cn.piesat.sec.comm.constant.Constant;
import cn.piesat.sec.comm.constant.DateConstant;
import cn.piesat.sec.comm.properties.SecFileServerProperties;
import cn.piesat.sec.comm.properties.SecMinioProperties;
import cn.piesat.sec.comm.util.DateUtil;
import cn.piesat.sec.comm.util.FileUtil;
import cn.piesat.sec.comm.util.MinioUtil;
import cn.piesat.sec.comm.util.ProcessUtil;
import cn.piesat.sec.comm.word.CommonWordUtil;
import cn.piesat.sec.comm.word.DailyShorUtil;
import cn.piesat.sec.comm.word.MonthUtil;
import cn.piesat.sec.comm.word.WeekDetailUtil;
import cn.piesat.sec.comm.word.domain.DailyShortBean;
import cn.piesat.sec.comm.word.domain.MonthBean;
import cn.piesat.sec.comm.word.domain.WeekDetailBean;
import cn.piesat.sec.dao.mapper.SecAlarmEventMapper;
import cn.piesat.sec.dao.mapper.SecOverviewMapper;
import cn.piesat.sec.dao.mapper.SecReportMapper;
import cn.piesat.sec.model.entity.SecAlarmForecastDO;
import cn.piesat.sec.model.entity.SecProtonAlarmDO;
import cn.piesat.sec.model.vo.SecOverviewVO;
import cn.piesat.sec.service.SecReportService;
import com.deepoove.poi.data.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class SecReportServiceImpl implements SecReportService {
    private static final Logger logger = LoggerFactory.getLogger(SecReportServiceImpl.class);

    @Autowired
    private SecFileServerProperties secFileServerProperties;

    @Autowired
    private MinioUtil minioUtil;

    @Autowired
    private SecMinioProperties secMinioProperties;

    @Autowired
    private SecAlarmEventMapper secAlarmEventMapper;

    @Autowired
    private SecOverviewMapper secOverviewMapper;

    @Autowired
    private SecReportMapper secReportMapper;

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
        String targetDir = secFileServerProperties.getProfile() + FileUtil.getAdayFilePath(0) + Constant.REPORT + Constant.FILE_SEPARATOR;
        FileUtil.mkdirs(targetDir); // 如果文件夹不存在则创建文件夹
        String fileName = "空间环境日报" + DateUtil.parseDate(LocalDateTime.now(), "yyyyMMdd") + ".docx"; // 输出文件名称和路径
        String tarPath = targetDir + fileName;
        if (minioUtil.doesObjectExist(secMinioProperties.getBucketName(), tarPath)) {
            return tarPath;
        }
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

        try {
            // 文件上传
            minioUtil.upload(secMinioProperties.getBucketName(), tarPath, tarPath);
            // 查看文件是否上传成功，如果不成功再传一次，如果二次上传仍不成功则记录失败日志
            boolean isFileExists = minioUtil.doesObjectExist(secMinioProperties.getBucketName(), tarPath);
            if (isFileExists) {
                // 更新数据库数据
                secAlarmEventMapper.updatePath(startTime, tarPath, "day");
            } else {
                // 二次上传
                minioUtil.upload(secMinioProperties.getBucketName(), tarPath, tarPath);
                isFileExists = minioUtil.doesObjectExist(secMinioProperties.getBucketName(), tarPath);
                if (isFileExists) {
                    // 更新数据库数据
                    secAlarmEventMapper.updatePath(startTime, tarPath, "day");
                } else {
                    logger.error(String.format(Locale.ROOT, "-----Failed to upload file %s", tarPath));
                }
            }

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
        String targetDir = secFileServerProperties.getProfile() + FileUtil.getAdayFilePath(0) + Constant.REPORT + Constant.FILE_SEPARATOR;
        FileUtil.mkdirs(targetDir); // 如果文件夹不存在则创建文件夹
        String fileName = "空间环境周报" + DateUtil.parseDate(LocalDateTime.now(), "yyyyMMdd") + ".docx"; // 输出文件名称和路径
        String tarPath = targetDir + fileName;
        if (minioUtil.doesObjectExist(secMinioProperties.getBucketName(), tarPath)) {
            return tarPath;
        }
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

        //图片、表格(查询最近一周周五~周四的数据)
        List<String> lastFriday2Thursday = DateUtil.getLastFriday2Thursday(null, DateConstant.DATE_PATTERN);
        String begin = lastFriday2Thursday.get(0);
        String end = lastFriday2Thursday.get(1);
        createWeekPng(weekDetailBean, begin + " 00:00:00", end + " 23:59:59", targetDir);

        try {
            begin += " 00:00:00";
            end += " 23:59:59";
            setTableData(weekDetailBean, begin, end);
            weekDetailBean.setUnit("中国星网");
            weekDetailBean.setDateCapital(DateUtil.getToDayCapital());
            InputStream model = this.getClass().getResourceAsStream("/word/weekDetail.docx");
            WeekDetailUtil.createDailyDetailDocx(model, tarPath, weekDetailBean);

            // 文件上传
            minioUtil.upload(secMinioProperties.getBucketName(), tarPath, tarPath);
            // 查看文件是否上传成功，如果不成功再传一次，如果二次上传仍不成功则记录失败日志
            boolean isFileExists = minioUtil.doesObjectExist(secMinioProperties.getBucketName(), tarPath);
            if (isFileExists) {
                // 更新数据库数据
                secAlarmEventMapper.updatePath(pointDay, tarPath.replace(secFileServerProperties.getProfile(), ""), "week");
            } else {
                // 二次上传
                minioUtil.upload(secMinioProperties.getBucketName(), tarPath, tarPath);
                isFileExists = minioUtil.doesObjectExist(secMinioProperties.getBucketName(), tarPath);
                if (isFileExists) {
                    // 更新数据库数据
                    secAlarmEventMapper.updatePath(pointDay, tarPath.replace(secFileServerProperties.getProfile(), ""), "week");
                } else {
                    logger.error(String.format(Locale.ROOT, "-------Failed to upload file %s", tarPath));
                }
            }
            FileUtils.forceDelete(FileUtils.getFile(targetDir.concat("week1.png"))); // 生成文件后删除图片
        } catch (Exception e) {
            logger.error(String.format(Locale.ROOT, "-----method makeShortDayReport----Insert message data exception  %s", e.getMessage()));
        } finally {
            return tarPath;
        }
    }

    private void createWeekPng(WeekDetailBean weekDetailBean, String startTime, String endTime, String targetDir) {
        try {
            String python = secFileServerProperties.getProfile() + "algorithm/weekpng/analyse_t.py";
            String pythonini = secFileServerProperties.getProfile() + "algorithm/weekpng/xw.ini";
            StringBuilder cmd = new StringBuilder("python ");
            cmd.append(python).append(" \"")
                    .append(startTime).append("\" \"")
                    .append(endTime).append("\" ")
                    .append(pythonini).append(" ")
                    .append(targetDir);
            Process process = Runtime.getRuntime().exec(ProcessUtil.getCommand(cmd.toString()));
            int isFinished = process.waitFor(); // 阻塞线程，等待程序执行结束
            if (isFinished == 0) {
                logger.info(String.format(Locale.ROOT, "-----Weekly report image is successfully generated."));
            } else {
                logger.info(String.format(Locale.ROOT, "-----Failed to generate weekly report image."));
            }

            String picPath = targetDir + "week1.png";
            File pic = FileUtils.getFile(picPath);
            if (pic.exists()) {
                weekDetailBean.setPicTitleA("图1  MMM卫星环境");
                weekDetailBean.setPicA(new PictureRenderData(Constant.PIC_WIDTH, Constant.PIC_HIGH, picPath));
            }
        } catch (IOException e) {
            logger.error(String.format(Locale.ROOT, "---------Template file not found  %s", e.getMessage()));
        } catch (InterruptedException e) {
            logger.info(String.format(Locale.ROOT, "-----Failed to generate weekly report picture and throw InterruptedException. %s", e.getMessage()));
        }
    }

    @Override
    public String makeMonthReport() {
        String targetDir = secFileServerProperties.getProfile() + FileUtil.getAdayFilePath(0) + Constant.REPORT + Constant.FILE_SEPARATOR;
        FileUtil.mkdirs(targetDir);
        String fileName = "空间环境月报" + DateUtil.parseDate(LocalDateTime.now(), "yyyyMMdd") + ".docx"; // 输出文件名称和路径
        String tarPath = targetDir + fileName;
        if (minioUtil.doesObjectExist(secMinioProperties.getBucketName(), tarPath)) {
            return tarPath;
        }
        MonthBean monthBean = new MonthBean();
        monthBean.setYear(DateUtil.getToDay().substring(0, 4));
        monthBean.setWhichIssue(DateUtil.getWeekPeriodical());
        monthBean.setSubTitle(Constant.UNIT); // 单位
        monthBean.setDateZH(DateUtil.getToDayZH()); // 日期
        try {
            LocalDate today = LocalDate.now();
            int dayOfMonth = today.getDayOfMonth();
            LocalDate firstDay = LocalDate.now().minusDays(dayOfMonth);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String startTime = firstDay.format(formatter) + " 00:00:00";
            String endTime = today.format(formatter) + " 23:59:59";
            // 综述
            List<SecOverviewVO> monthOverview = secOverviewMapper.getPeriodOverview("SEC_MONTH_OVERVIEW", startTime, endTime);
            if (CollectionUtils.isNotEmpty(monthOverview)) {
                SecOverviewVO secOverviewVO = monthOverview.get(0);
                setMonthOverview(monthBean, startTime, endTime, secOverviewVO);

                List<String> lastMonthDay = DateUtil.getLastMonthDay(null, null);
                String begin = lastMonthDay.get(0) + " 00:00:00";
                String end = lastMonthDay.get(1) + " 23:59:59";
                // 过去一个月的警报数据汇总
                combinTable1Data(monthBean, begin, end);
                // 过去一个月的太阳地磁数据汇总
                setTableData(monthBean, begin, end);
                monthBean.setPic1(new PictureRenderData(Constant.PIC_WIDTH, Constant.PIC_HIGH, secFileServerProperties.getProfile() + "report/f107andssn.png"));
                monthBean.setPicTitle1("图1 太阳F10.7和黑子数");
                monthBean.setPic2(new PictureRenderData(Constant.PIC_WIDTH, Constant.PIC_HIGH, secFileServerProperties.getProfile() + "report/LongerandShorter.png"));
                monthBean.setPicTitle2("图2 太阳X射线流量");
                monthBean.setPic3(new PictureRenderData(Constant.PIC_WIDTH, Constant.PIC_HIGH, secFileServerProperties.getProfile() + "report/proton.png"));
                monthBean.setPicTitle3("图3 高能质子通量");
                monthBean.setPic4(new PictureRenderData(Constant.PIC_WIDTH, Constant.PIC_HIGH, secFileServerProperties.getProfile() + "report/ele.png"));
                monthBean.setPicTitle4("图4 高能电子通量");
                monthBean.setPic5(new PictureRenderData(Constant.PIC_WIDTH, Constant.PIC_HIGH, secFileServerProperties.getProfile() + "report/ap11.png"));
                monthBean.setPicTitle5("图5 未来一个月AP指数");
                monthBean.setPic6(new PictureRenderData(Constant.PIC_WIDTH, Constant.PIC_HIGH, secFileServerProperties.getProfile() + "report/kp11.png"));
                monthBean.setPicTitle6("图6 未来一个月KP指数");

                monthBean.setUnit("中国星网");
                monthBean.setDateCapital(DateUtil.getToDayCapital());
                InputStream model = this.getClass().getResourceAsStream("/word/monthDetail.docx");
                MonthUtil.createDailyMonthDocx(model, tarPath, monthBean);
                // 更新数据库数据
                secAlarmEventMapper.updatePath(secOverviewVO.getTime(), tarPath.replace(secFileServerProperties.getProfile(), ""), "month");
            }
        } catch (Exception e) {
            logger.error(String.format(Locale.ROOT, "--------The generated monthly report table data is abnormal %s", e.getMessage()));
        } finally {
            return tarPath;
        }
    }

    /**
     * 月报综述
     *
     * @param monthBean     月报对象
     * @param startTime     开始时间
     * @param endTime       结束时间
     * @param secOverviewVO 综述对象
     */
    private void setMonthOverview(MonthBean monthBean, String startTime, String endTime, SecOverviewVO secOverviewVO) {
        monthBean.setPastTitle(startTime.substring(0, 4) + "年" + startTime.substring(5, 7) + "月空间环境综述");
        String pastReview = secOverviewVO.getPastReview();
        pastReview = StringUtils.isEmpty(pastReview) ? "" : pastReview.replaceAll("</br>", "\n    ");
        monthBean.setPastOverview(pastReview);
        monthBean.setFutureTitle(endTime.substring(0, 4) + "年" + endTime.substring(5, 7) + "月空间环境预报综述");
        String futureReview = secOverviewVO.getFutureReview();
        futureReview = StringUtils.isEmpty(futureReview) ? "" : futureReview.replaceAll("</br>", "\n    ");
        monthBean.setFutureOverview(futureReview);
    }

    private void setTableData(WeekDetailBean weekDetailBean, String startTime, String endTime) throws Exception {
        List<Map<String, Object>> data = secReportMapper.getCombinData(startTime, endTime);
        if (CollectionUtils.isNotEmpty(data)) {
            data = data.size() > 7 ? data.subList(0, 7) : data;
            String[][] table = new String[9][];
            String[] row0 = new String[]{"日期", "M级以上X射线耀斑", "", "", "", "F10.7射电流量", "黑子数", "是否发生质子事件", "高能电子通量(Electrons/cm2-day-sr)", "地磁Ap指数(Kp)"};
            String[] row1 = new String[]{"", "开始", "最大", "结束", "级别", "", "", "", "", ""};
            table[0] = row0;
            table[1] = row1;
            Object time;
            Object ap;
            Object f107;
            Object ssn;
            Object e2;
            Object kp;
            Object proton;
            String strAp;
            String strF107;
            String strSsn;
            String strE2;
            String strKp;
            String strProton;

            for (int i = 0; i < data.size(); i++) {
                Map<String, Object> strObjMap = data.get(i);
                time = strObjMap.get("TIME");
                f107 = strObjMap.get("F107");
                strF107 = f107 == null ? "" : f107.toString();
                ssn = strObjMap.get("SSN");
                strSsn = ssn == null ? "" : ssn.toString();
                e2 = strObjMap.get("E2");
                strE2 = e2 == null ? "" : e2.toString();
                proton = strObjMap.get("PROTON");
                strProton = proton == null || Integer.parseInt(proton.toString()) < 1 ? "否" : "是";
                kp = strObjMap.get("KP");
                strKp = kp == null ? "" : kp.toString();
                ap = strObjMap.get("AP");
                strAp = ap == null ? "" : ap.toString();
                strAp = strKp == null || strKp.length() == 0 ? strAp : strAp + "(" + strKp + ")";
                table[i + 2] = new String[]{time.toString(), "", "", "", "", strF107, strSsn, strProton, strE2, strAp};
            }
            weekDetailBean.setTableTitle("表1 太阳地磁活动数据表");
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
    }

    /**
     * 组合表一内容
     *
     * @param mtb       月报对象
     * @param startTime 开始时间
     * @param endTime   结束时间
     */
    private void combinTable1Data(MonthBean mtb, String startTime, String endTime) {
        List<String[]> table1 = new ArrayList<>();
        String[] head = new String[]{"序号", "事件", "级别", "时间", "备注"};
        table1.add(head);
        int rowNum = 1; // 第几行
        String eventName;
        // 获取表1数据
        try {
            eventName = "太阳X射线耀斑";
            List<SecProtonAlarmDO> tb1List = secAlarmEventMapper.getAlarmEventCount("SEC_XRAY_ALARM", startTime, endTime);
            if (CollectionUtils.isNotEmpty(tb1List)) {
                for (SecProtonAlarmDO wb : tb1List) {
                    String[] row = new String[]{String.valueOf(rowNum), eventName, Constant.WARN_LEVEL.get(wb.getLevel()), wb.getContent(), ""};
                    table1.add(row);
                    rowNum++;
                }
            }
            eventName = "太阳质子事件";
            List<SecProtonAlarmDO> tb2List = secAlarmEventMapper.getAlarmEventCount("SEC_PROTON_ALARM", startTime, endTime);
            if (CollectionUtils.isNotEmpty(tb2List)) {
                for (SecProtonAlarmDO wb : tb2List) {
                    String[] row = new String[]{String.valueOf(rowNum), eventName, Constant.WARN_LEVEL.get(wb.getLevel()), wb.getContent(), ""};
                    table1.add(row);
                    rowNum++;
                }
            }
            eventName = "高能电子暴";
            List<SecProtonAlarmDO> tb3List = secAlarmEventMapper.getAlarmEventCount("SEC_ELE_ALARM", startTime, endTime);
            if (CollectionUtils.isNotEmpty(tb3List)) {
                for (SecProtonAlarmDO wb : tb3List) {
                    String[] row = new String[]{String.valueOf(rowNum), eventName, Constant.WARN_LEVEL.get(wb.getLevel()), wb.getContent(), ""};
                    table1.add(row);
                    rowNum++;
                }
            }
            eventName = "地磁暴";
            List<SecProtonAlarmDO> tb4List = secAlarmEventMapper.getAlarmEventCount("SEC_DST_ALARM", startTime, endTime);
            if (CollectionUtils.isNotEmpty(tb4List)) {
                for (SecProtonAlarmDO wb : tb4List) {
                    String[] row = new String[]{String.valueOf(rowNum), eventName, Constant.WARN_LEVEL.get(wb.getLevel()), wb.getContent(), ""};
                    table1.add(row);
                    rowNum++;
                }
            }
            if (CollectionUtils.isNotEmpty(tb1List) && CollectionUtils.isNotEmpty(tb2List) && CollectionUtils.isNotEmpty(tb3List) && CollectionUtils.isNotEmpty(tb4List)) {
                List<RowRenderData> render = MonthUtil.createTableData(table1.toArray(new String[table1.size()][]));
                TableRenderData tb = Tables.ofWidth(17).width(17, new double[]{2, 4, 3, 4, 4}).center().create();
                tb.setRows(render);
                mtb.setTableTitle1("表1 " + DateUtil.getLastMonthDay(null, "yyyy年MM月").get(0) + "太空环境事件汇总表");
                mtb.setTable1(tb);
            }
        } catch (Exception e) {
            logger.error(String.format(Locale.ROOT, "-----Getting alert data throws exception  %s", e.getMessage()));
        }
    }

    private void setTableData(MonthBean mtb, String startTime, String endTime) throws Exception {
        List<Map<String, Object>> data = secReportMapper.getCombinData(startTime, endTime);
        String[][] table = new String[data.size() + 1][];
        table[0] = new String[]{"日期", "F10.7射电流量", "太阳黑子数", "X射线耀斑", "电子通量", "AP", "kp"}; // 表头
        Object time;
        Object ap;
        Object f107;
        Object ssn;
        Object longer;
        Object e2;
        Object kp;
        String strAp;
        String strF107;
        String strSsn;
        String strLonger;
        String strE2;
        String strKp;

        for (int i = 0; i < data.size(); i++) {
            Map<String, Object> strObjMap = data.get(i);
            time = strObjMap.get("TIME");
            ap = strObjMap.get("AP");
            strAp = ap == null ? "" : ap.toString();
            f107 = strObjMap.get("F107");
            strF107 = f107 == null ? "" : f107.toString();
            ssn = strObjMap.get("SSN");
            strSsn = ssn == null ? "" : ssn.toString();
            longer = strObjMap.get("LONGER");
            strLonger = longer == null ? "" : longer.toString();
            e2 = strObjMap.get("E2");
            strE2 = e2 == null ? "" : e2.toString();
            kp = strObjMap.get("KP");
            strKp = kp == null ? "" : kp.toString();
            table[i + 1] = new String[]{time.toString(), strF107, strSsn, strLonger, strE2, strAp, strKp};
        }
        TableRenderData tb = Tables.ofWidth(17).width(17, new double[]{3, 2, 2, 2, 3, 2, 3}).center().create();
        List<RowRenderData> render = MonthUtil.createTableData(table);
        tb.setRows(render);
        mtb.setTableTitle2("表2 " + DateUtil.getLastMonthDay(null, "yyyy年MM月").get(0) + "太阳地磁观测数据");
        mtb.setTable2(tb);
    }

    private void setOverviewData(WeekDetailBean weekDetailBean, String startTime, String endTime) {
        try {
            List<SecOverviewVO> weekOverview = secOverviewMapper.getPeriodOverview("SEC_WEEK_OVERVIEW", startTime, endTime);
            if (CollectionUtils.isNotEmpty(weekOverview)) {
                SecOverviewVO secOverviewVO = weekOverview.get(0);
                weekDetailBean.setPastOverView(secOverviewVO.getPastReview());
                weekDetailBean.setFutureOverView(secOverviewVO.getFutureReview());
            }
        } catch (Exception e) {
            logger.error(String.format(Locale.ROOT, "---Getting a weekly review throws an exception %s", e.getMessage()));
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
