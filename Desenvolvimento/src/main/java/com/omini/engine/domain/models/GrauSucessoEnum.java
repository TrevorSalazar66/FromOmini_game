package com.omini.engine.domain.models;

/**
 * Representa os 5 graus de sucesso possíveis na resolução de qualquer teste dinâmico.
 */
public enum GrauSucessoEnum {
    FALHA_CRITICA("Falha Crítica", -0.50),
    FALHA("Falha", -0.15),
    SUCESSO_PARCIAL("Sucesso Parcial", 0.00),
    SUCESSO_TOTAL("Sucesso Total", 0.20),
    SUCESSO_EXTREMO("Sucesso Extremo", Double.MAX_VALUE);

    private final String descricao;
    private final double limiarSuperior;

    GrauSucessoEnum(String descricao, double limiarSuperior) {
        this.descricao = descricao;
        this.limiarSuperior = limiarSuperior;
    }

    public String getDescricao() {
        return descricao;
    }

    public double getLimiarSuperior() {
        return limiarSuperior;
    }
}
