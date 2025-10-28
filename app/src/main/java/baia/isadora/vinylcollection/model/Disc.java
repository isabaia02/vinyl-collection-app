package baia.isadora.vinylcollection.model;

import static androidx.room.ForeignKey.CASCADE;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Objects;

@Entity(foreignKeys = {@ForeignKey(entity = Artist.class, parentColumns = "id", childColumns = "artistId", onDelete = CASCADE)})
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
    @ColumnInfo(index = true)
    private Long artistId;
    private int releaseYear;
    private String genre;
    private boolean alreadyHave;
    private int condition;
    private DiscSpeed discSpeed;
    private LocalDate acquiredDate;

    public Disc(String name, Long artistId, int releaseYear, String genre, boolean alreadyHave, int condition, DiscSpeed discSpeed, LocalDate acquiredDate) {
        this.name = name;
        this.artistId = artistId;
        this.releaseYear = releaseYear;
        this.genre = genre;
        this.alreadyHave = alreadyHave;
        this.condition = condition;
        this.discSpeed = discSpeed;
        this.acquiredDate = acquiredDate;
    }

    public LocalDate getAcquiredDate() {
        return acquiredDate;
    }

    public void setAcquiredDate(LocalDate acquiredDate) {
        this.acquiredDate = acquiredDate;
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

    public Long getArtistId() {
        return artistId;
    }

    public void setArtistId(Long artistId) {
        this.artistId = artistId;
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
        if(acquiredDate == null && disc.acquiredDate != null){
            return false;
        }
        if(acquiredDate != null && !acquiredDate.equals(disc.acquiredDate)){
            return false;
        }
        return releaseYear == disc.releaseYear && alreadyHave == disc.alreadyHave && condition == disc.condition && Objects.equals(name, disc.name) && Objects.equals(artistId, disc.artistId) && Objects.equals(genre, disc.genre) && discSpeed == disc.discSpeed;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, artistId, releaseYear, genre, alreadyHave, condition, discSpeed, acquiredDate);
    }

    @Override
    public String toString(){
        return name + "\n" +
                artistId + "\n" +
                releaseYear + "\n" +
                genre + "\n" +
                alreadyHave + "\n" +
                condition + "\n" +
                discSpeed;
    }
}
