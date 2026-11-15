package streaming.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Podcast extends Media implements Resumable {

    private final int episodeNumber;
    private int position;

    public Podcast(String id, String title, String creator, int durationSeconds, Genre genre, LocalDate releaseDate, int episodeNumber) {
        super(id, title, creator, durationSeconds, genre, releaseDate);
        this.episodeNumber = episodeNumber;
        this.position = 0;
    }

    public int getEpisodeNumber() {
        return episodeNumber;
    }

    @Override
    public double recommendationScore(User user) {
        // recent episodes are more interesting : -1 point per week
        long weeks = ChronoUnit.WEEKS.between(getReleaseDate(), LocalDate.now());
        double score = 60 + genreBonus(user) - weeks;
        return Math.max(score, 0);
    }

    @Override
    public String getType() {
        return "PODCAST";
    }

    @Override
    public void savePosition(int seconds) {
        if (seconds < 0 || seconds > getDurationSeconds()) {
            throw new IllegalArgumentException("position out of range");
        }
        this.position = seconds;
    }

    @Override
    public int getResumePosition() {
        return position;
    }
}
