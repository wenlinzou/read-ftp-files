package com.mq.ftp.util;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    private static final String YYYYDD = "yyyyMMdd";

    /**
     * 将date格式化为指定格式的字符串时间
     *
     * @param date   java.util.Date
     * @param format 需要格式的样式(yyyy-MM-dd等)
     * @return String
     */
    public static String date2Str(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.CHINESE);
        if (null == date) {
            throw new NullPointerException("the param of data can't be null");
        }
        if (StringUtils.isEmpty(format)) {
            throw new NullPointerException("the param of format can't be null or empty");
        }
        return sdf.format(date);
    }

    public static Date beforeDay() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, -1);
        Date before = c.getTime();
        return before;
    }

    public static String getBeforeDay() {
        return date2Str(beforeDay(), YYYYDD);
    }


}
