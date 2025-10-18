package baia.isadora.vinylcollection.persistency;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import baia.isadora.vinylcollection.model.Disc;

@Database(entities = {Disc.class}, version = 1)
@TypeConverters({ConvertDiscSpeed.class})
public abstract class DiscsDatabase extends RoomDatabase {
    public abstract DiscDao getDiscDao();
    private static DiscsDatabase INSTANCE;
    public static DiscsDatabase getInstance(final Context context){
        if(INSTANCE == null){
            synchronized (DiscsDatabase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context, DiscsDatabase.class, "discs.db").allowMainThreadQueries().build();
                }
            }
        }
        return INSTANCE;
    }
}
