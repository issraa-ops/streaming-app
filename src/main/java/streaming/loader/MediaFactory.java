package streaming.loader;

import streaming.exception.InvalidMediaDataException;
import streaming.model.Audiobook;
import streaming.model.Genre;
import streaming.model.Media;
import streaming.model.Podcast;
import streaming.model.Song;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class MediaFactory {
    private static final int NB_COLUMNS = 8;

    public static Media fromCsvLine(String line) throws InvalidMediaDataException {
        String[] parts = line.split(",");
        if (parts.length != NB_COLUMNS) {
            throw new InvalidMediaDataException("expected " + NB_COLUMNS + " columns : " + line);
        }

        String type = parts[0].trim();
        String id = parts[1].trim();
        String title = parts[2].trim();
        String creator = parts[3].trim();
        String extra = parts[7].trim();

        try {
            int duration = Integer.parseInt(parts[4].trim());
            Genre genre = Genre.valueOf(parts[5].trim());
            LocalDate releaseDate = LocalDate.parse(parts[6].trim());

            switch (type) {
                case "SONG":
                    return new Song(id, title, creator, duration, genre, releaseDate, extra);
                case "PODCAST":
                    return new Podcast(id, title, creator, duration, genre, releaseDate, Integer.parseInt(extra));
                case "AUDIOBOOK":
                    return new Audiobook(id, title, creator, duration, genre, releaseDate, Integer.parseInt(extra));
                default:
                    throw new InvalidMediaDataException("unknown type " + type + " : " + line);
            }
        } catch (NumberFormatException e) {
            throw new InvalidMediaDataException("not a number : " + line);
        } catch (DateTimeParseException e) {
            throw new InvalidMediaDataException("bad date : " + line);
        } catch (IllegalArgumentException e) {
            throw new InvalidMediaDataException(e.getMessage() + " : " + line);
        }
    }
}
