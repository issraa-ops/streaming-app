package streaming.exception;

public class SkipLimitReachedException extends Exception {
    public SkipLimitReachedException(String message) {
        super(message);
    }
}
