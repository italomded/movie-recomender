import java.util.Scanner;
import java.util.Random;

public class Start {
    public static void main(String[] args) throws Exception {
        CollaborativeFilteringAlgorithm algorithm = new CollaborativeFilteringAlgorithm();
        algorithm.loadData("movies.csv", "ratings.csv");
        algorithm.preprocessData();

        System.out.println("====================================");
        System.out.println("SISTEMA DE RECOMENDAÇÃO DE FILMES");
        System.out.println("====================================");

        Scanner sc = new Scanner(System.in);
        Random rd = new Random();

        System.out.print("Digite seu número de identificaço: ");
        int userId = sc.nextInt();

        System.out.print("Digite o número máximo de recomendações (n): ");
        int qtd = sc.nextInt();

        System.out.println();

        sc.close();

        algorithm.userBasedCollaborativeFiltering(userId, 5);
        algorithm.evaluateRecommendations(userId, rd.nextInt(10), qtd);
    }
}
