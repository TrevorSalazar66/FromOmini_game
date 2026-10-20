package com.omini.engine.domain.models;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Modelo de domínio imutável que representa a definição de um template de item no jogo.
 */
public final class ItemModel {

    private final String id;
    private final String name;
    private final String description;
    private final String category;
    private final int widthSlots;
    private final int heightSlots;
    private final String materialId;
    private final Double weightKgOverride;
    private final boolean canRotate;
    private final String equipSlot;
    private final Map<String, Object> properties;

    public ItemModel(String id,
                     String name,
                     String description,
                     String category,
                     int widthSlots,
                     int heightSlots,
                     String materialId,
                     Double weightKgOverride,
                     boolean canRotate,
                     String equipSlot,
                     Map<String, Object> properties) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("O ID do item não pode ser nulo ou vazio.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do item não pode ser nulo ou vazio.");
        }
        if (widthSlots <= 0 || heightSlots <= 0) {
            throw new IllegalArgumentException("As dimensões dos slots devem ser estritamente positivas.");
        }
        this.id = id.trim();
        this.name = name.trim();
        this.description = description != null ? description.trim() : "";
        this.category = category != null ? category.trim().toLowerCase() : "misc";
        this.widthSlots = widthSlots;
        this.heightSlots = heightSlots;
        this.materialId = materialId != null ? materialId.trim() : "generic";
        this.weightKgOverride = weightKgOverride;
        this.canRotate = canRotate;
        this.equipSlot = equipSlot;
        this.properties = properties != null ? Collections.unmodifiableMap(properties) : Collections.emptyMap();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public int getWidthSlots() {
        return widthSlots;
    }

    public int getHeightSlots() {
        return heightSlots;
    }

    public int getAreaSlots() {
        return widthSlots * heightSlots;
    }

    public String getMaterialId() {
        return materialId;
    }

    public Optional<Double> getWeightKgOverride() {
        return Optional.ofNullable(weightKgOverride);
    }

    public boolean isCanRotate() {
        return canRotate;
    }

    public Optional<String> getEquipSlot() {
        return Optional.ofNullable(equipSlot);
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemModel itemModel = (ItemModel) o;
        return Objects.equals(id, itemModel.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ItemModel{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", slots=" + widthSlots + "x" + heightSlots +
                ", material='" + materialId + '\'' +
                '}';
    }
}
