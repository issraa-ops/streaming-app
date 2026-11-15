package streaming.model;

import java.util.ArrayList;
import java.util.List;

public class Playlist {
    private final String name;
    private final User owner;
    private final List<Media> items;

    public Playlist(String name, User owner) {
        this.name = name;
        this.owner = owner;
        this.items = new ArrayList<>();
    }

    public void add(Media media) {
        items.add(media);
    }

    public boolean remove(Media media) {
        return items.remove(media);
    }

    public boolean contains(Media media) {
        return items.contains(media);
    }

    public int size() {
        return items.size();
    }

    public int getTotalDuration() {
        return items.stream().mapToInt(Media::getDurationSeconds).sum();
    }

    public String getName() {
        return name;
    }

    public User getOwner() {
        return owner;
    }

    public List<Media> getItems() {
        return List.copyOf(items);
    }
}
