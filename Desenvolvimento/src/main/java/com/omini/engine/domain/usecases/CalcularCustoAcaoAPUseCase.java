package com.omini.engine.domain.usecases;

import com.omini.engine.domain.dtos.BodyPartStateVO;
import com.omini.engine.domain.dtos.EstadoSobrecargaVO;
import com.omini.engine.domain.ecs.Entity;
import com.omini.engine.domain.ecs.components.*;
import com.omini.engine.domain.models.TipoAcaoEnum;

/**
 * Caso de Uso responsável por calcular o custo dinâmico de Pontos de Ação (AP) de qualquer ação,
 * aplicando penalidades fisiológicas (fraturas, dor) e de sobrecarga de peso corporal.
 */
public final class CalcularCustoAcaoAPUseCase {

    private final CalcularSobrecargaUseCase sobrecargaUseCase;

    public CalcularCustoAcaoAPUseCase(CalcularSobrecargaUseCase sobrecargaUseCase) {
        this.sobrecargaUseCase = sobrecargaUseCase != null ? sobrecargaUseCase : new CalcularSobrecargaUseCase();
    }

    public CalcularCustoAcaoAPUseCase() {
        this(new CalcularSobrecargaUseCase());
    }

    public int executar(Entity ator, TipoAcaoEnum acao) {
        if (acao == null) {
            throw new IllegalArgumentException("A ação não pode ser nula.");
        }
        if (ator == null) {
            return acao.getCustoBaseAP();
        }

        double custoCalculado = acao.getCustoBaseAP();

        // 1. Modificador de Sobrecarga de Inventário
        var statsOpt = ator.getComponent(StatsComponent.class);
        var gridOpt = ator.getComponent(InventoryGridComponent.class);

        if (statsOpt.isPresent() && gridOpt.isPresent()) {
            double peso = gridOpt.get().getPesoTotalItens();
            int forca = statsOpt.get().getForca();
            EstadoSobrecargaVO sobrecarga = sobrecargaUseCase.executar(peso, forca);
            double penalidadePeso = sobrecarga.getPenalidadeAPPercentual() / 100.0;
            custoCalculado *= (1.0 + penalidadePeso);
        }

        // 2. Modificador Fisiológico de Fraturas em Membros Inferiores para Movimento
        if (acao == TipoAcaoEnum.MOVER) {
            var anatomyOpt = ator.getComponent(AnatomyComponent.class);
            if (anatomyOpt.isPresent()) {
                double multiplicadorFratura = 1.0;
                for (BodyPartStateVO parte : anatomyOpt.get().getTodasPartes()) {
                    if (isMembroLocomocao(parte.getPartId()) && parte.isFraturado()) {
                        // Fratura sem tala dobra o esforço (+100%); com tala adiciona +40%
                        multiplicadorFratura += parte.isImobilizadoComTala() ? 0.40 : 1.00;
                    }
                }
                custoCalculado *= multiplicadorFratura;
            }
        }

        // 3. Modificador de Dor Aguda (atrasa reflexos e tomada de decisão)
        var painOpt = ator.getComponent(PainFocusComponent.class);
        if (painOpt.isPresent()) {
            double dorEfetiva = painOpt.get().getDorEfetiva();
            if (dorEfetiva > 20.0) {
                // Atraso de até +25% de AP por dor aguda
                double penalidadeDor = (dorEfetiva - 20.0) / 320.0;
                custoCalculado *= (1.0 + Math.min(0.25, penalidadeDor));
            }
        }

        return (int) Math.round(custoCalculado);
    }

    private boolean isMembroLocomocao(String partId) {
        String id = partId.toLowerCase();
        return id.contains("thigh") || id.contains("shin") || id.contains("foot") || id.contains("leg");
    }
}
