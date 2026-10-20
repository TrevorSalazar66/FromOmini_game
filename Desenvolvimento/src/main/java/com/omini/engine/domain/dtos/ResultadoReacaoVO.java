package com.omini.engine.domain.dtos;

import com.omini.engine.domain.ecs.Entity;

import java.util.Collections;
import java.util.List;

/**
 * Value Object com o resultado da resolução de reações químicas/elementais no mapa.
 */
public final class ResultadoReacaoVO {

    private final boolean ocorreuReacao;
    private final String acaoRealizada;
    private final String descricao;
    private final List<Entity> entidadesRemovidas;
    private final List<Entity> novasEntidadesGeradas;
    private final int poderExplosao;
    private final double danoPorTurno;

    public ResultadoReacaoVO(boolean ocorreuReacao,
                             String acaoRealizada,
                             String descricao,
                             List<Entity> entidadesRemovidas,
                             List<Entity> novasEntidadesGeradas,
                             int poderExplosao,
                             double danoPorTurno) {
        this.ocorreuReacao = ocorreuReacao;
        this.acaoRealizada = acaoRealizada != null ? acaoRealizada : "none";
        this.descricao = descricao != null ? descricao : "";
        this.entidadesRemovidas = entidadesRemovidas != null ? Collections.unmodifiableList(entidadesRemovidas) : Collections.emptyList();
        this.novasEntidadesGeradas = novasEntidadesGeradas != null ? Collections.unmodifiableList(novasEntidadesGeradas) : Collections.emptyList();
        this.poderExplosao = poderExplosao;
        this.danoPorTurno = danoPorTurno;
    }

    public static ResultadoReacaoVO semReacao() {
        return new ResultadoReacaoVO(false, "none", "Nenhuma reação química detectada.", Collections.emptyList(), Collections.emptyList(), 0, 0.0);
    }

    public boolean isOcorreuReacao() {
        return ocorreuReacao;
    }

    public String getAcaoRealizada() {
        return acaoRealizada;
    }

    public String getAcao() {
        return acaoRealizada;
    }

    public String getDescricao() {
        return descricao;
    }

    public List<Entity> getEntidadesRemovidas() {
        return entidadesRemovidas;
    }

    public List<Entity> getNovasEntidadesGeradas() {
        return novasEntidadesGeradas;
    }

    public List<Entity> getEntidadesGeradas() {
        return novasEntidadesGeradas;
    }

    public int getPoderExplosao() {
        return poderExplosao;
    }

    public int getExplosionPower() {
        return poderExplosao;
    }

    public double getDanoPorTurno() {
        return danoPorTurno;
    }

    public double getDamagePerTurn() {
        return danoPorTurno;
    }

    @Override
    public String toString() {
        return "ResultadoReacaoVO{" +
                "ocorreu=" + ocorreuReacao +
                ", acao='" + acaoRealizada + '\'' +
                ", removidas=" + entidadesRemovidas.size() +
                ", geradas=" + novasEntidadesGeradas.size() +
                ", explosao=" + poderExplosao +
                '}';
    }
}
