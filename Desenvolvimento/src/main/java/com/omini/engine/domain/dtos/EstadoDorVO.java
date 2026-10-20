package com.omini.engine.domain.dtos;

/**
 * Value Object com a avaliação da dor, analgésicos ativos, teto de foco e penalidades fisiológicas.
 */
public final class EstadoDorVO {

    private final double dorReal;
    private final double nivelAnalgesico;
    private final double dorEfetiva;
    private final double tetoFoco;
    private final String classificacao;
    private final double penalidadeRolagensPercentual;

    public EstadoDorVO(double dorReal,
                       double nivelAnalgesico,
                       double dorEfetiva,
                       double tetoFoco,
                       String classificacao,
                       double penalidadeRolagensPercentual) {
        this.dorReal = dorReal;
        this.nivelAnalgesico = nivelAnalgesico;
        this.dorEfetiva = dorEfetiva;
        this.tetoFoco = tetoFoco;
        this.classificacao = classificacao;
        this.penalidadeRolagensPercentual = penalidadeRolagensPercentual;
    }

    public double getDorReal() {
        return dorReal;
    }

    public double getNivelAnalgesico() {
        return nivelAnalgesico;
    }

    public double getDorEfetiva() {
        return dorEfetiva;
    }

    public double getTetoFoco() {
        return tetoFoco;
    }

    public String getClassificacao() {
        return classificacao;
    }

    public double getPenalidadeRolagensPercentual() {
        return penalidadeRolagensPercentual;
    }

    @Override
    public String toString() {
        return "EstadoDorVO{" +
                "classificacao='" + classificacao + '\'' +
                ", dorEfetiva=" + String.format("%.1f", dorEfetiva) +
                ", tetoFoco=" + String.format("%.1f", tetoFoco) +
                ", penalidade=-" + String.format("%.1f%%", penalidadeRolagensPercentual * 100) +
                '}';
    }
}
