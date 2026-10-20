package com.omini.engine.domain.models;

import java.util.Collections;
import java.util.Map;

/**
 * Entidade de domínio imutável representando um material físico do jogo.
 */
public final class MaterialModel {

    private final String id;
    private final String nome;
    private final double densidade;
    private final double durezaBase;
    private final boolean inflamavel;
    private final Map<String, Double> modificadoresDano;

    public MaterialModel(
        String id,
        String nome,
        double densidade,
        double durezaBase,
        boolean inflamavel,
        Map<String, Double> modificadoresDano
    ) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("O identificador do material não pode ser nulo ou vazio.");
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do material não pode ser nulo ou vazio.");
        }
        if (densidade <= 0) {
            throw new IllegalArgumentException("A densidade do material deve ser positiva. Recebido: " + densidade);
        }
        if (durezaBase <= 0) {
            throw new IllegalArgumentException("A dureza base do material deve ser positiva. Recebido: " + durezaBase);
        }

        this.id = id;
        this.nome = nome;
        this.densidade = densidade;
        this.durezaBase = durezaBase;
        this.inflamavel = inflamavel;
        this.modificadoresDano = modificadoresDano != null
            ? Collections.unmodifiableMap(modificadoresDano)
            : Collections.emptyMap();
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public double getDensidade() {
        return densidade;
    }

    public double getDurezaBase() {
        return durezaBase;
    }

    public boolean isInflamavel() {
        return inflamavel;
    }

    public double getModificadorDano(String tipoDanoId) {
        return modificadoresDano.getOrDefault(tipoDanoId, 1.0);
    }

    public Map<String, Double> getModificadoresDano() {
        return modificadoresDano;
    }
}
