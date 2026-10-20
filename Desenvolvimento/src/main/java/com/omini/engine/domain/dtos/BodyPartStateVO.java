package com.omini.engine.domain.dtos;

import java.util.Objects;

/**
 * Value Object imutável que representa o estado fisiológico instantâneo de um membro corporal.
 */
public final class BodyPartStateVO {

    private final String partId;
    private final String name;
    private final double hpAtual;
    private final double hpMaximo;
    private final boolean vital;
    private final boolean fraturado;
    private final double sangramentoPorTurno;
    private final boolean enfaixado;
    private final boolean imobilizadoComTala;

    public BodyPartStateVO(String partId,
                           String name,
                           double hpAtual,
                           double hpMaximo,
                           boolean vital,
                           boolean fraturado,
                           double sangramentoPorTurno,
                           boolean enfaixado,
                           boolean imobilizadoComTala) {
        if (partId == null || partId.trim().isEmpty()) {
            throw new IllegalArgumentException("O ID da parte corporal não pode ser nulo ou vazio.");
        }
        if (hpMaximo <= 0.0) {
            throw new IllegalArgumentException("O HP máximo do membro deve ser positivo.");
        }
        this.partId = partId.trim().toLowerCase();
        this.name = name != null ? name.trim() : partId;
        this.hpAtual = Math.max(0.0, Math.min(hpAtual, hpMaximo));
        this.hpMaximo = hpMaximo;
        this.vital = vital;
        this.fraturado = fraturado || (this.hpAtual <= 0.0);
        this.sangramentoPorTurno = Math.max(0.0, sangramentoPorTurno);
        this.enfaixado = enfaixado;
        this.imobilizadoComTala = imobilizadoComTala;
    }

    public BodyPartStateVO(String partId,
                           String name,
                           double hpAtual,
                           double hpMaximo,
                           boolean vital,
                           boolean fraturado,
                           boolean imobilizadoComTala,
                           double sangramentoPorTurno) {
        this(partId, name, hpAtual, hpMaximo, vital, fraturado, sangramentoPorTurno, false, imobilizadoComTala);
    }

    public static BodyPartStateVO criarSaudavel(String partId, String name, double hpMaximo, boolean vital) {
        return new BodyPartStateVO(partId, name, hpMaximo, hpMaximo, vital, false, 0.0, false, false);
    }

    public String getPartId() {
        return partId;
    }

    public String getName() {
        return name;
    }

    public double getHpAtual() {
        return hpAtual;
    }

    public double getHpMaximo() {
        return hpMaximo;
    }

    public boolean isVital() {
        return vital;
    }

    public boolean isFraturado() {
        return fraturado;
    }

    public double getSangramentoPorTurno() {
        return enfaixado ? 0.0 : sangramentoPorTurno;
    }

    public double getTaxaSangramentoOriginal() {
        return sangramentoPorTurno;
    }

    public boolean isEnfaixado() {
        return enfaixado;
    }

    public boolean isImobilizadoComTala() {
        return imobilizadoComTala;
    }

    public boolean isDestruido() {
        return hpAtual <= 0.0;
    }

    public double getPercentualHp() {
        return hpMaximo > 0.0 ? (hpAtual / hpMaximo) : 0.0;
    }

    public BodyPartStateVO comDano(double dano) {
        double novoHp = Math.max(0.0, this.hpAtual - dano);
        boolean novaFratura = this.fraturado || (novoHp <= 0.0);
        // Dano físico significativo pode abrir sangramento
        double novoSangramento = this.sangramentoPorTurno + (dano > 5.0 ? (dano * 0.15) : 0.0);
        return new BodyPartStateVO(
                this.partId,
                this.name,
                novoHp,
                this.hpMaximo,
                this.vital,
                novaFratura,
                novoSangramento,
                false, // ferimento novo rompe ataduras prévias
                this.imobilizadoComTala
        );
    }

    public BodyPartStateVO comCurativo() {
        return new BodyPartStateVO(
                this.partId,
                this.name,
                this.hpAtual,
                this.hpMaximo,
                this.vital,
                this.fraturado,
                0.0,
                true,
                this.imobilizadoComTala
        );
    }

    public BodyPartStateVO comTala() {
        return new BodyPartStateVO(
                this.partId,
                this.name,
                this.hpAtual,
                this.hpMaximo,
                this.vital,
                this.fraturado,
                this.sangramentoPorTurno,
                this.enfaixado,
                true
        );
    }

    public BodyPartStateVO comCura(double valorCura) {
        double novoHp = Math.min(this.hpMaximo, this.hpAtual + valorCura);
        boolean fraturaMantida = novoHp < this.hpMaximo * 0.50 ? this.fraturado : false;
        return new BodyPartStateVO(
                this.partId,
                this.name,
                novoHp,
                this.hpMaximo,
                this.vital,
                fraturaMantida,
                this.sangramentoPorTurno,
                this.enfaixado,
                this.imobilizadoComTala
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BodyPartStateVO that = (BodyPartStateVO) o;
        return Objects.equals(partId, that.partId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(partId);
    }

    @Override
    public String toString() {
        return "BodyPartStateVO{" +
                "part='" + partId + '\'' +
                ", hp=" + String.format("%.1f/%.1f", hpAtual, hpMaximo) +
                ", fraturado=" + fraturado +
                ", sangramento=" + getSangramentoPorTurno() +
                ", enfaixado=" + enfaixado +
                '}';
    }
}
