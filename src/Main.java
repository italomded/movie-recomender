import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String movieFilePath = "C:\\programacao\\movie-recommender-leo\\src\\movies.csv";
        String ratingFilePath = "C:\\programacao\\movie-recommender-leo\\src\\ratings.csv";

        try {
            MovieRecommendationSystem movieSystem = new MovieRecommendationSystem();
            movieSystem.loadMovieData(movieFilePath);
            movieSystem.loadRatingData(ratingFilePath);
            movieSystem.generateRecommendations();
            movieSystem.printRecommendations();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}