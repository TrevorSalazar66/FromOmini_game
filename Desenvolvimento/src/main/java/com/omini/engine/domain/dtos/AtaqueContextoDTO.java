package com.omini.engine.domain.dtos;

import com.omini.engine.domain.models.AnatomyDefinitionModel;

/**
 * DTO que encapsula o contexto de um ataque para fins de sorteio estocástico do membro atingido.
 */
public final class AtaqueContextoDTO {

    private final AnatomyDefinitionModel anatomia;
    private final boolean coberturaMembrosInferiores;
    private final String membroMiradoId;
    private final double focoGasto;

    public AtaqueContextoDTO(AnatomyDefinitionModel anatomia,
                             boolean coberturaMembrosInferiores,
                             String membroMiradoId,
                             double focoGasto) {
        if (anatomia == null) {
            throw new IllegalArgumentException("A anatomia do alvo não pode ser nula.");
        }
        this.anatomia = anatomia;
        this.coberturaMembrosInferiores = coberturaMembrosInferiores;
        this.membroMiradoId = membroMiradoId != null ? membroMiradoId.trim().toLowerCase() : null;
        this.focoGasto = Math.max(0.0, focoGasto);
    }

    public static AtaqueContextoDTO padrao(AnatomyDefinitionModel anatomia) {
        return new AtaqueContextoDTO(anatomia, false, null, 0.0);
    }

    public AnatomyDefinitionModel getAnatomia() {
        return anatomia;
    }

    public boolean isCoberturaMembrosInferiores() {
        return coberturaMembrosInferiores;
    }

    public String getMembroMiradoId() {
        return membroMiradoId;
    }

    public double getFocoGasto() {
        return focoGasto;
    }
}
