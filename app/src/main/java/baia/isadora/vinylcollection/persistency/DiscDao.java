package baia.isadora.vinylcollection.persistency;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import baia.isadora.vinylcollection.model.Disc;

@Dao
public interface DiscDao {
    @Insert
    long insert(Disc disc);

    @Delete
    int delete(Disc disc);

    @Update
    int update(Disc disc);

    @Query("SELECT * FROM disc WHERE id=:id")
    Disc queryForId(long id);

    @Query("SELECT * FROM disc ORDER BY name ASC")
    List<Disc> queryAllAscending();

    @Query("SELECT * FROM disc ORDER BY name DESC")
    List<Disc> queryAllDownward();

    @Query("SELECT count(*) FROM Disc WHERE artistId = :artistId")
    int totalIdArtist(long artistId);

    @Query("SELECT * FROM Disc WHERE artistId = :artistId ORDER BY name ASC")
    List<Disc> queryByArtist(long artistId);
}
