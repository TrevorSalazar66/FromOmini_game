package com.omini.engine.domain.ecs.components;

import com.omini.engine.domain.ecs.Component;

/**
 * Componente responsável pelo sistema tático de dor, farmacologia (analgésicos) e foco mental.
 */
public final class PainFocusComponent implements Component {

    private double dorReal;
    private double nivelAnalgesico;
    private double focoAtual;

    public PainFocusComponent(double dorInicial, double focoInicial) {
        this.dorReal = Math.max(0.0, Math.min(100.0, dorInicial));
        this.nivelAnalgesico = 0.0;
        this.focoAtual = Math.max(0.0, Math.min(getTetoFoco(), focoInicial));
    }

    public PainFocusComponent() {
        this(0.0, 100.0);
    }

    public double getDorReal() {
        return dorReal;
    }

    public double getNivelAnalgesico() {
        return nivelAnalgesico;
    }

    public double getDorEfetiva() {
        return Math.max(0.0, dorReal - nivelAnalgesico);
    }

    public double getTetoFoco() {
        return Math.max(0.0, 100.0 - getDorEfetiva());
    }

    public double getFocoAtual() {
        return Math.min(focoAtual, getTetoFoco());
    }

    public void adicionarDor(double valor) {
        if (valor > 0.0) {
            this.dorReal = Math.min(100.0, this.dorReal + valor);
            // Ao aumentar a dor, o teto de foco cai imediatamente
            if (this.focoAtual > getTetoFoco()) {
                this.focoAtual = getTetoFoco();
            }
        }
    }

    public void reduzirDor(double valor) {
        if (valor > 0.0) {
            this.dorReal = Math.max(0.0, this.dorReal - valor);
        }
    }

    public void administrarAnalgesico(double dosagem) {
        if (dosagem > 0.0) {
            this.nivelAnalgesico = Math.min(100.0, this.nivelAnalgesico + dosagem);
        }
    }

    public void processarTurnoFarmacologico(double taxaDecaimentoAnalgesico, double recuperacaoNaturalDor) {
        if (taxaDecaimentoAnalgesico > 0.0) {
            this.nivelAnalgesico = Math.max(0.0, this.nivelAnalgesico - taxaDecaimentoAnalgesico);
        }
        if (recuperacaoNaturalDor > 0.0) {
            this.dorReal = Math.max(0.0, this.dorReal - recuperacaoNaturalDor);
        }
        if (this.focoAtual > getTetoFoco()) {
            this.focoAtual = getTetoFoco();
        }
    }

    public boolean gastarFoco(double custo) {
        if (custo <= 0.0) return true;
        if (getFocoAtual() >= custo) {
            this.focoAtual = getFocoAtual() - custo;
            return true;
        }
        return false;
    }

    public void regenerarFoco(double valor) {
        if (valor > 0.0) {
            this.focoAtual = Math.min(getTetoFoco(), this.focoAtual + valor);
        }
    }

    @Override
    public String toString() {
        return "PainFocusComponent{" +
                "dorReal=" + String.format("%.1f", dorReal) +
                ", analgesico=" + String.format("%.1f", nivelAnalgesico) +
                ", dorEfetiva=" + String.format("%.1f", getDorEfetiva()) +
                ", foco=" + String.format("%.1f/%.1f", getFocoAtual(), getTetoFoco()) +
                '}';
    }
}
