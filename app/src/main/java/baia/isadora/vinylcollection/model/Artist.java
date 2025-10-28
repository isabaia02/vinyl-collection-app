package baia.isadora.vinylcollection.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Comparator;

@Entity
public class Artist {
    public static Comparator<Artist> growingOrder = new Comparator<Artist>() {
        @Override
        public int compare(Artist artist1, Artist artist2) {
            return artist1.getName().compareToIgnoreCase(artist2.getName());
        }
    };
    public static Comparator<Artist> decreasingOrder = new Comparator<Artist>() {
        @Override
        public int compare(Artist artist1, Artist artist2) {
            return -1 * artist1.getName().compareToIgnoreCase(artist2.getName());
        }
    };
    @PrimaryKey(autoGenerate = true)
    private long id;
    @NonNull
    private String name;
    private String nationality;

    public Artist(@NonNull String name, String nationality) {
        this.name = name;
        this.nationality = nationality;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    @Override
    public String toString() {
        return name;
    }
}
