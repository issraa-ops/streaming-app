package streaming.subscription;

import streaming.model.Media;

public interface SubscriptionPlan {
    String getName();

    int maxPlaylistSize();

    int maxSkipsPerHour();

    boolean canAccess(Media media);
}
