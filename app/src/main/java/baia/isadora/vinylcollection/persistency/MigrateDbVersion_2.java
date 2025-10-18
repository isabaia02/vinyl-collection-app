package baia.isadora.vinylcollection.persistency;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class MigrateDbVersion_2 extends Migration {
    public MigrateDbVersion_2(){
        super(2,3);
    }
    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        database.execSQL("ALTER TABLE Disc ADD COLUMN acquiredDate INTEGER");
    }
}
