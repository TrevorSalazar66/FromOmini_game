package com.omini.engine.domain.usecases;

import com.omini.engine.domain.dtos.BodyPartStateVO;
import com.omini.engine.domain.ecs.Entity;
import com.omini.engine.domain.ecs.components.AnatomyComponent;
import com.omini.engine.domain.ecs.components.PainFocusComponent;
import com.omini.engine.domain.models.TipoTratamentoEnum;

import java.util.Optional;

/**
 * Caso de Uso responsável por aplicar procedimentos de primeiros socorros em membros e ferimentos corporais.
 */
public final class TratarFerimentoUseCase {

    public boolean executar(Entity paciente, String membroId, TipoTratamentoEnum tipo, double potenciaTratamento) {
        if (paciente == null || tipo == null) {
            throw new IllegalArgumentException("Paciente e tipo de tratamento não podem ser nulos.");
        }

        if (tipo == TipoTratamentoEnum.ANALGESICO) {
            Optional<PainFocusComponent> painOpt = paciente.getComponent(PainFocusComponent.class);
            if (painOpt.isPresent()) {
                painOpt.get().administrarAnalgesico(potenciaTratamento);
                return true;
            }
            return false;
        }

        AnatomyComponent anatomy = paciente.getComponent(AnatomyComponent.class)
                .orElseThrow(() -> new IllegalArgumentException("O paciente não possui AnatomyComponent."));

        if (membroId == null) {
            return false;
        }

        Optional<BodyPartStateVO> parteOpt = anatomy.getParte(membroId);
        if (parteOpt.isEmpty()) {
            return false;
        }

        BodyPartStateVO parte = parteOpt.get();

        switch (tipo) {
            case CURATIVO_SANGRAMENTO -> {
                if (parte.getSangramentoPorTurno() > 0.0 || parte.getTaxaSangramentoOriginal() > 0.0) {
                    BodyPartStateVO enfaixada = parte.comCurativo();
                    anatomy.atualizarParte(enfaixada);
                    return true;
                }
                return false;
            }
            case TALA_FRATURA -> {
                if (parte.isFraturado() && !parte.isImobilizadoComTala()) {
                    BodyPartStateVO comTala = parte.comTala();
                    anatomy.atualizarParte(comTala);
                    // Imobilizar fratura com tala reduz dor aguda
                    paciente.getComponent(PainFocusComponent.class).ifPresent(p -> p.reduzirDor(10.0));
                    return true;
                }
                return false;
            }
            default -> {
                return false;
            }
        }
    }

    public boolean executar(Entity paciente, String membroId, TipoTratamentoEnum tipo) {
        return executar(paciente, membroId, tipo, 30.0);
    }
}
