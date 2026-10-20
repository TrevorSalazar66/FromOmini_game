package com.omini.engine.domain.usecases;

import com.omini.engine.domain.dtos.RangeTesteVO;
import com.omini.engine.domain.dtos.TabelaPesosVO;
import com.omini.engine.domain.models.IRandomProvider;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Caso de Uso: Realiza o sorteio ponderado sobre a tabela de pesos de um teste.
 */
public final class ExecutarSorteioPonderadoUseCase {

    private final IRandomProvider randomProvider;

    public ExecutarSorteioPonderadoUseCase() {
        this(limite -> ThreadLocalRandom.current().nextLong(limite));
    }

    public ExecutarSorteioPonderadoUseCase(IRandomProvider randomProvider) {
        if (randomProvider == null) {
            throw new IllegalArgumentException("O provedor de aleatoriedade não pode ser nulo.");
        }
        this.randomProvider = randomProvider;
    }

    public int executar(TabelaPesosVO tabela) {
        if (tabela == null) {
            throw new IllegalArgumentException("A tabela de pesos para o sorteio não pode ser nula.");
        }

        long pesoTotal = tabela.getPesoTotal();
        if (pesoTotal <= 0) {
            throw new IllegalStateException("O peso acumulado na tabela deve ser maior que zero.");
        }

        long sorteado = randomProvider.proximoLong(pesoTotal);
        RangeTesteVO range = tabela.getRange();

        long acumulador = 0L;
        for (int valor = range.minimo(); valor <= range.maximo(); valor++) {
            acumulador += tabela.getPeso(valor);
            if (sorteado < acumulador) {
                return valor;
            }
        }

        // Fallback defensivo para o limite superior
        return range.maximo();
    }
}
