package streaming.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import streaming.exception.SkipLimitReachedException;
import streaming.model.Audiobook;
import streaming.model.Genre;
import streaming.model.Song;
import streaming.model.User;
import streaming.subscription.FreePlan;
import streaming.subscription.PremiumPlan;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlaybackServiceTest {

    private PlaybackService service;
    private final LocalDateTime now = LocalDateTime.of(2026, 11, 14, 10, 0);

    @BeforeEach
    void setUp() {
        service = new PlaybackService();
    }

    @Test
    void seventhSkipInAnHourIsRefusedForFreeUser() throws Exception {
        User alice = new User("Alice", new FreePlan());
        for (int i = 0; i < 6; i++) {
            service.skip(alice, now);
        }

        assertThrows(SkipLimitReachedException.class, () -> service.skip(alice, now));
    }

    @Test
    void skipsOlderThanOneHourDoNotCount() throws Exception {
        User alice = new User("Alice", new FreePlan());
        for (int i = 0; i < 6; i++) {
            service.skip(alice, now);
        }

        assertDoesNotThrow(() -> service.skip(alice, now.plusHours(1).plusMinutes(1)));
    }

    @Test
    void premiumUserHasNoSkipLimit() {
        User bob = new User("Bob", new PremiumPlan());

        assertDoesNotThrow(() -> {
            for (int i = 0; i < 50; i++) {
                service.skip(bob, now);
            }
        });
    }

    @Test
    void audiobookResumesAtSavedPosition() {
        Audiobook book = new Audiobook("a1", "Dune", "Herbert", 10000, Genre.SCIFI, LocalDate.of(1965, 8, 1), 48);

        service.stop(book, 3600);

        assertEquals(3600, book.getResumePosition());
        assertTrue(service.play(book).startsWith("Resuming"));
    }

    @Test
    void songIsAlwaysPlayedFromTheStart() {
        Song song = new Song("s1", "Bad Guy", "Billie", 194, Genre.POP, LocalDate.of(2019, 3, 29), "Album");

        service.stop(song, 100);

        assertTrue(service.play(song).startsWith("Playing"));
    }

    @Test
    void positionAfterTheEndIsRefused() {
        Audiobook book = new Audiobook("a1", "Dune", "Herbert", 1000, Genre.SCIFI, LocalDate.of(1965, 8, 1), 48);

        assertThrows(IllegalArgumentException.class, () -> service.stop(book, 5000));
    }
}
