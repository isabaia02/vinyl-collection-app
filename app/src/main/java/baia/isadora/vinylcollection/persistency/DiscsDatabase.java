package baia.isadora.vinylcollection.persistency;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import baia.isadora.vinylcollection.model.Artist;
import baia.isadora.vinylcollection.model.Disc;

@Database(entities = {Disc.class, Artist.class}, version = 1)
@TypeConverters({ConvertDiscSpeed.class, ConvertLocalDate.class})
public abstract class DiscsDatabase extends RoomDatabase {
    public abstract DiscDao getDiscDao();
    public abstract ArtistDao getArtistDao();
    private static DiscsDatabase INSTANCE;
    public static DiscsDatabase getInstance(final Context context){
        if(INSTANCE == null){
            synchronized (DiscsDatabase.class){
                if(INSTANCE == null){
                    Builder builder = Room.databaseBuilder(context, DiscsDatabase.class, "discs.db");
                    builder.allowMainThreadQueries();
//                    builder.addMigrations(new MigrateDbVersion_1());
//                    builder.addMigrations(new MigrateDbVersion_2());
                    builder.fallbackToDestructiveMigration();
                    INSTANCE = (DiscsDatabase) builder.build();
                }
            }
        }
        return INSTANCE;
    }
}
