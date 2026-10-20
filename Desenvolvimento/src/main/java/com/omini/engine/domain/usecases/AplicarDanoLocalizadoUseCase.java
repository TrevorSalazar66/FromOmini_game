package com.omini.engine.domain.usecases;

import com.omini.engine.domain.dtos.BodyPartStateVO;
import com.omini.engine.domain.dtos.ResultadoDanoLocalizadoVO;
import com.omini.engine.domain.ecs.Entity;
import com.omini.engine.domain.ecs.components.AnatomyComponent;
import com.omini.engine.domain.ecs.components.PainFocusComponent;

/**
 * Caso de Uso responsável por aplicar impacto e ferimentos localizados a membros de uma entidade viva,
 * gerenciando integridade física, fraturas, sangramento e morte de membros vitais.
 */
public final class AplicarDanoLocalizadoUseCase {

    public ResultadoDanoLocalizadoVO executar(Entity alvo, String membroId, double danoBruto, String danoTipoId) {
        if (alvo == null) {
            throw new IllegalArgumentException("A entidade alvo não pode ser nula.");
        }
        if (danoBruto < 0.0) {
            throw new IllegalArgumentException("O dano bruto não pode ser negativo: " + danoBruto);
        }

        AnatomyComponent anatomy = alvo.getComponent(AnatomyComponent.class)
                .orElseThrow(() -> new IllegalArgumentException("A entidade '" + alvo.getName() + "' não possui AnatomyComponent."));

        String idAlvoMembro = membroId != null ? membroId.trim().toLowerCase() : "torso";
        BodyPartStateVO parte = anatomy.getParte(idAlvoMembro)
                .orElseGet(() -> anatomy.getParte("torso")
                        .orElseThrow(() -> new IllegalStateException("Anatomia sem membro válido e sem tronco.")));

        boolean jaEstavaMorta = anatomy.isMorto();
        boolean jaEstavaFraturado = parte.isFraturado();

        // 1. Aplicação do dano ao membro
        BodyPartStateVO novaParte = parte.comDano(danoBruto);
        anatomy.atualizarParte(novaParte);

        boolean causouFratura = !jaEstavaFraturado && novaParte.isFraturado();
        boolean causouMorte = !jaEstavaMorta && anatomy.isMorto();
        if (causouMorte) {
            alvo.addTag("morto");
        }

        // 2. Acúmulo de dor e quebra de foco
        double dorGerada = (danoBruto * 0.8) + (causouFratura ? 15.0 : 0.0);
        alvo.getComponent(PainFocusComponent.class).ifPresent(pain -> {
            pain.adicionarDor(dorGerada);
        });

        double sangramentoGerado = novaParte.getSangramentoPorTurno();

        return new ResultadoDanoLocalizadoVO(
                novaParte.getPartId(),
                novaParte.getName(),
                danoBruto,
                novaParte.getHpAtual(),
                causouFratura,
                causouMorte,
                sangramentoGerado,
                dorGerada
        );
    }
}
