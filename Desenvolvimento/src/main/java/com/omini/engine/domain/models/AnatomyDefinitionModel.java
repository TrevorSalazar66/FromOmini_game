package com.omini.engine.domain.models;

import java.util.*;

/**
 * Modelo de domínio imutável que representa a estrutura anatômica completa de um tipo de ser vivo (ex.: Humanoide).
 */
public final class AnatomyDefinitionModel {

    private final String id;
    private final String name;
    private final Map<String, BodyPartDefinitionModel> bodyParts;

    public AnatomyDefinitionModel(String id, String name, Map<String, BodyPartDefinitionModel> bodyParts) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("O ID da anatomia não pode ser nulo ou vazio.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome da anatomia não pode ser nulo ou vazio.");
        }
        if (bodyParts == null || bodyParts.isEmpty()) {
            throw new IllegalArgumentException("A anatomia deve conter ao menos uma parte do corpo.");
        }
        this.id = id.trim();
        this.name = name.trim();
        this.bodyParts = Collections.unmodifiableMap(new LinkedHashMap<>(bodyParts));
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Optional<BodyPartDefinitionModel> getBodyPart(String partId) {
        if (partId == null) return Optional.empty();
        return Optional.ofNullable(bodyParts.get(partId.trim().toLowerCase()));
    }

    public Collection<BodyPartDefinitionModel> getAllBodyParts() {
        return bodyParts.values();
    }

    public Map<String, BodyPartDefinitionModel> getBodyPartsMap() {
        return bodyParts;
    }

    public int getTotalHitWeight() {
        int total = 0;
        for (BodyPartDefinitionModel part : bodyParts.values()) {
            total += part.getHitWeight();
        }
        return total;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnatomyDefinitionModel that = (AnatomyDefinitionModel) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AnatomyDefinitionModel{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", totalParts=" + bodyParts.size() +
                ", totalWeight=" + getTotalHitWeight() +
                '}';
    }
}
