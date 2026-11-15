package streaming.subscription;

import streaming.model.Media;

public class PremiumPlan implements SubscriptionPlan {

    @Override
    public String getName() {
        return "Premium";
    }

    @Override
    public int maxPlaylistSize() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int maxSkipsPerHour() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean canAccess(Media media) {
        return true;
    }
}
