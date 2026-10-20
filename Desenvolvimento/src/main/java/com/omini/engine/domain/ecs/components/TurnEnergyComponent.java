package com.omini.engine.domain.ecs.components;

import com.omini.engine.domain.ecs.Component;

/**
 * Componente que armazena a reserva de energia e iniciativa da entidade para o motor de turnos e AP.
 */
public final class TurnEnergyComponent implements Component {

    public static final int LIMIAR_ACAO_PADRAO = 100;

    private int energia;
    private final int limiarAcao;

    public TurnEnergyComponent(int energiaInicial, int limiarAcao) {
        this.energia = energiaInicial;
        this.limiarAcao = limiarAcao > 0 ? limiarAcao : LIMIAR_ACAO_PADRAO;
    }

    public TurnEnergyComponent(int energiaInicial) {
        this(energiaInicial, LIMIAR_ACAO_PADRAO);
    }

    public TurnEnergyComponent() {
        this(0, LIMIAR_ACAO_PADRAO);
    }

    public int getEnergia() {
        return energia;
    }

    public int getLimiarAcao() {
        return limiarAcao;
    }

    public boolean podeAgir() {
        return energia >= limiarAcao;
    }

    public void adicionarEnergia(int quantidade) {
        if (quantidade > 0) {
            this.energia += quantidade;
        }
    }

    public void consumirEnergia(int quantidade) {
        if (quantidade > 0) {
            this.energia -= quantidade;
        }
    }

    @Override
    public String toString() {
        return "TurnEnergyComponent{" +
                "energia=" + energia +
                "/" + limiarAcao +
                ", pronto=" + podeAgir() +
                '}';
    }
}
