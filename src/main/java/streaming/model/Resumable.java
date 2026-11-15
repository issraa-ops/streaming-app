package streaming.model;

public interface Resumable {
    void savePosition(int seconds);

    int getResumePosition();
}
