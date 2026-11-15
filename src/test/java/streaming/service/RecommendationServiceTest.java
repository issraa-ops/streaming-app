package streaming.service;

import org.junit.jupiter.api.Test;
import streaming.model.Audiobook;
import streaming.model.Genre;
import streaming.model.Media;
import streaming.model.Song;
import streaming.model.User;
import streaming.subscription.FreePlan;
import streaming.subscription.PremiumPlan;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecommendationServiceTest {

    private final RecommendationService service = new RecommendationService();
    private final Song popSong = new Song("s1", "Pop", "A", 200, Genre.POP, LocalDate.of(2020, 1, 1), "Album");
    private final Song rockSong = new Song("s2", "Rock", "B", 200, Genre.ROCK, LocalDate.of(2020, 1, 1), "Album");
    private final Audiobook book = new Audiobook("a1", "Dune", "C", 1000, Genre.SCIFI, LocalDate.of(1965, 8, 1), 48);

    @Test
    void songOfFavoriteGenreComesFirst() {
        User user = new User("Alice", new FreePlan());
        user.addFavoriteGenre(Genre.ROCK);

        List<Media> result = service.recommend(user, List.of(popSong, rockSong), 2);

        assertEquals("s2", result.get(0).getId());
    }

    @Test
    void freeUserDoesNotGetAudiobooks() {
        User user = new User("Alice", new FreePlan());

        List<Media> result = service.recommend(user, List.of(popSong, book), 5);

        assertFalse(result.contains(book));
    }

    @Test
    void startedAudiobookIsPushedFirst() {
        User user = new User("Bob", new PremiumPlan());
        book.savePosition(500);

        List<Media> result = service.recommend(user, List.of(popSong, rockSong, book), 3);

        assertEquals("a1", result.get(0).getId());
    }

    @Test
    void resultIsLimitedToN() {
        User user = new User("Bob", new PremiumPlan());

        assertEquals(1, service.recommend(user, List.of(popSong, rockSong, book), 1).size());
        assertTrue(service.recommend(user, List.of(), 3).isEmpty());
    }
}
