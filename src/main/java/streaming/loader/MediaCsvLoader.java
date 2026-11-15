package streaming.loader;

import streaming.exception.InvalidMediaDataException;
import streaming.model.Media;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MediaCsvLoader {
    private final List<String> errors = new ArrayList<>();

    public List<Media> load(Path file) throws IOException {
        List<String> lines = Files.readAllLines(file);
        List<Media> medias = new ArrayList<>();
        errors.clear();

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
