package baia.isadora.vinylcollection.persistency;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class MigrateDbVersion_3 extends Migration {
    public MigrateDbVersion_3(){
        super(3,4);
    }
    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `Artist` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `nationality` TEXT");

    }
}
