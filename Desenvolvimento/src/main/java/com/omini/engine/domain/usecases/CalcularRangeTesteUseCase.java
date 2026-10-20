package com.omini.engine.domain.usecases;

import com.omini.engine.domain.dtos.RangeTesteVO;

/**
 * Caso de Uso: Calcula o intervalo dinâmico de sorteio [minimo, maximo] com base no atributo do personagem.
 * Regra: Minimo = Atributo; Maximo = Atributo * multiplicadorLinear (padrão: 10).
 */
public final class CalcularRangeTesteUseCase {

    private static final int MULTIPLICADOR_PADRAO = 10;

    public RangeTesteVO executar(int atributoBase) {
        return executar(atributoBase, MULTIPLICADOR_PADRAO);
    }

    public RangeTesteVO executar(int atributoBase, int multiplicadorLinear) {
        if (atributoBase < 1) {
            throw new IllegalArgumentException("O atributo base para o teste deve ser pelo menos 1. Recebido: " + atributoBase);
        }
        if (multiplicadorLinear < 1) {
            throw new IllegalArgumentException("O multiplicador linear deve ser pelo menos 1. Recebido: " + multiplicadorLinear);
        }

        int minimo = atributoBase;
        int maximo = atributoBase * multiplicadorLinear;

        return new RangeTesteVO(minimo, maximo);
    }
}
