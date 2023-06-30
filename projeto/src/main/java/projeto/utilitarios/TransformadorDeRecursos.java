package projeto.utilitarios;

import projeto.modelos.Avaliacao;
import projeto.modelos.Similaridade;
import projeto.modelos.Usuario;

import java.util.*;
import java.util.stream.Stream;

public class TransformadorDeRecursos {
    private static final String MENSAGEM_ERRO_NORMALIZACAO = "Ocorreu um erro inesperado durante a normalização dos dados!";
    private static final String MENSAGEM_ERRO_CALCULO_SIMILARIDADE = "Ocorreu um erro inesperado durante o cálculo de valor dos dados!";
    private static final String MENSAGEM_ERRO_USUARIO_ALVO_SEM_DADOS = "O usuário alvo do cálculo de valor não possui dados de avaliação de filmes!";

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
                double similaridadeEntreUsuarios = this.calcularSimilaridadeEntreAvaliacoes(avaliacaosDoUsuarioAlvo, avaliacaosOutroUsuario);
                usuariosComSimilaridadeCalculada.add(new Similaridade(usuario, similaridadeEntreUsuarios));
            }
        }

        return usuariosComSimilaridadeCalculada;
    }

    public double calcularSimilaridadeEntreAvaliacoes(List<Avaliacao> avaliacoesA, List<Avaliacao> avaliacoesB) {
        try  {
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
}
