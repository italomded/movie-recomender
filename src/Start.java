import java.util.Scanner;
import java.util.Random;

public class Start {
    public static void main(String[] args) throws Exception {
        CollaborativeFilteringAlgorithm cf = new CollaborativeFilteringAlgorithm();
        cf.loadMovies("movies.csv");
        cf.loadRatings("ratings.csv");
        cf.preprocessData();

        System.out.println("====================================");
        System.out.println("SISTEMA DE RECOMENDAÇÃO DE FILMES");
        System.out.println("====================================");

        Scanner sc = new Scanner(System.in);
        Random rd = new Random();

        System.out.print("Digite seu número de identificaço: ");
        int userId = sc.nextInt();

        System.out.print("Digite o número máximo de recomendações (n): ");
        int qtd = sc.nextInt();

        sc.close();

        System.out.println();
        cf.userBasedCollaborativeFiltering(userId, 5);
        cf.evaluateRecommendations(userId, rd.nextInt(10), qtd);
    }
}
