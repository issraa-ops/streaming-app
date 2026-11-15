package streaming.subscription;

import streaming.model.Audiobook;
import streaming.model.Media;

public class FreePlan implements SubscriptionPlan {
    @Override
    public String getName() {
        return "Free";
    }

    @Override
    public int maxPlaylistSize() {
        return 20;
    }

    @Override
    public int maxSkipsPerHour() {
        return 6;
    }

    @Override
    public boolean canAccess(Media media) {
        return !(media instanceof Audiobook);
    }
}
