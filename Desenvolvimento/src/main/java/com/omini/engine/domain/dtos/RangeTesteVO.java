package com.omini.engine.domain.dtos;

/**
 * Objeto de valor imutável que representa o intervalo [minimo, maximo] de sorteio de um teste.
 */
public record RangeTesteVO(int minimo, int maximo) {

    public RangeTesteVO {
        if (minimo < 1) {
            throw new IllegalArgumentException("O valor mínimo do teste não pode ser inferior a 1. Recebido: " + minimo);
        }
        if (maximo < minimo) {
            throw new IllegalArgumentException("O valor máximo (" + maximo + ") não pode ser menor que o mínimo (" + minimo + ").");
        }
    }

    /**
     * Retorna a quantidade total de números inteiros presentes no intervalo sorteável.
     */
    public int getTamanhoIntervalo() {
        return (maximo - minimo) + 1;
    }
}
