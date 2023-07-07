import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollaborativeFilteringAlgorithm {
    private Map<Integer, String> movies;
    private Map<Integer, Map<Integer, Double>> ratings;

    public CollaborativeFilteringAlgorithm() {
        movies = new HashMap<>();
        ratings = new HashMap<>();
    }

    public void loadData(String moviesFilePath, String ratingsFilePath) {
        movies = ReadFile.loadMovies(moviesFilePath);
        ratings = ReadFile.loadRatings(ratingsFilePath);
    }

    public void preprocessData() {
        removeDuplicateRatings();
    }

    private void removeDuplicateRatings() {
        for (Map.Entry<Integer, Map<Integer, Double>> entry : ratings.entrySet()) {
            int userId = entry.getKey();
            Map<Integer, Double> userRatings = entry.getValue();
            Map<Integer, Double> uniqueRatings = new HashMap<>();

            for (Map.Entry<Integer, Double> ratingEntry : userRatings.entrySet()) {
                int movieId = ratingEntry.getKey();
                double rating = ratingEntry.getValue();

                if (!uniqueRatings.containsKey(movieId)) {
                    uniqueRatings.put(movieId, rating);
                }
            }

            ratings.put(userId, uniqueRatings);
        }
    }

    public void userBasedCollaborativeFiltering(int userId, int k) throws Exception {
        Map<Integer, Double> userRatings = ratings.get(userId);
        if (userRatings == null)
            throw new Exception("Usuário não encontrado");

        // Map<Integer, Double> userSimilarities = calculateUserSimilarities(userId);
        // List<Integer> similarUsers = findKSimilarUsers(userSimilarities, k);
        // Map<Integer, Double> moviePredictions = generateMoviePredictions(userId,
        // similarUsers);

        // for (Map.Entry<Integer, Double> entry : moviePredictions.entrySet()) {
        // int movieId = entry.getKey();
        // double prediction = entry.getValue();
        // String movieTitle = movies.get(movieId);
        // // System.out.println("Previsão de avaliação para o filme '" + movieTitle +
        // "':
        // // " + prediction);
        // }
    }

    private Map<Integer, Double> calculateUserSimilarities(int targetUserId) {
        Map<Integer, Double> userSimilarities = new HashMap<>();
        Map<Integer, Double> targetUserRatings = ratings.get(targetUserId);

        for (Map.Entry<Integer, Map<Integer, Double>> entry : ratings.entrySet()) {
            int userId = entry.getKey();
            if (userId != targetUserId) {
                Map<Integer, Double> userRatings = entry.getValue();
                double similarity = calculateCosineSimilarity(targetUserRatings, userRatings);
                userSimilarities.put(userId, similarity);
            }
        }

        return userSimilarities;
    }

    private double calculateCosineSimilarity(Map<Integer, Double> ratingsA, Map<Integer, Double> ratingsB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (Map.Entry<Integer, Double> entry : ratingsA.entrySet()) {
            int movieId = entry.getKey();
            double ratingA = entry.getValue();
            Double ratingB = ratingsB.get(movieId);

            if (ratingB != null) {
                dotProduct += ratingA * ratingB;
            }
            normA += ratingA * ratingA;
        }

        for (double rating : ratingsB.values()) {
            normB += rating * rating;
        }

        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private List<Integer> findKSimilarUsers(Map<Integer, Double> userSimilarities, int k) {
        List<Integer> similarUsers = new ArrayList<>(userSimilarities.keySet());
        similarUsers.sort(
                (userId1, userId2) -> Double.compare(userSimilarities.get(userId2), userSimilarities.get(userId1)));
        return similarUsers.subList(0, Math.min(k, similarUsers.size()));
    }

    private Map<Integer, Double> generateMoviePredictions(int targetUserId, List<Integer> similarUsers) {
        Map<Integer, Double> moviePredictions = new HashMap<>();
        Map<Integer, Double> targetUserRatings = ratings.get(targetUserId);

        for (Map.Entry<Integer, String> movieEntry : movies.entrySet()) {
            int movieId = movieEntry.getKey();
            if (!targetUserRatings.containsKey(movieId)) {
                double prediction = calculateMoviePrediction(targetUserId, movieId, similarUsers);
                moviePredictions.put(movieId, prediction);
            }
        }

        return moviePredictions;
    }

    private double calculateMoviePrediction(int targetUserId, int movieId, List<Integer> similarUsers) {
        double prediction = 0.0;
        double similaritySum = 0.0;

        for (int userId : similarUsers) {
            Map<Integer, Double> userRatings = ratings.get(userId);
            Double rating = userRatings.get(movieId);

            if (rating != null) {
                double similarity = calculateCosineSimilarity(ratings.get(targetUserId), userRatings);
                prediction += similarity * rating;
                similaritySum += similarity;
            }
        }

        if (similaritySum == 0.0)
            return 0.0;

        return prediction / similaritySum;
    }

    public void evaluateRecommendations(int userId, int k, int n) {
        List<Integer> recommendedMovies = getTopNRecommendations(userId, k, n);
        System.out
                .println("Com base nas preferências do usuário " + userId + " são recomendados os seguintes filmes: ");

        for (int movieId : recommendedMovies) {
            String movieTitle = movies.get(movieId);
            System.out.println("- " + movieTitle);
        }
    }

    private List<Integer> getTopNRecommendations(int userId, int k, int n) {
        Map<Integer, Double> userSimilarities = calculateUserSimilarities(userId);
        List<Integer> similarUsers = findKSimilarUsers(userSimilarities, k);
        Map<Integer, Double> moviePredictions = generateMoviePredictions(userId, similarUsers);
        List<Integer> recommendedMovies = new ArrayList<>(moviePredictions.keySet());

        recommendedMovies.sort(
                (movieId1, movieId2) -> Double.compare(moviePredictions.get(movieId2), moviePredictions.get(movieId1)));

        return recommendedMovies.subList(0, Math.min(n, recommendedMovies.size()));
    }
}
