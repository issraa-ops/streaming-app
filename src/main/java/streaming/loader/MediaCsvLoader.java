package streaming.loader;

import streaming.exception.InvalidMediaDataException;
import streaming.model.Media;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads the media file at startup.
 * A bad line is skipped and kept in the errors list, the other lines are still loaded.
 */
public class MediaCsvLoader {

    private final List<String> errors = new ArrayList<>();

    public List<Media> load(Path file) throws IOException {
        List<String> lines = Files.readAllLines(file);
        List<Media> medias = new ArrayList<>();
        errors.clear();

        // first line is the header
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                continue;
            }
            try {
                medias.add(MediaFactory.fromCsvLine(line));
            } catch (InvalidMediaDataException e) {
                errors.add("line " + (i + 1) + " : " + e.getMessage());
            }
        }
        return medias;
    }

    public List<String> getErrors() {
        return List.copyOf(errors);
    }
}
