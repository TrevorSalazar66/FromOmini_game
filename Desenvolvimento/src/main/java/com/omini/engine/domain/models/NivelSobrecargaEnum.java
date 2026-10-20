package com.omini.engine.domain.models;

/**
 * Faixas de classificação da carga transportada por uma criatura em relação à sua força.
 */
public enum NivelSobrecargaEnum {
    LEVE("Carga Leve", 0, 0.0),
    CONFORTAVEL("Carga Confortável", 0, 0.0),
    PESADA("Carga Pesada", 15, 0.20),
    CRITICA("Carga Crítica", 40, 0.50),
    INCAPACITADO("Incapacitado por Excesso de Peso", 100, 1.00);

    private final String descricao;
    private final int penalidadeAPPercentual;
    private final double penalidadeEsquivaPercentual;

    NivelSobrecargaEnum(String descricao, int penalidadeAPPercentual, double penalidadeEsquivaPercentual) {
        this.descricao = descricao;
        this.penalidadeAPPercentual = penalidadeAPPercentual;
        this.penalidadeEsquivaPercentual = penalidadeEsquivaPercentual;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getPenalidadeAPPercentual() {
        return penalidadeAPPercentual;
    }

    public double getPenalidadeEsquivaPercentual() {
        return penalidadeEsquivaPercentual;
    }
}
