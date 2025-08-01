package com.cw.framework.utils;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

/**
 * @author thisdcw
 * @date 2025年03月25日 9:53
 */
public class DateTimeUtils {


    public static Integer getSeconds(Date date) {
        return Math.toIntExact(date.toInstant().atZone(ZoneId.of("GMT+8")).toEpochSecond());
    }

    public static String getYmd(Integer seconds) {
        return LocalDateTime.ofEpochSecond(seconds, 0, ZoneOffset.ofHours(8)).toLocalDate().toString();
    }

    public static Integer getYm(String month) {
        return Integer.parseInt(month.split("-")[0] + month.split("-")[1]);
    }

    public static String getTimeStamp() {
        return Long.toString(Instant.now().getEpochSecond());
    }

    public static int getWaterYearMonth() {
        return LocalDate.now().getYear() * 100 + LocalDate.now().getMonthValue();
    }

    /**
     * 获取今天的起始时间（00:00:00）的秒级时间戳
     *
     * @return 秒级时间戳
     */
    public static long getTodayStartTimestamp() {
        return LocalDateTime.now()
                .toLocalDate()
                .atStartOfDay(ZoneId.systemDefault())
                .toEpochSecond();
    }

    /**
     * 获取昨天的起始时间（00:00:00）的秒级时间戳
     *
     * @return 秒级时间戳
     */
    public static long getYesterdayStartTimestamp() {
        return LocalDate.now()
                .minusDays(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toEpochSecond();
    }

    public static int now() {
        return (int) LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(8));
    }


    public static long getSeconds(int year, int month, int day, int hour, int minute, int second) {
        LocalDateTime dateTime = LocalDateTime.of(year, month, day, hour, minute, second);
        return dateTime.toEpochSecond(ZoneOffset.UTC);
    }

    /**
     * 判断传入的时间戳是否是当天
     *
     * @param timestamp 要判断的时间戳（秒级）
     * @return 是否是当天
     */
    public static boolean isToday(long timestamp) {
        // 获取当前日期的零点时间戳（秒级）
        long todayStart = getStartOfDayTimestamp();

        // 获取当前日期的结束时间戳（秒级）
        long todayEnd = todayStart + 24 * 60 * 60 - 1;

        // 判断传入的时间戳是否在今天的时间范围内
        return timestamp >= todayStart && timestamp <= todayEnd;
    }

    /**
     * 获取当前日期的零点时间戳（秒级）
     *
     * @return 当前日期零点的时间戳（秒级）
     */
    private static long getStartOfDayTimestamp() {
        Calendar calendar = Calendar.getInstance();
        // 设置为0点
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        // 返回秒级时间戳
        return calendar.getTimeInMillis() / 1000;
    }

    /**
     * 给定秒级时间戳,获取距现在的时长
     *
     * @param timestamp
     * @return
     */
    public static String getTimeDifference(long timestamp) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime targetTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());

        long years = ChronoUnit.YEARS.between(targetTime, now);
        targetTime = targetTime.plusYears(years);

        long months = ChronoUnit.MONTHS.between(targetTime, now);
        targetTime = targetTime.plusMonths(months);

        long days = ChronoUnit.DAYS.between(targetTime, now);
        targetTime = targetTime.plusDays(days);

        long hours = ChronoUnit.HOURS.between(targetTime, now);
        targetTime = targetTime.plusHours(hours);

        long minutes = ChronoUnit.MINUTES.between(targetTime, now);
        targetTime = targetTime.plusMinutes(minutes);

        long seconds = ChronoUnit.SECONDS.between(targetTime, now);

        StringBuilder result = new StringBuilder();
        if (years > 0) {
            result.append(years).append("年 ");
        }
        if (months > 0) {
            result.append(months).append("个月 ");
        }
        ;
        if (days > 0) {
            result.append(days).append("天 ");
        }
        ;
        if (hours > 0) {
            result.append(hours).append("小时 ");
        }
        ;
        if (minutes > 0) {
            result.append(minutes).append("分钟 ");
        }
        ;
        // 秒必定显示
        result.append(seconds).append("秒");

        return result.toString().trim();
    }

    // 获取某个日期的 UNIX 时间戳（秒级）
    private static Integer getUnixTimestamp(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        // 设置为 0 点
        calendar.set(year, month - 1, day, 0, 0, 0);
        // 转为秒级时间戳
        return (int) (calendar.getTimeInMillis() / 1000);
    }
}
