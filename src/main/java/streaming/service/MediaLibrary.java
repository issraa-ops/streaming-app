package streaming.service;

import streaming.exception.MediaNotFoundException;
import streaming.model.Media;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * All the media loaded from the file.
 */
public class MediaLibrary {

    private final List<Media> medias;

    public MediaLibrary(List<Media> medias) {
        this.medias = new ArrayList<>(medias);
    }

    public Optional<Media> findById(String id) {
        return medias.stream()
                .filter(m -> m.getId().equals(id))
                .findFirst();
    }

    public Media getById(String id) throws MediaNotFoundException {
        return findById(id).orElseThrow(() -> new MediaNotFoundException("no media with id " + id));
    }

    public List<Media> getAll() {
        return List.copyOf(medias);
    }

    public int size() {
        return medias.size();
    }
}
