package com.omini.engine.domain.dtos;

import java.util.Collections;
import java.util.Map;

/**
 * Objeto de valor imutável contendo a distribuição de pesos probabilísticos sobre o intervalo do teste.
 */
public final class TabelaPesosVO {

    private final RangeTesteVO range;
    private final Map<Integer, Long> pesosPorValor;
    private final long pesoTotal;

    public TabelaPesosVO(RangeTesteVO range, Map<Integer, Long> pesosPorValor) {
        if (range == null) {
            throw new IllegalArgumentException("O range associado à tabela de pesos não pode ser nulo.");
        }
        if (pesosPorValor == null || pesosPorValor.isEmpty()) {
            throw new IllegalArgumentException("A tabela de pesos deve conter ao menos um valor ponderado.");
        }

        this.range = range;
        this.pesosPorValor = Collections.unmodifiableMap(pesosPorValor);

        long acumulado = 0;
        for (Long peso : pesosPorValor.values()) {
            if (peso == null || peso < 1) {
                throw new IllegalArgumentException("Cada elemento do intervalo deve possuir peso estritamente positivo (>= 1).");
            }
            acumulado += peso;
        }
        this.pesoTotal = acumulado;
    }

    public RangeTesteVO getRange() {
        return range;
    }

    public Map<Integer, Long> getPesosPorValor() {
        return pesosPorValor;
    }

    public long getPesoTotal() {
        return pesoTotal;
    }

    public long getPeso(int valor) {
        return pesosPorValor.getOrDefault(valor, 0L);
    }
}
