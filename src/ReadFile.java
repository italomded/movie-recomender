import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ReadFile {
    public static Map<Integer, String> loadMovies(String filePath) {
        Map<Integer, String> movies = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Ignorar cabeçalho do arquivo
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                int movieId = Integer.parseInt(parts[0]);
                String movieTitle = parts[1];
                movies.put(movieId, movieTitle);
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar o arquivo de filmes: " + e.getMessage());
        }

        return movies;
    }

    public static Map<Integer, Map<Integer, Double>> loadRatings(String filePath) {
        Map<Integer, Map<Integer, Double>> ratings = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Ignorar cabeçalho do arquivo
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                int userId = Integer.parseInt(parts[0]);
                int movieId = Integer.parseInt(parts[1]);
                double rating = Double.parseDouble(parts[2]);
                addRating(ratings, userId, movieId, rating);
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar o arquivo de avaliações: " + e.getMessage());
        }

        return ratings;
    }

    private static void addRating(Map<Integer, Map<Integer, Double>> ratings, int userId, int movieId, double rating) {
        ratings.putIfAbsent(userId, new HashMap<>());
        ratings.get(userId).put(movieId, rating);
    }
}
