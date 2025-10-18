package baia.isadora.vinylcollection.persistency;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class MigrateDbVersion_1 extends Migration {
    public MigrateDbVersion_1(){
        super(1,2);
    }
    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `Disc_temporary` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `artist` TEXT, `releaseYear` INTEGER NOT NULL, `genre` TEXT, `alreadyHave` INTEGER NOT NULL, `condition` INTEGER NOT NULL, `discSpeed` INTEGER)");
        database.execSQL("INSERT INTO Disc_temporary (id, name, artist, releaseYear, genre, alreadyHave, condition, discSpeed) " +
                            "SELECT id, name, artist, releaseYear, genre, alreadyHave, condition, CASE WHEN discSpeed = 'RPM_33' THEN 0 WHEN discSpeed = 'RPM_45' THEN 1 ELSE -1 END FROM Disc");
        database.execSQL("DROP TABLE Disc");
        database.execSQL("ALTER TABLE Disc_temporary RENAME TO Disc");
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_Disc_name` ON `Disc` (`name`)");
    }
}
