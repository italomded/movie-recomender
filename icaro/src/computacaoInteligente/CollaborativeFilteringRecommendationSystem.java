package computacaoInteligente;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CollaborativeFilteringRecommendationSystem {
	private Map<String, Map<String, Double>> ratings;

	public CollaborativeFilteringRecommendationSystem() {
		ratings = new HashMap<>();
	}

	public void addRating(String user, String movie, Double rating) {
		ratings.computeIfAbsent(user, k -> new HashMap<>()).put(movie, rating);
	}

	public double calculateSimilarity(Map<String, Double> userRatings1, Map<String, Double> userRatings2) {
		double sumOfSquares = 0.0;
		for (String movie : userRatings1.keySet()) {
			if (userRatings2.containsKey(movie)) {
				double rating1 = userRatings1.get(movie);
				double rating2 = userRatings2.get(movie);
				sumOfSquares += Math.pow(rating1 - rating2, 2);
			}
		}
		return 1 / (1 + Math.sqrt(sumOfSquares));
	}

	public double predictRating(String user, String movie) {
		if (!ratings.containsKey(user)) {
			return 0.0; // User has no ratings
		}

		Map<String, Double> userRatings = ratings.get(user);
		double weightedSum = 0.0;
		double similaritySum = 0.0;

		for (String otherUser : ratings.keySet()) {
			if (!otherUser.equals(user)) {
				Map<String, Double> otherUserRatings = ratings.get(otherUser);
				if (otherUserRatings.containsKey(movie)) {
					double similarity = calculateSimilarity(userRatings, otherUserRatings);
					double rating = otherUserRatings.get(movie);
					weightedSum += similarity * rating;
					similaritySum += similarity;
				}
			}
		}

		if (similaritySum == 0.0) {
			return 0.0; // No similar users found
		}

		return weightedSum / similaritySum;
	}

	public static void main(String[] args) {
		CollaborativeFilteringRecommendationSystem system = new CollaborativeFilteringRecommendationSystem();
		ReadFile file = new ReadFile();

		ArrayList<String> ratingsFile = file.readFileRatings("src/files/ratings.csv");

		for (String item : ratingsFile) {
			String[] data = item.split(",");
			system.addRating("User" + data[0], "Movie" + data[1], Double.parseDouble(data[2]));
			// Integer.parseInt(data[2].split(".")[0]));
		}

		 double predictedRating = system.predictRating("User1", "Movie2");
		 System.out.println("Predicted rating for User1 and Movie2: " + predictedRating);

	}

}
