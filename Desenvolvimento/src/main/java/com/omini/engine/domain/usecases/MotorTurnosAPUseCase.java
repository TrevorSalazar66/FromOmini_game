package com.omini.engine.domain.usecases;

import com.omini.engine.domain.ecs.Entity;
import com.omini.engine.domain.ecs.components.StatsComponent;
import com.omini.engine.domain.ecs.components.TurnEnergyComponent;

import java.util.List;
import java.util.Optional;

/**
 * Caso de Uso responsável por gerenciar a iniciativa e o relógio de turnos discretos baseado em energia e AP.
 */
public final class MotorTurnosAPUseCase {

    private int relogioGlobalTicks = 0;

    public int getRelogioGlobalTicks() {
        return relogioGlobalTicks;
    }

    /**
     * Avança os ticks de tempo até que pelo menos uma entidade atinja o limiar de ação (energia >= 100).
     * Retorna a entidade com maior prontidão para agir.
     */
    public Optional<Entity> obterProximaEntidadePronta(List<Entity> entidades) {
        if (entidades == null || entidades.isEmpty()) {
            return Optional.empty();
        }

        int iteracoesSeguranca = 1000;
        while (iteracoesSeguranca-- > 0) {
            Entity maisPronta = null;
            int maiorEnergia = -1;

            for (Entity e : entidades) {
                if (e.hasTag("morto")) continue;

                var energyOpt = e.getComponent(TurnEnergyComponent.class);
                if (energyOpt.isPresent() && energyOpt.get().podeAgir()) {
                    if (energyOpt.get().getEnergia() > maiorEnergia) {
                        maiorEnergia = energyOpt.get().getEnergia();
                        maisPronta = e;
                    }
                }
            }

            if (maisPronta != null) {
                return Optional.of(maisPronta);
            }

            // Nenhum pronto: avança 1 tick e distribui energia baseada na velocidade
            relogioGlobalTicks++;
            for (Entity e : entidades) {
                if (e.hasTag("morto")) continue;

                e.getComponent(TurnEnergyComponent.class).ifPresent(energy -> {
                    int velocidade = 100;
                    var statsOpt = e.getComponent(StatsComponent.class);
                    if (statsOpt.isPresent()) {
                        velocidade += statsOpt.get().getDestreza() * 2;
                    }
                    energy.adicionarEnergia(velocidade / 10);
                });
            }
        }

        return Optional.empty();
    }

    /**
     * Consome os pontos de ação (AP) da entidade após ela ter executado sua ação.
     */
    public void registrarAcaoExecutada(Entity entidade, int custoAP) {
        if (entidade == null || custoAP <= 0) return;
        entidade.getComponent(TurnEnergyComponent.class).ifPresent(energy -> {
            energy.consumirEnergia(custoAP);
        });
    }
}
