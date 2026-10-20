package com.omini.engine.domain.models;

import java.util.Objects;

/**
 * Modelo de domínio imutável que define uma parte do corpo e seus parâmetros anatômicos.
 */
public final class BodyPartDefinitionModel {

    private final String id;
    private final String name;
    private final int hitWeight;
    private final boolean vital;
    private final double baseHpRatio;
    private final String armorSlot;

    public BodyPartDefinitionModel(String id, String name, int hitWeight, boolean vital, double baseHpRatio, String armorSlot) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("O ID da parte corporal não pode ser nulo ou vazio.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome da parte corporal não pode ser nulo ou vazio.");
        }
        if (hitWeight <= 0) {
            throw new IllegalArgumentException("O peso de acerto (hit_weight) deve ser positivo. Recebido: " + hitWeight);
        }
        if (baseHpRatio <= 0.0) {
            throw new IllegalArgumentException("A razão de HP base deve ser positiva. Recebida: " + baseHpRatio);
        }
        this.id = id.trim();
        this.name = name.trim();
        this.hitWeight = hitWeight;
        this.vital = vital;
        this.baseHpRatio = baseHpRatio;
        this.armorSlot = armorSlot != null ? armorSlot.trim().toLowerCase() : "none";
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getHitWeight() {
        return hitWeight;
    }

    public boolean isVital() {
        return vital;
    }

    public double getBaseHpRatio() {
        return baseHpRatio;
    }

    public String getArmorSlot() {
        return armorSlot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BodyPartDefinitionModel that = (BodyPartDefinitionModel) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "BodyPartDefinitionModel{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", hitWeight=" + hitWeight +
                ", vital=" + vital +
                ", hpRatio=" + baseHpRatio +
                ", armorSlot='" + armorSlot + '\'' +
                '}';
    }
}
