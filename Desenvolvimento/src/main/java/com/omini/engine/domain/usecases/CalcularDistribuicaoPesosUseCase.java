package com.omini.engine.domain.usecases;

import com.omini.engine.domain.dtos.RangeTesteVO;
import com.omini.engine.domain.dtos.TabelaPesosVO;

import java.util.HashMap;
import java.util.Map;

/**
 * Caso de Uso: Gera a distribuição contínua de pesos probabilísticos sobre o intervalo do teste.
 * Implementa:
 * - Nível 0: Rampa de Decaimento linear (punição por inexperiência).
 * - Níveis 1 a 70: Curva Contínua do "Monte de Areia" com herança de pesos e deslocamento contínuo do pico.
 */
public final class CalcularDistribuicaoPesosUseCase {

    private static final int NIVEL_MAXIMO_PERICIA = 70;
    private static final long PESO_BASE_MINIMO = 1L;
    private static final long RAMPA_PESO_MAXIMO = 100L;

    public TabelaPesosVO executar(RangeTesteVO range, int nivelPericia) {
        if (range == null) {
            throw new IllegalArgumentException("O range não pode ser nulo.");
        }
        if (nivelPericia < 0) {
            throw new IllegalArgumentException("O nível de perícia não pode ser negativo. Recebido: " + nivelPericia);
        }

        int nivelLimitado = Math.min(nivelPericia, NIVEL_MAXIMO_PERICIA);

        if (nivelLimitado == 0) {
            return gerarRampaDecaimento(range);
        }

        return gerarCurvaMonteDeAreia(range, nivelLimitado);
    }

    /**
     * Nível 0: Inexperiência gera peso alto no mínimo que decai até 1 no valor máximo.
     */
    private TabelaPesosVO gerarRampaDecaimento(RangeTesteVO range) {
        Map<Integer, Long> pesos = new HashMap<>();
        int min = range.minimo();
        int max = range.maximo();
        int span = range.getTamanhoIntervalo();

        for (int valor = min; valor <= max; valor++) {
            if (span == 1) {
                pesos.put(valor, RAMPA_PESO_MAXIMO);
                continue;
            }
            double fator = (double) (max - valor) / (span - 1);
            long peso = PESO_BASE_MINIMO + Math.round(fator * (RAMPA_PESO_MAXIMO - PESO_BASE_MINIMO));
            pesos.put(valor, Math.max(PESO_BASE_MINIMO, peso));
        }

        return new TabelaPesosVO(range, pesos);
    }

    /**
     * Níveis 1 a 70: Interpolação contínua ("Monte de Areia").
     * Desliza o pico do percentil 10% (Nível 1) até 98% (Nível 70), estreitando a dispersão.
     */
    private TabelaPesosVO gerarCurvaMonteDeAreia(RangeTesteVO range, int nivel) {
        Map<Integer, Long> pesos = new HashMap<>();
        int min = range.minimo();
        int max = range.maximo();
        int span = range.getTamanhoIntervalo();

        // Percentil central do pico (0.10 no nível 1 até 0.98 no nível 70)
        double progressoNivel = (double) (nivel - 1) / (NIVEL_MAXIMO_PERICIA - 1);
        double percentilPico = 0.10 + (0.88 * progressoNivel);
        double valorPico = min + ((max - min) * percentilPico);

        // Desvio relativo (dispersão): quanto maior o nível, mais afunilado é o pico
        // Nível 1: dispersão ampla (sigma largo = 0.25 do range)
        // Nível 70: foco quase absoluto (sigma estreito = 0.03 do range)
        double sigmaRelativo = 0.25 - (0.22 * progressoNivel);
        double sigma = Math.max(1.0, (max - min) * sigmaRelativo);

        // Multiplicador de escala do pico para consolidar a certeza no nível alto
        double alturaPico = 10.0 + (5000.0 * Math.pow(progressoNivel, 2));

        for (int valor = min; valor <= max; valor++) {
            double distancia = valor - valorPico;
            double gaussiana = Math.exp(-0.5 * Math.pow(distancia / sigma, 2));
            long pesoCalculado = PESO_BASE_MINIMO + Math.round(gaussiana * alturaPico);
            pesos.put(valor, Math.max(PESO_BASE_MINIMO, pesoCalculado));
        }

        return new TabelaPesosVO(range, pesos);
    }
}
