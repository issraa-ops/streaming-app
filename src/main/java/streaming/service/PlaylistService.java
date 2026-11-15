package streaming.service;

import streaming.exception.DuplicateMediaException;
import streaming.exception.MediaNotFoundException;
import streaming.exception.PlaylistFullException;
import streaming.exception.PremiumContentException;
import streaming.model.Media;
import streaming.model.Playlist;
import streaming.subscription.SubscriptionPlan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PlaylistService {
    private final Random random;

    public PlaylistService() {
        this(new Random());
    }

    public PlaylistService(Random random) {
        this.random = random;
    }

    public void addToPlaylist(Playlist playlist, Media media)
            throws PlaylistFullException, PremiumContentException, DuplicateMediaException {
        SubscriptionPlan plan = playlist.getOwner().getPlan();

        if (!plan.canAccess(media)) {
            throw new PremiumContentException(media.getTitle() + " needs a premium plan");
        }
        if (playlist.contains(media)) {
            throw new DuplicateMediaException(media.getTitle() + " is already in " + playlist.getName());
        }
        if (playlist.size() >= plan.maxPlaylistSize()) {
            throw new PlaylistFullException(playlist.getName() + " is full (max " + plan.maxPlaylistSize() + ")");
        }
        playlist.add(media);
    }

    public void removeFromPlaylist(Playlist playlist, Media media) throws MediaNotFoundException {
        if (!playlist.remove(media)) {
            throw new MediaNotFoundException(media.getTitle() + " is not in " + playlist.getName());
        }
    }

    public List<Media> shuffle(Playlist playlist) {
        List<Media> remaining = new ArrayList<>(playlist.getItems());
        Collections.shuffle(remaining, random);

        List<Media> result = new ArrayList<>();
        while (!remaining.isEmpty()) {
            Media next = remaining.get(0);
            if (!result.isEmpty()) {
                String lastCreator = result.get(result.size() - 1).getCreator();
                for (Media m : remaining) {
                    if (!m.getCreator().equals(lastCreator)) {
                        next = m;
                        break;
                    }
                }
            }
            result.add(next);
            remaining.remove(next);
        }
        return result;
    }
}
