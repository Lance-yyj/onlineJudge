package com.edu.cqie.oj.util;


import org.apache.commons.lang3.time.DateFormatUtils;

import java.lang.management.ManagementFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class DateUtils extends org.apache.commons.lang3.time.DateUtils{
    public static String YYYY = "yyyy";

    public static String YYYY_MM = "yyyy-MM";

    public static String YYYY_MM_DD = "yyyy-MM-dd";

    public static String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    private static String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

    /**
     * 获取当前Date型日期
     *
     * @return Date() 当前日期
     */
    public static Date getNowDate()
    {
        return new Date();
    }

    /**
     * 获取当前日期, 默认格式为yyyy-MM-dd
     *
     * @return String
     */
    public static String getDate()
    {
        return dateTimeNow(YYYY_MM_DD);
    }

    public static final String getTime()
    {
        return dateTimeNow(YYYY_MM_DD_HH_MM_SS);
    }

    public static final String dateTimeNow()
    {
        return dateTimeNow(YYYYMMDDHHMMSS);
    }

    public static final String dateTimeNow(final String format)
    {
        return parseDateToStr(format, new Date());
    }

    public static final String dateTime(final Date date)
    {
        return parseDateToStr(YYYY_MM_DD, date);
    }

    public static final String parseDateToStr(final String format, final Date date)
    {
        return new SimpleDateFormat(format).format(date);
    }

    public static final Date dateTime(final String format, final String ts)
    {
        try
        {
            return new SimpleDateFormat(format).parse(ts);
        }
        catch (ParseException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * 日期路径 即年/月/日 如2018/08/08
     */
    public static final String datePath()
    {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyy/MM/dd");
    }

    /**
     * 日期路径 即年/月/日 如20180808
     */
    public static final String dateTime()
    {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyyMMdd");
    }

    /**
     * 日期型字符串转化为日期 格式
     */
    public static Date parseDate(Object str)
    {
        if (str == null)
        {
            return null;
        }
        try
        {
            return parseDate(str.toString(), parsePatterns);
        }
        catch (ParseException e)
        {
            return null;
        }
    }

    /**
     * 获取服务器启动时间
     */
    public static Date getServerStartDate()
    {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(time);
    }

    /**
     * 计算两个时间差
     */
    public static String getDatePoor(Date endDate, Date nowDate)
    {
        long nd = 1000L * 24 * 60 * 60;
        long nh = 1000L * 60 * 60;
        long nm = 1000L * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟";
    }

    /**
     * 增加 LocalDateTime ==> Date
     */
    public static Date toDate(LocalDateTime temporalAccessor)
    {
        ZonedDateTime zdt = temporalAccessor.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }

    /**
     * 增加 LocalDate ==> Date
     */
    public static Date toDate(LocalDate temporalAccessor)
    {
        LocalDateTime localDateTime = LocalDateTime.of(temporalAccessor, LocalTime.of(0, 0, 0));
        ZonedDateTime zdt = localDateTime.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }

    /**
     * 将时间戳转化为日期
     *
     * @param timeStamp 时间戳
     * @return Date
     * @author xiaowei
     * @date 2025/7/18 14:50
     **/
    public static Date  timeStamp(String timeStamp){
        Instant instant = Instant.ofEpochSecond(Long.parseLong(timeStamp));
        ZonedDateTime localDateTime = instant.atZone(ZoneId.of("Asia/Shanghai")); // 指定时区
        return Date.from(localDateTime.toInstant());
    }

    /**
     * 时间转 TimeZone
     *
     * @param time
     * @return String
     * @author xiaowei
     * @date 2025/10/7 19:01
     **/
    public static String dateToTimeZone(long time) throws Exception {
        return dateToTimeZone(new Date(time));
    }

    /**
     * 时间转 TimeZone
     *
     * @param date riqi1
     * @return String ZonedDateTime为字符串
     * @author xiaowei
     * @date 2025/10/7 19:02
     **/
    public static String dateToTimeZone(Date date) throws Exception {
        if (date == null) {
            throw new Exception("date is not null");
        }
        Instant instant = date.toInstant();

        // 将Instant转换为ZonedDateTime，使用系统默认时区
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());

        // 定义日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

        // 格式化ZonedDateTime为字符串
        String time = zonedDateTime.format(formatter);

        return time;
    }

    /**
     * 固定日期增加固定天数计算
     *
     * @param plusDays 增加的固定天数
     * @return Date 增加固定天数后的日期
     * @author xiaowei
     * @date 2025/10/10 15:58
     **/
    public static Date plusDays(Date date,Integer plusDays) {
        // 将Date转换为LocalDate
        LocalDate originalDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        // 计算新日期
        LocalDate newDate = originalDate.plusDays(plusDays);
        return Date.from(newDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }


    /**
     * 获取当天0点时间
     *
     * @return Date
     * @author xiaowei
     * @date 2025/10/17 9:31
     **/
    public static Date getStartOfDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取当天24点时间
     *
     * @return Date
     * @author xiaowei
     * @date 2025/10/17 9:31
     **/
    public static Date getEndOfDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }
}
