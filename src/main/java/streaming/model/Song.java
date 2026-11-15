package streaming.model;

import java.time.LocalDate;

public class Song extends Media {

    private final String album;

    public Song(String id, String title, String creator, int durationSeconds, Genre genre, LocalDate releaseDate, String album) {
        super(id, title, creator, durationSeconds, genre, releaseDate);
        this.album = album;
    }

    public String getAlbum() {
        return album;
    }

    @Override
    public double recommendationScore(User user) {
        // a song does not get old, so the score is just a base + the genre bonus
        return 50 + genreBonus(user);
    }

    @Override
    public String getType() {
        return "SONG";
    }
}
