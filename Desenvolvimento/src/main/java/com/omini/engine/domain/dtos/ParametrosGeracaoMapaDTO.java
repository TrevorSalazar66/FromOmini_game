package com.omini.engine.domain.dtos;

import com.omini.engine.domain.models.TileTypeEnum;

/**
 * Parâmetros de configuração para a geração procedural de mapas/masmorras.
 */
public final class ParametrosGeracaoMapaDTO {

    private final int largura;
    private final int altura;
    private final int maxSalas;
    private final int tamanhoMinSala;
    private final int tamanhoMaxSala;
    private final long seed;
    private final TileTypeEnum tipoChao;
    private final TileTypeEnum tipoParede;

    public ParametrosGeracaoMapaDTO(int largura, int altura, int maxSalas,
                                   int tamanhoMinSala, int tamanhoMaxSala,
                                   long seed, TileTypeEnum tipoChao, TileTypeEnum tipoParede) {
        if (largura <= 5 || altura <= 5) {
            throw new IllegalArgumentException("Dimensões do mapa devem ser maiores que 5.");
        }
        if (tamanhoMinSala < 2 || tamanhoMaxSala < tamanhoMinSala) {
            throw new IllegalArgumentException("Dimensões de sala inválidas.");
        }
        if (maxSalas <= 0) {
            throw new IllegalArgumentException("Quantidade máxima de salas deve ser positiva.");
        }

        this.largura = largura;
        this.altura = altura;
        this.maxSalas = maxSalas;
        this.tamanhoMinSala = tamanhoMinSala;
        this.tamanhoMaxSala = tamanhoMaxSala;
        this.seed = seed;
        this.tipoChao = tipoChao != null ? tipoChao : TileTypeEnum.CHAO_PEDRA;
        this.tipoParede = tipoParede != null ? tipoParede : TileTypeEnum.PAREDE_GRANITO;
    }

    public ParametrosGeracaoMapaDTO(int largura, int altura, int maxSalas, int tamanhoMinSala, int tamanhoMaxSala, long seed) {
        this(largura, altura, maxSalas, tamanhoMinSala, tamanhoMaxSala, seed, TileTypeEnum.CHAO_PEDRA, TileTypeEnum.PAREDE_GRANITO);
    }

    public int getLargura() {
        return largura;
    }

    public int getAltura() {
        return altura;
    }

    public int getMaxSalas() {
        return maxSalas;
    }

    public int getTamanhoMinSala() {
        return tamanhoMinSala;
    }

    public int getTamanhoMaxSala() {
        return tamanhoMaxSala;
    }

    public long getSeed() {
        return seed;
    }

    public TileTypeEnum getTipoChao() {
        return tipoChao;
    }

    public TileTypeEnum getTipoParede() {
        return tipoParede;
    }
}
