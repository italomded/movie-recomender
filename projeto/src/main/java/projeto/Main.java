package projeto;

import projeto.modelos.Avaliacao;
import projeto.modelos.Filme;
import projeto.modelos.Similaridade;
import projeto.modelos.Usuario;
import projeto.utilitarios.ProcessadorDeDados;
import projeto.utilitarios.TransformadorDeRecursos;

import java.util.*;

public class Main {
    public static final String MENSAGEM_ERRO_DIALOGOS = "Ocorrreu um erro durante o processamento dos diálogos!";
    public static final String NOME_ARQUIVO_FILMES = "movies";
    public static final String NOME_ARQUIVO_AVALIACOES = "ratings";


    public static void main(String[] args) {
        try {
            Usuario usuario;

            System.out.println("Lendo dados dos arquivos...");
            ProcessadorDeDados processadorDeDados = new ProcessadorDeDados();

            System.out.println("Lendo e processando arquivo de filmes...");
            List<Filme> filmes = processadorDeDados.carregarFilmes(NOME_ARQUIVO_FILMES);

            System.out.println("Lendo e processando arquivo de avaliacões...");
            Map<Usuario, List<Avaliacao>> matrizAvaliacoes = processadorDeDados.carregarDadosMatrizAvaliacoes(NOME_ARQUIVO_AVALIACOES, filmes);

            try {

                Scanner scanner = new Scanner(System.in);
                scanner.useLocale(Locale.US);

                System.out.println("\nSe for um usuário da base, digite o identificador dele, caso contrário, digite -1.");
                System.out.println("Se o identificador digitado for inválido, será considerado como o usuário de identificador 1.");
                System.out.print("[DIGITE]: ");
                Long identificador = scanner.nextLong();
                scanner.nextLine();

                if (identificador.equals(-1L)) {
                    usuario = new Usuario(identificador);
                    List<Avaliacao> avaliacoesUsuario = new ArrayList<>();
                    matrizAvaliacoes.put(usuario, avaliacoesUsuario);

                    System.out.println("\nAgora você deverá digitar uma parte ou o nome completo de um filme.");
                    System.out.println("O algoritmo irá te retornar os resultados correspondentes, se houverem, com seus respectivos identificadores.");
                    System.out.println("Caso o algoritmo não encontre o filme aparecerá novamente o dialogo de pesquisa.");
                    System.out.println("Você deverá digitar o identificador do filme e atribuir uma nota (padrão ponto).");
                    System.out.println("Caso a nota for inválida, retornará ao diálogo de pesquisa.");
                    System.out.println("Se digitar um identificador inválido, retornará ao diálogo de pesquisa.");
                    System.out.println("Em seguida será perguntado se deseja continuar, se sim deverá digitar Y e qualquer outra resposta será interpratada como um não.");
                    System.out.println("Sua avaliação só terá sido cadastrada caso veja a mensagem afirmativa.\n");
                    boolean finalizouAvaliacoes = false;
                    while (!finalizouAvaliacoes) {
                        System.out.print("[PESQUISE O FILME]: ");
                        String pesquisa = scanner.nextLine();

                        List<Filme> filmesEncontrados = filmes.stream().filter(filme -> filme.nome().toLowerCase().contains(pesquisa.toLowerCase())).toList();
                        if (!filmesEncontrados.isEmpty()) {
                            filmesEncontrados.forEach(System.out::println);

                            System.out.print("[DIGITE O IDENTIFICADOR DO FILME]: ");
                            Long identificadorDoFilme = scanner.nextLong();
                            Optional<Filme> filmeDesejado = filmesEncontrados.stream().filter(filme -> filme.identificador().equals(identificadorDoFilme)).findFirst();

                            if (filmeDesejado.isPresent()) {
                                System.out.print("[ATRIBUA A NOTA]: ");
                                double notaFilme = scanner.nextDouble();
                                scanner.nextLine();

                                if (notaFilme >= 0.0) {
                                    avaliacoesUsuario.add(new Avaliacao(filmeDesejado.get(), notaFilme));
                                    System.out.println("[AVALIAÇÃO CADASTRADA]");
                                    System.out.print("\n[CONTINUAR (Y/N)]: ");
                                    String continuar = scanner.nextLine();
                                    if (!continuar.equalsIgnoreCase("y")) {
                                        finalizouAvaliacoes = true;
                                    } else {
                                        System.out.println();
                                    }
                                }
                            } else {
                                scanner.nextLine();
                            }
                        }
                    }

                } else {
                    usuario = new Usuario(identificador);
                    List<Avaliacao> avaliacaos = matrizAvaliacoes.get(usuario);
                    if (avaliacaos == null) usuario = new Usuario(1L);
                }

            } catch (Exception excecao) {
                throw new RuntimeException(MENSAGEM_ERRO_DIALOGOS, excecao);
            }

            TransformadorDeRecursos transformadorDeRecursos = new TransformadorDeRecursos();
            Map<Usuario, List<Avaliacao>> matrizAvaliacoesNormalizada = transformadorDeRecursos.normalizarMatrizDeAvaliacoes(matrizAvaliacoes);

            List<Similaridade> similaridades = transformadorDeRecursos.calcularUsuariosSimilares(matrizAvaliacoesNormalizada, usuario);
            List<Filme> filmesRecomendados = transformadorDeRecursos.buscarRecomendacoes(similaridades, matrizAvaliacoes, usuario, 5);
            System.out.println("\nFilmes recomendados:");
            filmesRecomendados.forEach(System.out::println);
        } catch (Exception excecao) {
            System.out.println("\n[PROGRAMA FINALIZADO]");
            System.out.print("[CAUSA]: ");
            System.out.print(excecao.getMessage());
        }
    }
}
