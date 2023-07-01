package projeto.utilitarios;

import projeto.modelos.Avaliacao;
import projeto.modelos.Filme;
import projeto.modelos.Similaridade;
import projeto.modelos.Usuario;

import java.util.*;

public class TransformadorDeRecursos {
    private static final String MENSAGEM_ERRO_NORMALIZACAO = "Ocorreu um erro inesperado durante a normalização dos dados!";
    private static final String MENSAGEM_ERRO_CALCULO_SIMILARIDADE = "Ocorreu um erro inesperado durante o cálculo de valor dos dados!";
    private static final String MENSAGEM_ERRO_USUARIO_ALVO_SEM_DADOS = "O usuário alvo do cálculo de valor não possui dados de avaliação de filmes!";
    private static final String MENSAGEM_ERRO_RECOMENDACAO = "Ocorreu um erro durante a recomendação dos filmes para o usuário alvo!";

    public Map<Usuario, List<Avaliacao>> normalizarMatrizDeAvaliacoes(Map<Usuario, List<Avaliacao>> matrizDeAvaliacoes) {
        try {
            Map<Usuario, List<Avaliacao>> matrizDeAvaliacoesNormalizada = new HashMap<>();
            matrizDeAvaliacoes.forEach(((usuario, avaliacaos) -> {
                double somaDasAvaliacoes = avaliacaos.stream().map(Avaliacao::nota).reduce(Double::sum).orElseThrow();
                double mediaDasAvaliacoes = somaDasAvaliacoes / avaliacaos.size();

                List<Avaliacao> novaListaDeAvaliacoes = avaliacaos.stream()
                        .map(avaliacao -> new Avaliacao(avaliacao.filme(), avaliacao.nota() - mediaDasAvaliacoes))
                        .toList();

                matrizDeAvaliacoesNormalizada.put(usuario, novaListaDeAvaliacoes);
            }));
            return matrizDeAvaliacoesNormalizada;
        } catch (Exception excecao) {
            throw new RuntimeException(MENSAGEM_ERRO_NORMALIZACAO, excecao);
        }
    }

    public List<Similaridade> calcularUsuariosSimilares(Map<Usuario, List<Avaliacao>> matrizDeAvaliacoesNormalizada, Usuario usuarioAlvo) {
        List<Similaridade> usuariosComSimilaridadeCalculada = new ArrayList<>();
        List<Avaliacao> avaliacaosDoUsuarioAlvo = matrizDeAvaliacoesNormalizada.get(usuarioAlvo);
        if (avaliacaosDoUsuarioAlvo == null) throw new RuntimeException(MENSAGEM_ERRO_USUARIO_ALVO_SEM_DADOS);

        for (Usuario usuario : matrizDeAvaliacoesNormalizada.keySet()) {
            if (!usuario.equals(usuarioAlvo)) {
                List<Avaliacao> avaliacaosOutroUsuario = matrizDeAvaliacoesNormalizada.get(usuario);
                double similaridadeEntreUsuarios = this.calcularSimilaridadePorCosseno(avaliacaosDoUsuarioAlvo, avaliacaosOutroUsuario);
                usuariosComSimilaridadeCalculada.add(new Similaridade(usuario, similaridadeEntreUsuarios));
            }
        }

        return usuariosComSimilaridadeCalculada;
    }

    public double calcularSimilaridadePorCosseno(List<Avaliacao> avaliacoesA, List<Avaliacao> avaliacoesB) {
        try {
            double produtoDasAvaliacoesSimilares = 0;
            double somaDosQuadradosDasAvaliacoesSimilaresA = 0;
            double somaDosQuadradosDasAvaliacoesSimilaresB = 0;

            for (Avaliacao avaliacaoA : avaliacoesA) {
                Optional<Avaliacao> avaliacaoDoMesmoFilmeOpcional = avaliacoesB.stream()
                        .filter(avaliacao -> avaliacao.filme().equals(avaliacaoA.filme()))
                        .findFirst();

                if (avaliacaoDoMesmoFilmeOpcional.isPresent()) {
                    Avaliacao avaliacaoB = avaliacaoDoMesmoFilmeOpcional.get();

                    produtoDasAvaliacoesSimilares += avaliacaoA.nota() * avaliacaoB.nota();
                    somaDosQuadradosDasAvaliacoesSimilaresA += Math.pow(avaliacaoA.nota(), 2);
                    somaDosQuadradosDasAvaliacoesSimilaresB += Math.pow(avaliacaoB.nota(), 2);
                }
            }

            double raizDaSomaDosQuadradosA = Math.sqrt(somaDosQuadradosDasAvaliacoesSimilaresA);
            double raizDaSomaDosQuadradosB = Math.sqrt(somaDosQuadradosDasAvaliacoesSimilaresB);

            double similaridadeEntreAvaliacoes = 0;
            if (raizDaSomaDosQuadradosA != 0 && raizDaSomaDosQuadradosB != 0) {
                similaridadeEntreAvaliacoes = produtoDasAvaliacoesSimilares / (raizDaSomaDosQuadradosA * raizDaSomaDosQuadradosB);
            }

            return similaridadeEntreAvaliacoes;
        } catch (Exception excecao) {
            throw new RuntimeException(MENSAGEM_ERRO_CALCULO_SIMILARIDADE, excecao);
        }
    }

    public List<Filme> buscarRecomendacoes(List<Similaridade> similaridades, Map<Usuario, List<Avaliacao>> matrizDeAvaliacoes, Usuario usuarioAlvo, int usuariosProximos) {
        try {
            similaridades.sort(Comparator.comparing(Similaridade::valor).reversed());
            usuariosProximos = Math.min(similaridades.size(), usuariosProximos);

            List<Filme> filmesQueUsuarioAlvoJaAssistiu = matrizDeAvaliacoes.get(usuarioAlvo).stream().map(Avaliacao::filme).toList();
            List<Filme> filmesRecomendados = new ArrayList<>();

            if (filmesQueUsuarioAlvoJaAssistiu.isEmpty())
                throw new RuntimeException(MENSAGEM_ERRO_USUARIO_ALVO_SEM_DADOS);
            for (int contador = 0; contador < usuariosProximos; contador++) {
                Similaridade similaridade = similaridades.get(contador);
                List<Avaliacao> avaliacaos = matrizDeAvaliacoes.get(similaridade.usuario());
                if (avaliacaos != null && !avaliacaos.isEmpty()) {
                    Integer contadorFilmesRecomendadosPorUsuario = 0;
                    for (Avaliacao avaliacao : avaliacaos) {
                        if (contadorFilmesRecomendadosPorUsuario.equals(5)) break;
                        if (!filmesQueUsuarioAlvoJaAssistiu.contains(avaliacao.filme())) {
                            filmesRecomendados.add(avaliacao.filme());
                            contadorFilmesRecomendadosPorUsuario++;
                        }
                    }
                }
            }

            return filmesRecomendados;
        } catch (Exception excecao) {
            throw new RuntimeException(MENSAGEM_ERRO_RECOMENDACAO, excecao);
        }
    }
}
