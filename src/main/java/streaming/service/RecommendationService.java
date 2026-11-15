package streaming.service;

import streaming.model.Media;
import streaming.model.User;

import java.util.Comparator;
import java.util.List;

public class RecommendationService {
    public List<Media> recommend(User user, List<Media> medias, int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n cannot be negative");
        }
        return medias.stream()
                .filter(m -> user.getPlan().canAccess(m))
                .sorted(Comparator.comparingDouble((Media m) -> m.recommendationScore(user)).reversed())
                .limit(n)
                .toList();
    }
}
