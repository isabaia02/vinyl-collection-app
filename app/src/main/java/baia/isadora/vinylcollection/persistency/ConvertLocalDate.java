package baia.isadora.vinylcollection.persistency;

import androidx.room.TypeConverter;

import java.time.LocalDate;

public class ConvertLocalDate {
    @TypeConverter
    public static Long fromLocalDateToLong(LocalDate date){
        if(date == null){
            return null;
        }
        return date.toEpochDay();
    }
    @TypeConverter
    public static LocalDate fromLongToLocalDate(Long epochDay){
        if(epochDay == null){
            return null;
        }
        return LocalDate.ofEpochDay(epochDay);
    }
}
