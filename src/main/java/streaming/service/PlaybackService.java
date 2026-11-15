package streaming.service;

import streaming.exception.SkipLimitReachedException;
import streaming.model.Media;
import streaming.model.Resumable;
import streaming.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaybackService {

    // for each user name, the times of the skips
    private final Map<String, List<LocalDateTime>> skips = new HashMap<>();

    public String play(Media media) {
        if (media instanceof Resumable) {
            Resumable resumable = (Resumable) media;
            if (resumable.getResumePosition() > 0) {
                return "Resuming " + media.getTitle() + " at " + formatTime(resumable.getResumePosition());
            }
        }
        return "Playing " + media.getTitle() + " (" + formatTime(media.getDurationSeconds()) + ")";
    }

    // when the user stops, we keep the position only if the media can be resumed
    public void stop(Media media, int secondsListened) {
        if (media instanceof Resumable) {
            ((Resumable) media).savePosition(secondsListened);
        }
    }

    public void skip(User user, LocalDateTime now) throws SkipLimitReachedException {
        List<LocalDateTime> userSkips = skips.computeIfAbsent(user.getName(), k -> new ArrayList<>());

        // only the skips of the last hour count
        userSkips.removeIf(time -> time.isBefore(now.minusHours(1)));

        int max = user.getPlan().maxSkipsPerHour();
        if (userSkips.size() >= max) {
            throw new SkipLimitReachedException(user.getName() + " already skipped " + max + " times this hour");
        }
        userSkips.add(now);
    }

    public static String formatTime(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;
        if (hours > 0) {
            return String.format("%dh%02dm%02ds", hours, minutes, secs);
        }
        return String.format("%dm%02ds", minutes, secs);
    }
}
