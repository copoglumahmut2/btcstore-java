package util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;

public class StoreDateUtils extends DateUtils {

    public static final String DATE_FORMAT_YYYY_MM_DD = "yyyy-MM-dd";
    public static final String DATE_FORMAT_YYYYMMDD = "yyyyMMdd";
    public static final String DATE_FORMAT_DDMMYYYY = "ddMMyyyy";
    public static final String DATE_FORMAT_DD_MM_YYYY = "dd-MM-yyyy";

    /**
     * @param date
     * @return {@link Date}
     * @apiNote Return max value given date like 23:59:59
     **/

    public static Date maxTime(Date date) {
        date = DateUtils.setHours(date, 23);
        date = DateUtils.setMinutes(date, 59);
        date = DateUtils.setSeconds(date, 59);
        date = DateUtils.setMilliseconds(date, 999);
        return date;
    }

    /**
     * @param date
     * @return {@link Date}
     * @apiNote Return min value given date like 00:00:00
     **/
    public static Date minTime(Date date) {
        date = DateUtils.setHours(date, 0);
        date = DateUtils.setMinutes(date, 0);
        date = DateUtils.setSeconds(date, 0);
        date = DateUtils.setMilliseconds(date, 0);
        return date;
    }

    public static Boolean isBetween(Date startDate, Date endDate, Date dateNow) {

        return startDate.before(dateNow) && endDate.after(dateNow);
    }

    public static String convertDateToString(Date date, String dateFormat) {
        if (Objects.isNull(date)) {
            return StringUtils.EMPTY;
        }
        return DateFormatUtils.format(date, dateFormat);
    }

    /**
     * @param date
     * @return {@link Date}
     * @apiNote Sunucunun saat dilimine göre farklı servislerden gelen date alanlarını doğru tarihe çevirir
     **/
    public static Date convertServerUtcTimeZone(Date date) {

        var zoneId = ZoneId.systemDefault().getRules().getOffset(Instant.now());
        return DateUtils.addSeconds(date, zoneId.getTotalSeconds());
    }

    public static LocalDate convertLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDateTime convertLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static Date convertDate(LocalDate date) {
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static Date convertDate(LocalDateTime date) {
        return Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static long difference(LocalDate startDate, LocalDate endDate, ChronoUnit chronoUnit) {
        var duration = chronoUnit.between(startDate, endDate);
        return Math.abs(duration);

    }

    public static long difference(LocalDateTime startDate,
                                  LocalDateTime endDate,
                                  ChronoUnit chronoUnit) {
        var duration = chronoUnit.between(startDate, endDate);
        return Math.abs(duration);
    }

}
