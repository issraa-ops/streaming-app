package streaming.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import streaming.exception.DuplicateMediaException;
import streaming.exception.MediaNotFoundException;
import streaming.exception.PlaylistFullException;
import streaming.exception.PremiumContentException;
import streaming.model.Audiobook;
import streaming.model.Genre;
import streaming.model.Media;
import streaming.model.Playlist;
import streaming.model.Song;
import streaming.model.User;
import streaming.subscription.FreePlan;
import streaming.subscription.PremiumPlan;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlaylistServiceTest {

    private PlaylistService service;
    private Playlist freePlaylist;

    @BeforeEach
    void setUp() {
        service = new PlaylistService(new Random(42));
        freePlaylist = new Playlist("test", new User("Alice", new FreePlan()));
    }

    private Song song(String id, String creator, int duration) {
        return new Song(id, "Song " + id, creator, duration, Genre.POP, LocalDate.of(2020, 1, 1), "Album");
    }

    @Test
    void addSongsAndComputeTotalDuration() throws Exception {
        service.addToPlaylist(freePlaylist, song("s1", "A", 200));
        service.addToPlaylist(freePlaylist, song("s2", "B", 100));

        assertEquals(2, freePlaylist.size());
        assertEquals(300, freePlaylist.getTotalDuration());
    }

    @Test
    void emptyPlaylistHasZeroDuration() {
        assertEquals(0, freePlaylist.getTotalDuration());
    }

    @Test
    void freeUserCannotAddMoreThan20Items() throws Exception {
        for (int i = 1; i <= 20; i++) {
            service.addToPlaylist(freePlaylist, song("s" + i, "A", 100));
        }

        assertThrows(PlaylistFullException.class,
                () -> service.addToPlaylist(freePlaylist, song("s21", "A", 100)));
        assertEquals(20, freePlaylist.size());
    }

    @Test
    void premiumUserCanAddMoreThan20Items() throws Exception {
        Playlist premiumPlaylist = new Playlist("big", new User("Bob", new PremiumPlan()));
        for (int i = 1; i <= 25; i++) {
            service.addToPlaylist(premiumPlaylist, song("s" + i, "A", 100));
        }

        assertEquals(25, premiumPlaylist.size());
    }

    @Test
    void freeUserCannotAddAudiobook() {
        Audiobook book = new Audiobook("a1", "Dune", "Herbert", 1000, Genre.SCIFI, LocalDate.of(1965, 8, 1), 48);

        assertThrows(PremiumContentException.class, () -> service.addToPlaylist(freePlaylist, book));
        assertEquals(0, freePlaylist.size());
    }

    @Test
    void sameMediaCannotBeAddedTwice() throws Exception {
        Song s1 = song("s1", "A", 200);
        service.addToPlaylist(freePlaylist, s1);

        assertThrows(DuplicateMediaException.class, () -> service.addToPlaylist(freePlaylist, s1));
    }

    @Test
    void removeMediaNotInPlaylistThrows() {
        assertThrows(MediaNotFoundException.class,
                () -> service.removeFromPlaylist(freePlaylist, song("s1", "A", 200)));
    }

    @Test
    void shuffleNeverPutsSameCreatorTwiceInARow() throws Exception {
        service.addToPlaylist(freePlaylist, song("s1", "A", 100));
        service.addToPlaylist(freePlaylist, song("s2", "A", 100));
        service.addToPlaylist(freePlaylist, song("s3", "B", 100));
        service.addToPlaylist(freePlaylist, song("s4", "B", 100));
        service.addToPlaylist(freePlaylist, song("s5", "C", 100));

        List<Media> shuffled = service.shuffle(freePlaylist);

        assertEquals(5, shuffled.size());
        for (int i = 1; i < shuffled.size(); i++) {
            assertNotEquals(shuffled.get(i - 1).getCreator(), shuffled.get(i).getCreator());
        }
    }

    @Test
    void shuffleWithOnlyOneCreatorKeepsAllItems() throws Exception {
        service.addToPlaylist(freePlaylist, song("s1", "A", 100));
        service.addToPlaylist(freePlaylist, song("s2", "A", 100));
        service.addToPlaylist(freePlaylist, song("s3", "A", 100));

        List<Media> shuffled = service.shuffle(freePlaylist);

        // not possible to avoid the same creator, so we just check nothing is lost
        assertEquals(3, shuffled.size());
        assertTrue(shuffled.containsAll(freePlaylist.getItems()));
    }

    @Test
    void shuffleDoesNotChangeThePlaylist() throws Exception {
        service.addToPlaylist(freePlaylist, song("s1", "A", 100));
        service.addToPlaylist(freePlaylist, song("s2", "B", 100));

        service.shuffle(freePlaylist);

        assertEquals("s1", freePlaylist.getItems().get(0).getId());
        assertFalse(freePlaylist.getItems().isEmpty());
    }
}
