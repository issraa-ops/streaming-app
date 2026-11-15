package streaming.model;

import streaming.subscription.SubscriptionPlan;

import java.util.HashSet;
import java.util.Set;

public class User {
    private final String name;
    private SubscriptionPlan plan;
    private final Set<Genre> favoriteGenres;

    public User(String name, SubscriptionPlan plan) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name cannot be empty");
        }
        this.name = name;
        this.plan = plan;
        this.favoriteGenres = new HashSet<>();
    }

    public void addFavoriteGenre(Genre genre) {
        favoriteGenres.add(genre);
    }

    public String getName() {
        return name;
    }

    public SubscriptionPlan getPlan() {
        return plan;
    }

    public void setPlan(SubscriptionPlan plan) {
        this.plan = plan;
    }

    public Set<Genre> getFavoriteGenres() {
        return Set.copyOf(favoriteGenres);
    }
}
