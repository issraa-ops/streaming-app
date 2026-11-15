package streaming.model;

import java.time.LocalDate;

public class Audiobook extends Media implements Resumable {
    private final int chapterCount;
    private int position;

    public Audiobook(String id, String title, String creator, int durationSeconds, Genre genre, LocalDate releaseDate, int chapterCount) {
        super(id, title, creator, durationSeconds, genre, releaseDate);
        this.chapterCount = chapterCount;
        this.position = 0;
    }

    public int getChapterCount() {
        return chapterCount;
    }

    public boolean isStarted() {
        return position > 0;
    }

    public boolean isFinished() {
        return position >= getDurationSeconds();
    }

    @Override
    public double recommendationScore(User user) {
        double score = 40 + genreBonus(user);
        if (isStarted() && !isFinished()) {
            score += 40;
        }
        return score;
    }

    @Override
    public String getType() {
        return "AUDIOBOOK";
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
