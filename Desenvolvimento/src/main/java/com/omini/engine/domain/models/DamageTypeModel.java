package com.omini.engine.domain.models;

import java.util.Collections;
import java.util.Map;

/**
 * Entidade de domínio imutável representando um tipo de dano no motor.
 */
public final class DamageTypeModel {

    private final String id;
    private final String nome;
    private final String visualFx;
    private final Map<String, Double> multiplicadoresTags;

    public DamageTypeModel(
        String id,
        String nome,
        String visualFx,
        Map<String, Double> multiplicadoresTags
    ) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("O id do tipo de dano não pode ser nulo ou vazio.");
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do tipo de dano não pode ser nulo ou vazio.");
        }

        this.id = id;
        this.nome = nome;
        this.visualFx = visualFx != null ? visualFx : "";
        this.multiplicadoresTags = multiplicadoresTags != null
            ? Collections.unmodifiableMap(multiplicadoresTags)
            : Collections.emptyMap();
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getVisualFx() {
        return visualFx;
    }

    public double getMultiplicadorTag(String tagId) {
        return multiplicadoresTags.getOrDefault(tagId, 1.0);
    }

    public Map<String, Double> getMultiplicadoresTags() {
        return multiplicadoresTags;
    }
}
