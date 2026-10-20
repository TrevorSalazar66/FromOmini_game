package com.omini.engine.domain.usecases;

import com.omini.engine.domain.dtos.InteracaoAlvoDTO;

/**
 * Caso de Uso: Calcula o valor da Classe de Dificuldade (Alvo) dinâmica de uma interação.
 * Fórmula: Alvo = (DurezaMaterial * NivelAlvo) / MultiplicadorFerramenta
 */
public final class CalcularAlvoDificuldadeUseCase {

    public double executar(InteracaoAlvoDTO parametros) {
        if (parametros == null) {
            throw new IllegalArgumentException("Os parâmetros da interação não podem ser nulos.");
        }

        double resistenciaTotal = parametros.durezaMaterial() * parametros.nivelAlvo();
        return resistenciaTotal / parametros.multiplicadorFerramenta();
    }
}
