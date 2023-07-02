import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class MovieRecommendationSystem {
    Map<Integer, String> movieMap;
    Map<Integer, Map<Integer, Double>> userRatings;
    private Map<Integer, Map<Integer, Double>> itemRatings;
    Map<Integer, List<Integer>> recommendations;

    public MovieRecommendationSystem() {
        movieMap = new HashMap<>();
        userRatings = new HashMap<>();
        itemRatings = new HashMap<>();
        recommendations = new HashMap<>();
    }

    public void loadMovieData(String moviesFilePath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(moviesFilePath));
        String line;
        boolean isFirstLine = true;  // Adicione esta variável

        while ((line = br.readLine()) != null) {
            if (isFirstLine) {
                isFirstLine = false;
                continue;  // Ignore a primeira linha
            }

            String[] data = line.split(",");
            int movieId = Integer.parseInt(data[0]);
            String title = data[1];
            movieMap.put(movieId, title);
        }
        br.close();
    }


    public void loadRatingData(String ratingFilePath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(ratingFilePath));
        String line;
        boolean isFirstLine = true;

        while ((line = br.readLine()) != null) {
            if (isFirstLine) {
                isFirstLine = false;
                continue;
            }

            String[] data = line.split(",");
            int userId = Integer.parseInt(data[0]);
            int movieId = Integer.parseInt(data[1]);
            double rating = Double.parseDouble(data[2]);

            // Update user ratings
            userRatings.computeIfAbsent(userId, k -> new HashMap<>());
            userRatings.get(userId).put(movieId, rating);

            // Update item ratings
            itemRatings.computeIfAbsent(movieId, k -> new HashMap<>());
            itemRatings.get(movieId).put(userId, rating);
        }
        br.close();
    }

    public void generateRecommendations() {
        for (int userId : userRatings.keySet()) {
            List<Integer> recommendedMovies = recommendMovies(userId);
            recommendations.put(userId, recommendedMovies);
        }
    }

    private List<Integer> recommendMovies(int userId) {
        Map<Integer, Double> userRating = userRatings.get(userId);

        // Calculate similarity scores between the target user and other users
        Map<Integer, Double> similarityScores = new HashMap<>();
        for (int otherUserId : userRatings.keySet()) {
            if (otherUserId != userId) {
                Map<Integer, Double> otherUserRating = userRatings.get(otherUserId);
                double similarity = calculateSimilarity(userRating, otherUserRating);
                similarityScores.put(otherUserId, similarity);
            }
        }

        // Sort other users by similarity scores in descending order
        List<Integer> sortedUsers = new ArrayList<>(similarityScores.keySet());
        Collections.sort(sortedUsers, (u1, u2) -> Double.compare(similarityScores.get(u2), similarityScores.get(u1)));

        // Recommend movies based on similar users' ratings
        Set<Integer> recommendedMovies = new HashSet<>();
        for (int otherUserId : sortedUsers) {
            Map<Integer, Double> otherUserRating = userRatings.get(otherUserId);
            Set<Integer> moviesNotRatedByTargetUser = getMoviesNotRatedByUser(userId);

            for (int movieId : otherUserRating.keySet()) {
                if (moviesNotRatedByTargetUser.contains(movieId)) {
                    recommendedMovies.add(movieId);
                }

                if (recommendedMovies.size() >= 5) { // Recommend top 5 movies
                    break;
                }
            }

            if (recommendedMovies.size() >= 5) {
                break;
            }
        }

        return new ArrayList<>(recommendedMovies);
    }

    double calculateSimilarity(Map<Integer, Double> userRating1, Map<Integer, Double> userRating2) {
        // Calculate cosine similarity between two users' ratings
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (int movieId : userRating1.keySet()) {
            if (userRating2.containsKey(movieId)) {
                double rating1 = userRating1.get(movieId);
                double rating2 = userRating2.get(movieId);
                dotProduct += rating1 * rating2;
                norm1 += Math.pow(rating1, 2);
                norm2 += Math.pow(rating2, 2);
            }
        }

        double similarity = dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
        return similarity;
    }

    private Set<Integer> getMoviesNotRatedByUser(int userId) {
        Set<Integer> allMovies = new HashSet<>(movieMap.keySet());
        if (userRatings.containsKey(userId)) {
            Set<Integer> ratedMovies = userRatings.get(userId).keySet();
            allMovies.removeAll(ratedMovies);
        }
        return allMovies;
    }

    public void printRecommendations() {
        for (int userId : recommendations.keySet()) {
            System.out.println("User ID: " + userId);
            List<Integer> recommendedMovies = recommendations.get(userId);
            for (int movieId : recommendedMovies) {
                String movieTitle = movieMap.get(movieId);
                System.out.println("  - " + movieTitle);
            }
            System.out.println();
        }
    }


}

