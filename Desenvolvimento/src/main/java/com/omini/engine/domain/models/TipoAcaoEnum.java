package com.omini.engine.domain.models;

/**
 * Enumeração das ações táticas possíveis com seus custos base de Pontos de Ação (AP).
 */
public enum TipoAcaoEnum {
    MOVER("Movimento Espacial", 100),
    ATACAR("Golpe de Combate", 120),
    USAR_ITEM("Utilizar Item / Medicamento", 80),
    RECARREGAR("Recarregar Equipamento", 150),
    ESPERAR("Passar Turno / Descansar", 100);

    private final String descricao;
    private final int custoBaseAP;

    TipoAcaoEnum(String descricao, int custoBaseAP) {
        this.descricao = descricao;
        this.custoBaseAP = custoBaseAP;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getCustoBaseAP() {
        return custoBaseAP;
    }
}
