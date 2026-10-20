package com.omini.engine.domain.usecases;

import com.omini.engine.domain.dtos.BodyPartStateVO;
import com.omini.engine.domain.ecs.Entity;
import com.omini.engine.domain.ecs.components.AnatomyComponent;
import com.omini.engine.domain.ecs.components.PainFocusComponent;
import com.omini.engine.domain.ecs.components.SurvivalComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * Caso de Uso responsável por atualizar turno a turno os ciclos fisiológicos da entidade:
 * hemorragias/sangramentos, decaimento farmacológico, foco e taxa de sobrevivência.
 */
public final class ProcessarCicloTurnoFisiologicoUseCase {

    public void executar(Entity entidade, int turnos) {
        if (entidade == null || turnos <= 0) {
            return;
        }

        // 1. Processar Sangramento Anatômico
        entidade.getComponent(AnatomyComponent.class).ifPresent(anatomy -> {
            if (anatomy.isMorto()) {
                return;
            }

            List<BodyPartStateVO> partesAtualizadas = new ArrayList<>();
            for (BodyPartStateVO parte : anatomy.getTodasPartes()) {
                double sangramento = parte.getSangramentoPorTurno();
                if (sangramento > 0.0) {
                    double danoTotalHemorragia = sangramento * turnos;
                    BodyPartStateVO novaParte = parte.comDano(danoTotalHemorragia);
                    partesAtualizadas.add(novaParte);
                }
            }

            for (BodyPartStateVO p : partesAtualizadas) {
                anatomy.atualizarParte(p);
            }

            if (anatomy.isMorto()) {
                entidade.addTag("morto");
            }
        });

        // 2. Processar Farmacologia, Dor e Foco
        entidade.getComponent(PainFocusComponent.class).ifPresent(painFocus -> {
            double taxaDecaimentoAnalgesico = 1.0 * turnos;
            double recuperacaoDor = 0.2 * turnos;

            // Se possuir fraturas não imobilizadas, a dor não se recupera espontaneamente
            boolean temFraturaNaoTratada = false;
            var anatomyOpt = entidade.getComponent(AnatomyComponent.class);
            if (anatomyOpt.isPresent()) {
                for (BodyPartStateVO parte : anatomyOpt.get().getTodasPartes()) {
                    if (parte.isFraturado() && !parte.isImobilizadoComTala()) {
                        temFraturaNaoTratada = true;
                        break;
                    }
                }
            }

            if (temFraturaNaoTratada) {
                recuperacaoDor = 0.0;
            }

            painFocus.processarTurnoFarmacologico(taxaDecaimentoAnalgesico, recuperacaoDor);

            // Regeneração natural de foco mental
            double regeneracaoFoco = 1.5 * turnos;
            painFocus.regenerarFoco(regeneracaoFoco);
        });

        // 3. Processar Sobrevivência Básica
        entidade.getComponent(SurvivalComponent.class).ifPresent(survival -> {
            survival.incrementarSede(0.05 * turnos);
            survival.incrementarFome(0.02 * turnos);
            survival.incrementarSono(0.01 * turnos);
            survival.recuperarEstamina(2.0 * turnos);
        });
    }

    public void executar(Entity entidade) {
        executar(entidade, 1);
    }
}
