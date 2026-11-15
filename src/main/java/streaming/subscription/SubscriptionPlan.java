package streaming.subscription;

import streaming.model.Media;

/**
 * Rules of a subscription. The services only know this interface,
 * so adding a new plan (student plan for example) does not change them.
 */
public interface SubscriptionPlan {

    String getName();

    int maxPlaylistSize();

    int maxSkipsPerHour();

    boolean canAccess(Media media);
}
