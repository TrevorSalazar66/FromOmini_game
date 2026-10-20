package com.omini.engine.domain.dtos;

import com.omini.engine.domain.models.NivelSobrecargaEnum;

/**
 * Value Object com o estado de carga corporal de uma entidade e os modificadores aplicados.
 */
public final class EstadoSobrecargaVO {

    private final double pesoTotalCarregado;
    private final double capacidadeConfortavel;
    private final double capacidadeMaxima;
    private final double taxaCarga;
    private final boolean estaSobrecarregado;
    private final int penalidadeAPPercentual;
    private final double penalidadeEsquivaPercentual;
    private final NivelSobrecargaEnum nivelSobrecarga;

    public EstadoSobrecargaVO(double pesoTotalCarregado,
                              double capacidadeConfortavel,
                              double capacidadeMaxima,
                              double taxaCarga,
                              boolean estaSobrecarregado,
                              int penalidadeAPPercentual,
                              double penalidadeEsquivaPercentual,
                              NivelSobrecargaEnum nivelSobrecarga) {
        this.pesoTotalCarregado = pesoTotalCarregado;
        this.capacidadeConfortavel = capacidadeConfortavel;
        this.capacidadeMaxima = capacidadeMaxima;
        this.taxaCarga = taxaCarga;
        this.estaSobrecarregado = estaSobrecarregado;
        this.penalidadeAPPercentual = penalidadeAPPercentual;
        this.penalidadeEsquivaPercentual = penalidadeEsquivaPercentual;
        this.nivelSobrecarga = nivelSobrecarga;
    }

    public double getPesoTotalCarregado() {
        return pesoTotalCarregado;
    }

    public double getCapacidadeConfortavel() {
        return capacidadeConfortavel;
    }

    public double getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public double getTaxaCarga() {
        return taxaCarga;
    }

    public boolean isEstaSobrecarregado() {
        return estaSobrecarregado;
    }

    public int getPenalidadeAPPercentual() {
        return penalidadeAPPercentual;
    }

    public double getPenalidadeEsquivaPercentual() {
        return penalidadeEsquivaPercentual;
    }

    public NivelSobrecargaEnum getNivelSobrecarga() {
        return nivelSobrecarga;
    }

    @Override
    public String toString() {
        return "EstadoSobrecargaVO{" +
                "peso=" + String.format("%.2f", pesoTotalCarregado) + "kg" +
                ", confortavel=" + String.format("%.2f", capacidadeConfortavel) + "kg" +
                ", maximo=" + String.format("%.2f", capacidadeMaxima) + "kg" +
                ", taxa=" + String.format("%.1f%%", taxaCarga * 100) +
                ", sobrecarregado=" + estaSobrecarregado +
                ", nivel=" + nivelSobrecarga +
                ", penalidadeAP=+" + penalidadeAPPercentual + "%" +
                ", penalidadeEsquiva=" + String.format("%.1f%%", penalidadeEsquivaPercentual * 100) +
                '}';
    }
}
