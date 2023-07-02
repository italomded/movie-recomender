import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class MovieRecommender {

    private static Map<Integer, String> movies;
    private static Map<Integer, Map<Integer, Double>> ratings;

    public static void main(String[] args) throws IOException {
        // Load the movies data
        movies = loadMoviesData();

        // Load the ratings data
        ratings = loadRatingsData();

        // Prompt the user for a movie ID
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a movie ID: ");
        int movieID = scanner.nextInt();

        // Check if the ratings map is null
        if (ratings != null) {
            findSimilarMovies(movieID);
        } else {
            // The ratings map is null, so there are no recommendations
            System.out.println("No recommendations available.");
        }
    }

    private static Map<Integer, String> loadMoviesData() throws IOException {
        Map<Integer, String> movies = new HashMap<>();
        String moviesFile = "movies.csv";
        Files.lines(Paths.get(moviesFile)).forEach(line -> {
            String[] tokens = line.split(",");
            movies.put(Integer.parseInt(tokens[0]), tokens[1] + " (" + tokens[2] + ")");
        });
        return movies;
    }

    private static Map<Integer, Map<Integer, Double>> loadRatingsData() throws IOException {
        Map<Integer, Map<Integer, Double>> ratings = new HashMap<>();
        String ratingsFile = "ratings.csv";
        Files.lines(Paths.get(ratingsFile)).forEach(line -> {
            String[] tokens = line.split(",");
            int userID = Integer.parseInt(tokens[0]);
            int movieID = Integer.parseInt(tokens[1]);
            double rating = Double.parseDouble(tokens[2]);

            if (!ratings.containsKey(userID)) {
                ratings.put(userID, new HashMap<>());
            }

            ratings.get(userID).put(movieID, rating);
        });
        return ratings;
    }

    private static void findSimilarMovies(int movieID) {
        Map<Integer, Double> similarMovies = new HashMap<>();
        if (ratings.containsKey(movieID)) {
            for (int otherMovieID : ratings.keySet()) {
                if (otherMovieID != movieID) {
                    double similarity = 0.0;
                    for (int userID : ratings.get(movieID).keySet()) {
                        if (ratings.get(otherMovieID).containsKey(userID)) {
                            similarity += ratings.get(movieID).get(userID) * ratings.get(otherMovieID).get(userID);
                        }
                    }
                    similarity /= ratings.get(movieID).size();

                    similarMovies.put(otherMovieID, similarity);
                }
            }
        } else {
            // The ratings map does not contain a rating for this movie ID, so there are no recommendations
            System.out.println("No recommendations available.");
            return;
        }

        if(similarMovies.size() > 0){
            int recommendation = new Random().nextInt(similarMovies.size());
            System.out.println(movies.get(recommendation) + " (score: " + similarMovies.get(recommendation) + ")");
        } else {
            System.out.println("No recommendations available.");
        }
    }
}
