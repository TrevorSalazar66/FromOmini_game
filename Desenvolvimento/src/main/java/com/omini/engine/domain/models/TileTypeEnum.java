package com.omini.engine.domain.models;

/**
 * Tipos de terreno que compõem a grade espacial do mundo.
 */
public enum TileTypeEnum {
    CHAO_PEDRA("Chão de Pedra", true, false, 1.0, "stone_granite"),
    PAREDE_GRANITO("Parede de Granito", false, true, 999.0, "stone_granite"),
    PORTA_FECHADA("Porta de Madeira Fechada", false, true, 999.0, "wood_hard"),
    PORTA_ABERTA("Porta Aberta", true, false, 1.0, "wood_hard"),
    AGUA_RASA("Água Rasa", true, false, 2.5, "cloth"),
    GRAMA("Grama Alta", true, false, 1.2, "wood_soft");

    private final String nome;
    private final boolean passavel;
    private final boolean bloqueiaVisao;
    private final double custoMovimento;
    private final String materialId;

    TileTypeEnum(String nome, boolean passavel, boolean bloqueiaVisao, double custoMovimento, String materialId) {
        this.nome = nome;
        this.passavel = passavel;
        this.bloqueiaVisao = bloqueiaVisao;
        this.custoMovimento = custoMovimento;
        this.materialId = materialId;
    }

    public String getNome() {
        return nome;
    }

    public boolean isPassavel() {
        return passavel;
    }

    public boolean isBloqueiaVisao() {
        return bloqueiaVisao;
    }

    public double getCustoMovimento() {
        return custoMovimento;
    }

    public String getMaterialId() {
        return materialId;
    }
}
