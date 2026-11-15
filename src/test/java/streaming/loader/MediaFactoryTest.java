package streaming.loader;

import org.junit.jupiter.api.Test;
import streaming.exception.InvalidMediaDataException;
import streaming.model.Audiobook;
import streaming.model.Media;
import streaming.model.Podcast;
import streaming.model.Song;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MediaFactoryTest {

    @Test
    void createsTheRightSubclass() throws Exception {
        Media song = MediaFactory.fromCsvLine("SONG,s1,Blinding Lights,The Weeknd,200,POP,2019-11-29,After Hours");
        Media podcast = MediaFactory.fromCsvLine("PODCAST,p1,Episode 42,Tech Talk,3600,TECH,2026-10-01,42");
        Media book = MediaFactory.fromCsvLine("AUDIOBOOK,a1,Dune,Frank Herbert,75600,SCIFI,1965-08-01,48");

        assertTrue(song instanceof Song);
        assertTrue(podcast instanceof Podcast);
        assertTrue(book instanceof Audiobook);
        assertEquals(200, song.getDurationSeconds());
    }

    @Test
    void unknownTypeIsRejected() {
        assertThrows(InvalidMediaDataException.class,
                () -> MediaFactory.fromCsvLine("VIDEO,v1,Video,Someone,100,POP,2020-01-01,x"));
    }

    @Test
    void durationThatIsNotANumberIsRejected() {
        assertThrows(InvalidMediaDataException.class,
                () -> MediaFactory.fromCsvLine("SONG,s1,Title,Artist,abc,POP,2020-01-01,Album"));
    }

    @Test
    void badDateIsRejected() {
        assertThrows(InvalidMediaDataException.class,
                () -> MediaFactory.fromCsvLine("SONG,s1,Title,Artist,100,POP,01/01/2020,Album"));
    }

    @Test
    void missingColumnIsRejected() {
        assertThrows(InvalidMediaDataException.class,
                () -> MediaFactory.fromCsvLine("SONG,s1,Title,Artist,100,POP"));
    }

    @Test
    void negativeDurationIsRejected() {
        assertThrows(InvalidMediaDataException.class,
                () -> MediaFactory.fromCsvLine("SONG,s1,Title,Artist,-5,POP,2020-01-01,Album"));
    }
}
