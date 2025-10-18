package baia.isadora.vinylcollection.persistency;

import androidx.room.TypeConverter;

import baia.isadora.vinylcollection.model.DiscSpeed;

public class ConvertDiscSpeed {
    public static DiscSpeed[] discSpeeds = DiscSpeed.values();
    @TypeConverter
    public static int fromEnumToInt(DiscSpeed discSpeed){
        if(discSpeed == null){
            return -1;
        }
        return discSpeed.ordinal();
    }
    @TypeConverter
    public static DiscSpeed fromIntToEnum(int ordinal){
        if(ordinal <0){
            return null;
        }
        return discSpeeds[ordinal];
    }
}
