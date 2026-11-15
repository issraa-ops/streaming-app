package streaming.service;

import org.junit.jupiter.api.Test;
import streaming.exception.MediaNotFoundException;
import streaming.model.Genre;
import streaming.model.Song;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MediaLibraryTest {
    private final Song song = new Song("s1", "Bad Guy", "Billie", 194, Genre.POP, LocalDate.of(2019, 3, 29), "Album");
    private final MediaLibrary library = new MediaLibrary(List.of(song));

    @Test
    void findByIdReturnsTheMedia() throws Exception {
        assertEquals(song, library.getById("s1"));
        assertTrue(library.findById("s1").isPresent());
    }

    @Test
    void unknownIdGivesEmptyOptionalAndException() {
        assertTrue(library.findById("zzz").isEmpty());
        assertThrows(MediaNotFoundException.class, () -> library.getById("zzz"));
    }
}
