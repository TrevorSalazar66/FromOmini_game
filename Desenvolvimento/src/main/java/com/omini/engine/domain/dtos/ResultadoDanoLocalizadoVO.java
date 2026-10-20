package com.omini.engine.domain.dtos;

/**
 * Value Object com o resultado detalhado da resolução de um dano localizado em um membro.
 */
public final class ResultadoDanoLocalizadoVO {

    private final String membroId;
    private final String nomeMembro;
    private final double danoAplicado;
    private final double hpRestante;
    private final boolean causouFratura;
    private final boolean causouMorte;
    private final double sangramentoGerado;
    private final double dorGerada;

    public ResultadoDanoLocalizadoVO(String membroId,
                                     String nomeMembro,
                                     double danoAplicado,
                                     double hpRestante,
                                     boolean causouFratura,
                                     boolean causouMorte,
                                     double sangramentoGerado,
                                     double dorGerada) {
        this.membroId = membroId;
        this.nomeMembro = nomeMembro;
        this.danoAplicado = danoAplicado;
        this.hpRestante = hpRestante;
        this.causouFratura = causouFratura;
        this.causouMorte = causouMorte;
        this.sangramentoGerado = sangramentoGerado;
        this.dorGerada = dorGerada;
    }

    public String getMembroId() {
        return membroId;
    }

    public String getNomeMembro() {
        return nomeMembro;
    }

    public double getDanoAplicado() {
        return danoAplicado;
    }

    public double getHpRestante() {
        return hpRestante;
    }

    public boolean isCausouFratura() {
        return causouFratura;
    }

    public boolean isCausouMorte() {
        return causouMorte;
    }

    public double getSangramentoGerado() {
        return sangramentoGerado;
    }

    public double getDorGerada() {
        return dorGerada;
    }

    @Override
    public String toString() {
        return "ResultadoDanoLocalizadoVO{" +
                "membro='" + nomeMembro + '\'' +
                ", dano=" + String.format("%.1f", danoAplicado) +
                ", hpRestante=" + String.format("%.1f", hpRestante) +
                ", fraturou=" + causouFratura +
                ", fatal=" + causouMorte +
                ", sangramento=" + String.format("%.1f", sangramentoGerado) +
                ", dor=" + String.format("%.1f", dorGerada) +
                '}';
    }
}
