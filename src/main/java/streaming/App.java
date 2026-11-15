package streaming;

import streaming.exception.DuplicateMediaException;
import streaming.exception.MediaNotFoundException;
import streaming.exception.PlaylistFullException;
import streaming.exception.PremiumContentException;
import streaming.exception.SkipLimitReachedException;
import streaming.loader.MediaCsvLoader;
import streaming.model.Genre;
import streaming.model.Media;
import streaming.model.Playlist;
import streaming.model.User;
import streaming.service.MediaLibrary;
import streaming.service.PlaybackService;
import streaming.service.PlaylistService;
import streaming.service.RecommendationService;
import streaming.subscription.FreePlan;
import streaming.subscription.PremiumPlan;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public class App {

    public static void main(String[] args) {
        Path file = Path.of(args.length > 0 ? args[0] : "data/media.csv");

        // 1. load the data
        MediaCsvLoader loader = new MediaCsvLoader();
        MediaLibrary library;
        try {
            library = new MediaLibrary(loader.load(file));
        } catch (IOException e) {
            System.out.println("Cannot read " + file + " : " + e.getMessage());
            return;
        }
        System.out.println(library.size() + " media loaded from " + file);
        for (String error : loader.getErrors()) {
            System.out.println("  skipped -> " + error);
        }

        PlaylistService playlistService = new PlaylistService();
        PlaybackService playbackService = new PlaybackService();
        RecommendationService recommendationService = new RecommendationService();

        // 2. a free user builds a playlist
        User alice = new User("Alice", new FreePlan());
        alice.addFavoriteGenre(Genre.POP);
        alice.addFavoriteGenre(Genre.TECH);
        Playlist playlist = new Playlist("My mix", alice);

        System.out.println();
        System.out.println("=== " + alice.getName() + " (" + alice.getPlan().getName() + ") builds '" + playlist.getName() + "' ===");
        String[] ids = {"s1", "s2", "s3", "s4", "p1", "a1", "s1", "x9"};
        for (String id : ids) {
            try {
                Media media = library.getById(id);
                playlistService.addToPlaylist(playlist, media);
                System.out.println("added " + media);
            } catch (MediaNotFoundException | PremiumContentException | DuplicateMediaException | PlaylistFullException e) {
                System.out.println("refused : " + e.getMessage());
            }
        }
        System.out.println("total duration : " + PlaybackService.formatTime(playlist.getTotalDuration()));

        System.out.println();
        System.out.println("=== shuffle (no same artist twice in a row) ===");
        for (Media media : playlistService.shuffle(playlist)) {
            System.out.println("  " + media);
        }

        // 3. skips
        System.out.println();
        System.out.println("=== skips ===");
        LocalDateTime now = LocalDateTime.now();
        for (int i = 1; i <= 7; i++) {
            try {
                playbackService.skip(alice, now);
                System.out.println("skip " + i + " ok");
            } catch (SkipLimitReachedException e) {
                System.out.println("skip " + i + " refused : " + e.getMessage());
            }
        }

        // 4. a premium user listens to an audiobook and comes back later
        User bob = new User("Bob", new PremiumPlan());
        bob.addFavoriteGenre(Genre.SCIFI);
        System.out.println();
        System.out.println("=== " + bob.getName() + " (" + bob.getPlan().getName() + ") ===");
        try {
            Media dune = library.getById("a1");
            System.out.println(playbackService.play(dune));
            playbackService.stop(dune, 5400);
            System.out.println(playbackService.play(dune));
        } catch (MediaNotFoundException e) {
            System.out.println(e.getMessage());
        }

        // 5. recommendations
        System.out.println();
        printRecommendations(recommendationService, alice, library.getAll());
        printRecommendations(recommendationService, bob, library.getAll());
    }

    private static void printRecommendations(RecommendationService service, User user, List<Media> medias) {
        System.out.println("=== top 3 for " + user.getName() + " ===");
        for (Media media : service.recommend(user, medias, 3)) {
            System.out.printf("  %-40s score %.0f%n", media, media.recommendationScore(user));
        }
    }
}
