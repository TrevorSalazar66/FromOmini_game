package com.omini.engine.domain.usecases;

import com.omini.engine.domain.dtos.EstadoDorVO;
import com.omini.engine.domain.ecs.components.PainFocusComponent;

/**
 * Caso de Uso responsável por calcular o impacto da dor nos testes e o teto máximo de Foco da criatura.
 */
public final class CalcularPenalidadesDorUseCase {

    public EstadoDorVO executar(PainFocusComponent painFocus) {
        if (painFocus == null) {
            throw new IllegalArgumentException("O componente de Dor e Foco não pode ser nulo.");
        }

        double dorReal = painFocus.getDorReal();
        double analgesico = painFocus.getNivelAnalgesico();
        double dorEfetiva = painFocus.getDorEfetiva();
        double tetoFoco = painFocus.getTetoFoco();

        String classificacao;
        double penalidade;

        if (dorEfetiva <= 10.0) {
            classificacao = "Sem Dor / Confortável";
            penalidade = 0.0;
        } else if (dorEfetiva <= 30.0) {
            classificacao = "Dor Leve";
            penalidade = 0.05;
        } else if (dorEfetiva <= 60.0) {
            classificacao = "Dor Moderada";
            penalidade = 0.15;
        } else if (dorEfetiva <= 85.0) {
            classificacao = "Dor Severa";
            penalidade = 0.30;
        } else {
            classificacao = "Dor Agonizante / Colapso";
            penalidade = 0.50;
        }

        return new EstadoDorVO(dorReal, analgesico, dorEfetiva, tetoFoco, classificacao, penalidade);
    }
}
