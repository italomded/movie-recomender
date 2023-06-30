package projeto.utilitarios;

import projeto.modelos.Avaliacao;
import projeto.modelos.Filme;
import projeto.modelos.Usuario;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ProcessadorDeDados {
    private static final String MENSAGEM_ERRO_LEITURA_ARQUIVO = "Erro inesperado na leitura do arquivo!";
    private static final String MENSAGEM_ERRO_TRANSFORMACAO_ARQUIVO = "Erro inesperado na transformação da linha do arquivo!";

    public List<Filme> carregarFilmes(String nomeArquivo) {
        try (var arquivo = this.carregarArquivo(nomeArquivo)) {
            List<Filme> filmes = new ArrayList<>();
            try (var bufferedReader = new BufferedReader(new InputStreamReader(arquivo))) {
                String linha;
                bufferedReader.readLine();
                while ((linha = bufferedReader.readLine()) != null) {
                    this.transformarLinhaEmFilme(filmes, linha);
                }
                return filmes;
            }
        } catch (IOException excecao) {
            throw new RuntimeException(MENSAGEM_ERRO_LEITURA_ARQUIVO, excecao);
        }
    }

    public Map<Usuario, List<Avaliacao>> carregarDadosMatrizAvaliacoes(String nomeArquivo, List<Filme> filmes) {
        try (var arquivo = this.carregarArquivo(nomeArquivo)) {
            Map<Usuario, List<Avaliacao>> matrizDeAvaliacoes = new HashMap<>();
            try (var bufferedReader = new BufferedReader(new InputStreamReader(arquivo))) {
                String linha;
                bufferedReader.readLine();
                while ((linha = bufferedReader.readLine()) != null) {
                    this.transformarLinhaEmAvaliacao(matrizDeAvaliacoes, filmes, linha);
                }
                return matrizDeAvaliacoes;
            }
        } catch (IOException excecao) {
            throw new RuntimeException(MENSAGEM_ERRO_LEITURA_ARQUIVO, excecao);
        }
    }

    private InputStream carregarArquivo(String nomeDoArquivo) {
        InputStream arquivo = this.getClass().getClassLoader().getResourceAsStream(String.format("%s.%s", nomeDoArquivo, ExtensaoArquivo.CSV));
        if (arquivo == null) throw new RuntimeException("Arquivo inexistente!");
        return arquivo;
    }

    private void transformarLinhaEmAvaliacao(Map<Usuario, List<Avaliacao>> matrizDeAvaliacoes, List<Filme> filmes, String linha) {
        try {
            String[] dadosDaLinhaEmColunas = linha.split(",");
            Usuario usuario = new Usuario(Long.parseLong(dadosDaLinhaEmColunas[0]));
            List<Avaliacao> avaliacoes = matrizDeAvaliacoes.get(usuario);

            boolean jaExistemAvaliacoes = avaliacoes != null;
            if (!jaExistemAvaliacoes) {
                avaliacoes = new ArrayList<>();
                matrizDeAvaliacoes.put(usuario, avaliacoes);
            }

            Filme filmeAvaliado = filmes.stream().filter(filme -> filme.identificador().equals(Long.parseLong(dadosDaLinhaEmColunas[1]))).findFirst().orElseThrow();
            Avaliacao avaliacao = new Avaliacao(filmeAvaliado, Double.parseDouble(dadosDaLinhaEmColunas[2]));
            avaliacoes.add(avaliacao);
        } catch (Exception excecao) {
            throw new RuntimeException(MENSAGEM_ERRO_TRANSFORMACAO_ARQUIVO, excecao);
        }
    }

    private void transformarLinhaEmFilme(List<Filme> filmes, String linha) {
        try {
            String[] dadosDaLinhaEmColunas = linha.split(",", 2);
            long identificadorDoFilme = Long.parseLong(dadosDaLinhaEmColunas[0]);
            String tituloDoFilme = dadosDaLinhaEmColunas[1].replaceFirst(Regex.ULTIMA_VIRGULA_EM_DIANTE, "").replaceAll(Regex.ASPAS, "");
            Filme filme = new Filme(identificadorDoFilme, tituloDoFilme);
            filmes.add(filme);
        } catch (Exception excecao) {
            throw new RuntimeException(MENSAGEM_ERRO_TRANSFORMACAO_ARQUIVO, excecao);
        }
    }
}
