package streaming.model;

/**
 * For media that can be continued later (podcast, audiobook).
 * A song does not need this, so it does not implement it.
 */
public interface Resumable {

    void savePosition(int seconds);

    int getResumePosition();
}
