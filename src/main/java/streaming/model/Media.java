package streaming.model;

import java.time.LocalDate;

public abstract class Media {
    private final String id;
    private final String title;
    private final String creator;
    private final int durationSeconds;
    private final Genre genre;
    private final LocalDate releaseDate;

    protected Media(String id, String title, String creator, int durationSeconds, Genre genre, LocalDate releaseDate) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id cannot be empty");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title cannot be empty");
        }
        if (durationSeconds <= 0) {
            throw new IllegalArgumentException("duration must be positive");
        }
        this.id = id;
        this.title = title;
        this.creator = creator;
        this.durationSeconds = durationSeconds;
        this.genre = genre;
        this.releaseDate = releaseDate;
    }

    public abstract double recommendationScore(User user);

    public abstract String getType();

    protected double genreBonus(User user) {
        if (user.getFavoriteGenres().contains(genre)) {
            return 30;
        }
        return 0;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCreator() {
        return creator;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public Genre getGenre() {
        return genre;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    @Override
    public String toString() {
        return "[" + getType() + "] " + title + " - " + creator;
    }
}
