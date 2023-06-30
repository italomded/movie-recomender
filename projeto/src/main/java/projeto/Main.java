package projeto;

import projeto.modelos.Avaliacao;
import projeto.modelos.Filme;
import projeto.modelos.Similaridade;
import projeto.modelos.Usuario;
import projeto.utilitarios.ProcessadorDeDados;
import projeto.utilitarios.TransformadorDeRecursos;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        ProcessadorDeDados processadorDeDados = new ProcessadorDeDados();
        List<Filme> filmes = processadorDeDados.carregarFilmes("movies");
        Map<Usuario, List<Avaliacao>> matrizAvaliacoes = processadorDeDados.carregarDadosMatrizAvaliacoes("ratings", filmes);

        TransformadorDeRecursos transformadorDeRecursos = new TransformadorDeRecursos();
        Map<Usuario, List<Avaliacao>> matrizAvaliacoesNormalizada = transformadorDeRecursos.normalizarMatrizDeAvaliacoes(matrizAvaliacoes);

        List<Similaridade> similaridades = transformadorDeRecursos.calcularUsuariosSimilares(matrizAvaliacoesNormalizada, new Usuario(1L));
        similaridades.sort(Comparator.comparing(Similaridade::valor).reversed());
        similaridades.forEach(System.out::println);
    }
}
