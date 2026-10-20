package com.omini.engine.domain.ecs.components;

import com.omini.engine.domain.ecs.Component;

/**
 * Componente que atrela propriedades de campo de superfície volátil a uma entidade do mapa
 * (ex.: poça de água, fogo, gás inflamável, fumaça densa ou choque elétrico).
 */
public final class SurfaceFieldComponent implements Component {

    private final String fieldTag;
    private int turnosRestantes;
    private double intensidade;

    public SurfaceFieldComponent(String fieldTag, int turnosRestantes, double intensidade) {
        if (fieldTag == null || fieldTag.trim().isEmpty()) {
            throw new IllegalArgumentException("A tag do campo não pode ser nula ou vazia.");
        }
        this.fieldTag = fieldTag.trim().toLowerCase();
        this.turnosRestantes = Math.max(0, turnosRestantes);
        this.intensidade = Math.max(0.0, intensidade);
    }

    public SurfaceFieldComponent(String fieldTag, int turnosRestantes) {
        this(fieldTag, turnosRestantes, 1.0);
    }

    public String getFieldTag() {
        return fieldTag;
    }

    public int getTurnosRestantes() {
        return turnosRestantes;
    }

    public double getIntensidade() {
        return intensidade;
    }

    public void setIntensidade(double intensidade) {
        this.intensidade = Math.max(0.0, intensidade);
    }

    public void decrementarTurno() {
        if (turnosRestantes > 0) {
            turnosRestantes--;
        }
    }

    public boolean isExpirado() {
        return turnosRestantes <= 0;
    }

    @Override
    public String toString() {
        return "SurfaceFieldComponent{" +
                "tag='" + fieldTag + '\'' +
                ", turnos=" + turnosRestantes +
                ", intensidade=" + intensidade +
                '}';
    }
}
