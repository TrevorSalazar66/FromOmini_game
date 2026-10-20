package com.omini.engine.domain.ecs.components;

import com.omini.engine.domain.ecs.Component;

/**
 * Componente que atrela as propriedades físicas de matéria a uma entidade (item, criatura, parede).
 */
public final class MaterialComponent implements Component {

    private final String materialId;
    private final double density;
    private final double hardness;

    public MaterialComponent(String materialId, double density, double hardness) {
        if (materialId == null || materialId.trim().isEmpty()) {
            throw new IllegalArgumentException("O ID do material não pode ser nulo ou vazio.");
        }
        if (density <= 0.0) {
            throw new IllegalArgumentException("A densidade do material deve ser estritamente positiva.");
        }
        if (hardness < 0.0) {
            throw new IllegalArgumentException("A dureza do material não pode ser negativa.");
        }
        this.materialId = materialId.trim();
        this.density = density;
        this.hardness = hardness;
    }

    public String getMaterialId() {
        return materialId;
    }

    public double getDensity() {
        return density;
    }

    public double getHardness() {
        return hardness;
    }

    @Override
    public String toString() {
        return "MaterialComponent{" +
                "materialId='" + materialId + '\'' +
                ", density=" + density +
                ", hardness=" + hardness +
                '}';
    }
}
