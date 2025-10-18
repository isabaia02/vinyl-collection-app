package baia.isadora.vinylcollection.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.Locale;

public final class UtilsLocalDate {
    private UtilsLocalDate(){}
    public static String formatLocalDate(LocalDate date){
        if(date == null){
            return null;
        }
        String formatPattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(FormatStyle.SHORT, null, IsoChronology.INSTANCE, Locale.getDefault());
        formatPattern = formatPattern.replaceAll("\\byy\\b", "yyyy");
        formatPattern = formatPattern.replaceAll("\\bM\\b", "MM");
        formatPattern = formatPattern.replaceAll("\\bd\\b", "dd");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatPattern, Locale.getDefault());

        return date.format(formatter);
    }
    public static long toMiliseconds(LocalDate date){
        if(date == null){
            return 0;
        }
        return date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
