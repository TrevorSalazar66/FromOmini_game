package com.omini.engine.domain.ecs.components;

import com.omini.engine.domain.ecs.Component;

/**
 * Componente que armazena os atributos fundamentais de uma criatura ou personagem.
 */
public final class StatsComponent implements Component {

    private int forca;
    private int destreza;
    private int constituicao;
    private int percepcao;
    private int inteligencia;

    public StatsComponent(int forca, int destreza, int constituicao, int percepcao, int inteligencia) {
        if (forca < 1 || destreza < 1 || constituicao < 1 || percepcao < 1 || inteligencia < 1) {
            throw new IllegalArgumentException("Todos os atributos base devem ser no mínimo 1.");
        }
        this.forca = forca;
        this.destreza = destreza;
        this.constituicao = constituicao;
        this.percepcao = percepcao;
        this.inteligencia = inteligencia;
    }

    public StatsComponent() {
        this(10, 10, 10, 10, 10);
    }

    public int getForca() {
        return forca;
    }

    public void setForca(int forca) {
        if (forca >= 1) this.forca = forca;
    }

    public int getDestreza() {
        return destreza;
    }

    public void setDestreza(int destreza) {
        if (destreza >= 1) this.destreza = destreza;
    }

    public int getConstituicao() {
        return constituicao;
    }

    public void setConstituicao(int constituicao) {
        if (constituicao >= 1) this.constituicao = constituicao;
    }

    public int getPercepcao() {
        return percepcao;
    }

    public void setPercepcao(int percepcao) {
        if (percepcao >= 1) this.percepcao = percepcao;
    }

    public int getInteligencia() {
        return inteligencia;
    }

    public void setInteligencia(int inteligencia) {
        if (inteligencia >= 1) this.inteligencia = inteligencia;
    }

    public double getHpBasePadrao() {
        return constituicao * 10.0;
    }

    @Override
    public String toString() {
        return "StatsComponent{" +
                "FOR=" + forca +
                ", DES=" + destreza +
                ", CON=" + constituicao +
                ", PER=" + percepcao +
                ", INT=" + inteligencia +
                '}';
    }
}
