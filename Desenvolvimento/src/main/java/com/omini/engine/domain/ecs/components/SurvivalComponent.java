package com.omini.engine.domain.ecs.components;

import com.omini.engine.domain.ecs.Component;

/**
 * Componente que gerencia os parâmetros vitais do ciclo de sobrevivência básica:
 * Fome, Sede, Estamina e Sono.
 */
public final class SurvivalComponent implements Component {

    private double fome;      // 0.0 (saciado) a 100.0 (inanição)
    private double sede;      // 0.0 (hidratado) a 100.0 (desidratação fatal)
    private double estamina;  // 100.0 (vigor pleno) a 0.0 (exaustão física)
    private double sono;      // 0.0 (desperto) a 100.0 (privação severa)

    public SurvivalComponent(double fome, double sede, double estamina, double sono) {
        this.fome = Math.max(0.0, Math.min(100.0, fome));
        this.sede = Math.max(0.0, Math.min(100.0, sede));
        this.estamina = Math.max(0.0, Math.min(100.0, estamina));
        this.sono = Math.max(0.0, Math.min(100.0, sono));
    }

    public SurvivalComponent() {
        this(0.0, 0.0, 100.0, 0.0);
    }

    public double getFome() {
        return fome;
    }

    public double getSede() {
        return sede;
    }

    public double getEstamina() {
        return estamina;
    }

    public double getSono() {
        return sono;
    }

    public void consumirEstamina(double quantidade) {
        if (quantidade > 0.0) {
            this.estamina = Math.max(0.0, this.estamina - quantidade);
        }
    }

    public void recuperarEstamina(double quantidade) {
        if (quantidade > 0.0) {
            this.estamina = Math.min(100.0, this.estamina + quantidade);
        }
    }

    public void incrementarFome(double taxa) {
        if (taxa > 0.0) {
            this.fome = Math.min(100.0, this.fome + taxa);
        }
    }

    public void alimentar(double nutricao) {
        if (nutricao > 0.0) {
            this.fome = Math.max(0.0, this.fome - nutricao);
        }
    }

    public void incrementarSede(double taxa) {
        if (taxa > 0.0) {
            this.sede = Math.min(100.0, this.sede + taxa);
        }
    }

    public void hidratar(double quantidade) {
        if (quantidade > 0.0) {
            this.sede = Math.max(0.0, this.sede - quantidade);
        }
    }

    public void incrementarSono(double taxa) {
        if (taxa > 0.0) {
            this.sono = Math.min(100.0, this.sono + taxa);
        }
    }

    public void dormir(double descanso) {
        if (descanso > 0.0) {
            this.sono = Math.max(0.0, this.sono - descanso);
        }
    }

    public boolean isExausto() {
        return estamina <= 5.0;
    }

    public boolean isFamintoCritico() {
        return fome >= 85.0;
    }

    public boolean isDesidratadoCritico() {
        return sede >= 85.0;
    }

    public boolean isPrivadoDeSonoCritico() {
        return sono >= 85.0;
    }

    @Override
    public String toString() {
        return "SurvivalComponent{" +
                "estamina=" + String.format("%.1f%%", estamina) +
                ", sede=" + String.format("%.1f%%", sede) +
                ", fome=" + String.format("%.1f%%", fome) +
                ", sono=" + String.format("%.1f%%", sono) +
                '}';
    }
}
