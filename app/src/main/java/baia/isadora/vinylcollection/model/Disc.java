package baia.isadora.vinylcollection.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Comparator;
import java.util.Objects;

@Entity
public class Disc implements Cloneable {
    public static Comparator<Disc> growingOrder = new Comparator<Disc>() {
        @Override
        public int compare(Disc disc1, Disc disc2) {
            return disc1.getName().compareToIgnoreCase(disc2.getName());
        }
    };
    public static Comparator<Disc> decreasingOrder = new Comparator<Disc>() {
        @Override
        public int compare(Disc disc1, Disc disc2) {
            return -1 * disc1.getName().compareToIgnoreCase(disc2.getName());
        }
    };
    @PrimaryKey(autoGenerate = true)
    private long id;
    @NonNull
    @ColumnInfo(index = true)
    private String name;
    private String artist;
    private int releaseYear;
    private String genre;
    private boolean alreadyHave;
    private int condition;
    private DiscSpeed discSpeed;

    public Disc(String name, String artist, int releaseYear, String genre, boolean alreadyHave, int condition, DiscSpeed discSpeed) {
        this.name = name;
        this.artist = artist;
        this.releaseYear = releaseYear;
        this.genre = genre;
        this.alreadyHave = alreadyHave;
        this.condition = condition;
        this.discSpeed = discSpeed;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public boolean isAlreadyHave() {
        return alreadyHave;
    }

    public void setAlreadyHave(boolean alreadyHave) {
        this.alreadyHave = alreadyHave;
    }

    public int getCondition() {
        return condition;
    }

    public void setCondition(int condition) {
        this.condition = condition;
    }

    public DiscSpeed getDiscSpeed() {
        return discSpeed;
    }

    public void setDiscSpeed(DiscSpeed discSpeed) {
        this.discSpeed = discSpeed;
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Disc disc = (Disc) o;
        return releaseYear == disc.releaseYear && alreadyHave == disc.alreadyHave && condition == disc.condition && Objects.equals(name, disc.name) && Objects.equals(artist, disc.artist) && Objects.equals(genre, disc.genre) && discSpeed == disc.discSpeed;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, artist, releaseYear, genre, alreadyHave, condition, discSpeed);
    }

    @Override
    public String toString(){
        return name + "\n" +
                artist + "\n" +
                releaseYear + "\n" +
                genre + "\n" +
                alreadyHave + "\n" +
                condition + "\n" +
                discSpeed;
    }
}
