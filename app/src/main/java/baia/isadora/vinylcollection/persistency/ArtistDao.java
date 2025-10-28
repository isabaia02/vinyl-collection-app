package baia.isadora.vinylcollection.persistency;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import baia.isadora.vinylcollection.model.Artist;
import baia.isadora.vinylcollection.model.Disc;

@Dao
public interface ArtistDao {
    @Insert
    long insert(Artist artist);
    @Delete
    int delete(Artist artist);
    @Update
    int update(Artist artist);
    @Query("SELECT * FROM Artist WHERE id=:id")
    Artist queryForId(long id);
    @Query("SELECT * FROM Artist ORDER BY name ASC")
    List<Artist> queryAllAscending();
    @Query("SELECT * FROM Artist ORDER BY name DESC")
    List<Artist> queryAllDownward();

}
