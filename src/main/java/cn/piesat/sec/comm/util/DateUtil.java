package cn.piesat.sec.comm.util;


import cn.piesat.sec.comm.constant.Constant;
import cn.piesat.sec.comm.constant.DateConstant;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class DateUtil {
    /**
     * 获取今天的日期
     *
     * @return
     */
    @ApiOperation("")
    public static String getToDay() {
        return getDaysLater(0, "yyyy-MM-dd");
    }

    /**
     * 获取n天后的日期
     *
     * @param days 正数为几天后的日期，负数为几天前的日期
     * @return
     */
    @ApiOperation("")
    public static String getDaysLater(int days) {
        return getDaysLater(days, "yyyy-MM-dd");
    }

    /**
     * 获取n天后的日期，指定日期格式
     *
     * @param n 正数为几天后的日期，负数为几天前的日期
     * @return
     */
    @ApiOperation("")
    public static String getDaysLater(int n, String pattern) {
        LocalDate today = LocalDate.now();
        LocalDate befDays = today.plusDays(n);
        return parseDate(befDays, pattern);
    }

    /**
     * 获取今天的大写日期
     *
     * @return
     */
    @ApiOperation("")
    public static String getToDayZH() {
        return getDaysLater(0, "yyyy年MM月dd日");
    }

    /**
     * 解析指定格式时间对象
     *
     * @param localDate 时间对象
     * @param pattern   返回时间格式
     * @return 指定格式时间
     */
    @ApiOperation("")
    public static String parseDate(LocalDate localDate, String pattern) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        return localDate.format(dateTimeFormatter);
    }

    /**
     * 解析指定格式时间对象
     *
     * @param localDateTime 时间对象
     * @param pattern       返回时间格式
     * @return 指定格式时间
     */
    @ApiOperation("")
    public static String parseDate(LocalDateTime localDateTime, String pattern) {
        if (localDateTime == null) {
            return null;
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        return localDateTime.format(dateTimeFormatter);
    }

    /**
     * 获取当天期大写
     *
     * @return
     */
    @ApiOperation("")
    public static String getToDayCapital() {
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthValue();
        int day = today.getDayOfMonth();
        String yearStr = String.valueOf(year);
        StringBuilder years = new StringBuilder();
        for (int i = 0; i < yearStr.length(); i++) {
            int yItem = Integer.parseInt(String.valueOf(yearStr.charAt(i)));
            years.append(DateConstant.DATE_CONST.get(yItem));
        }
        return years + "年"
                + DateConstant.DATE_CONST.get(month) + "月"
                + DateConstant.DATE_CONST.get(day) + "日";
    }

    /**
     * 获取日期刊数
     *
     * @return 第n期
     */
    @ApiOperation("")
    public static int getDailyPeriodical() {
        LocalDate start = LocalDate.parse(Constant.START_PERIOD);
        LocalDate end = LocalDate.now();//当前时间
        return (int) ((end.toEpochDay() - start.toEpochDay()) + 1);
    }

    /**
     * 获取周期刊数
     *
     * @return 第n期
     */
    @ApiOperation("")
    public static int getWeekPeriodical() {
        LocalDate start = LocalDate.parse(Constant.START_PERIOD);
        LocalDate end = LocalDate.now();//当前时间
        return (int) ((end.toEpochDay() - start.toEpochDay()) / 7 + 1);
    }

    /**
     * 获取特定日期的上一周
     *
     * @param specificDate 特定日期，如果为null默认是当天
     * @param pattern      日期字符串
     * @return 上周周一，周日时间
     */
    @ApiOperation("")
    public static List<String> getLastWeekDay(String specificDate, String pattern) {
        List<String> list = new ArrayList<>();
        pattern = StringUtils.isEmpty(pattern) ? "yyyy-MM-dd" : pattern;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        LocalDate date = StringUtils.isEmpty(specificDate) ? LocalDate.now() : LocalDate.parse(specificDate);
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        int value = dayOfWeek.getValue();
        LocalDate startDay = date.minusDays(value + 6);
        LocalDate endDay = date.minusDays(value);
        list.add(startDay.format(dateTimeFormatter));
        list.add(endDay.format(dateTimeFormatter));
        return list;
    }

    /**
     * 获取今天之前的一个周四到上周五
     *
     * @param specificDate 特定日期，如果为null默认是当天
     * @param pattern      日期字符串
     * @return 上周周一，周日时间
     */
    public static List<String> getLastFriday2Thursday(String specificDate, String pattern) {
        List<String> list = new ArrayList<>();
        pattern = StringUtils.isEmpty(pattern) ? "yyyy-MM-dd" : pattern;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        LocalDate date = StringUtils.isEmpty(specificDate) ? LocalDate.now() : LocalDate.parse(specificDate);
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        int value = dayOfWeek.getValue();
        LocalDate startDay = null;
        LocalDate endDay = null;
        if (value >= 5) {
            startDay = date.minusDays(value - 4 + 6);
            endDay = date.minusDays(value - 4);
        } else {
            startDay = date.minusDays(value + 3 + 6);
            endDay = date.minusDays(value + 3);
        }
        list.add(startDay.format(dateTimeFormatter));
        list.add(endDay.format(dateTimeFormatter));
        return list;
    }

    /**
     * 获取最近的一个周几是几号
     *
     * @param nday    返回第几天
     * @param pattern 返回时间字符串格式
     * @return 最近时间日期
     */
    public static String getApointedDay(int nday, String pattern) {
        nday = nday > 7 ? nday % 7 : (nday < 1 ? 1 : nday);
        LocalDate date = LocalDate.now();
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        int noTday = dayOfWeek.getValue();
        pattern = StringUtils.isEmpty(pattern) ? "yyyy-MM-dd" : pattern;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        if (nday == noTday) {
            return date.format(formatter);
        } else if (nday > noTday) {
            return date.minusDays(7 - nday + noTday).format(formatter);
        } else {
            return date.minusDays(noTday - nday).format(formatter);
        }
    }

    /**
     * 获取特定日期的周一、周日
     *
     * @param specificDate 特定日期，如果为null默认是当天
     * @param pattern      日期字符串
     * @return 上周周一，周日时间
     */
    public static List<String> getThisWeekDay(String specificDate, String pattern) {
        List<String> list = new ArrayList<>();
        pattern = StringUtils.isEmpty(pattern) ? "yyyy-MM-dd" : pattern;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        LocalDate date = StringUtils.isEmpty(specificDate) ? LocalDate.now() : LocalDate.parse(specificDate);
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        int value = dayOfWeek.getValue();
        LocalDate startDay = date.minusDays(value - 1);
        LocalDate endDay = date.plusDays(7 - value);
        list.add(startDay.format(dateTimeFormatter));
        list.add(endDay.format(dateTimeFormatter));
        return list;
    }


    /**
     * 获取特定日期的上一月
     *
     * @param specificDate 特定日期，如果为null默认是当天
     * @param pattern      日期字符串
     * @return 上月第一天，上月最后一天
     */
    public static List<String> getLastMonthDay(String specificDate, String pattern) {
        List<String> list = new ArrayList<>();
        pattern = StringUtils.isEmpty(pattern) ? "yyyy-MM-dd" : pattern;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        LocalDate date = StringUtils.isEmpty(specificDate) ? LocalDate.now() : LocalDate.parse(specificDate);
        int dayOfMonth = date.getDayOfMonth();// 这个月的几号
        LocalDate endDay = date.minusDays(dayOfMonth);
        int daysMonth = endDay.lengthOfMonth();// 这个月有多少天（1~31）
        LocalDate startDay = endDay.minusDays(daysMonth - 1);
        list.add(startDay.format(dateTimeFormatter));
        list.add(endDay.format(dateTimeFormatter));
        return list;
    }

    /**
     * 获取指定格式的日期字符串
     *
     * @param specificDate 指定日期
     * @param days         几天的日期数据
     * @param pattern      日期格式
     * @return 日期字符串
     */
    public static List<String> getDateList(String specificDate, int days, String pattern) {
        pattern = StringUtils.isEmpty(pattern) ? "yyyy-MM-dd" : pattern;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDate day = LocalDate.parse(specificDate);
        List<String> dataList = new ArrayList<>();

        for (int i = 0; i < days; i++) {
            LocalDate newDay = day.plusDays(i);
            dataList.add(newDay.format(formatter));
        }
        return dataList;
    }

    /**
     * 时间字符串转对象
     *
     * @param dateStr 时间字符串
     * @param format  时间字符串格式
     * @return 时间对象
     */
    public static LocalDateTime parseLocalDateTime(String dateStr, String format) {
        if (StringUtils.isEmpty(dateStr) || StringUtils.isEmpty(format)) {
            return null;
        }
        if (!dateStr.matches("^\\d+")) {
            dateStr = dateStr.substring(1);  // 首字符可能含有格式特殊字符情况
        }
        if (dateStr.length() < format.length()) {
            int dis = format.length() - dateStr.length();
            for (int i = 0; i < dis; i++) {
                dateStr = dateStr.concat("0");
            }
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.parse(dateStr, dtf);
    }

    /**
     * 获取两个时间字符串
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 小时差
     */
    public static long bwtHours(String startTime, String endTime) {
        LocalDateTime ts = LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime te = LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return ChronoUnit.HOURS.between(ts, te);
    }

    /**
     * 获取两个时间字符串
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 分钟差
     */
    public static long bwtMinutes(String startTime, String endTime) {
        LocalDateTime ts = LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime te = LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return ChronoUnit.MINUTES.between(ts, te);
    }
}
