package com.omini.engine.domain.usecases;

import com.omini.engine.domain.dtos.ResultadoReacaoVO;
import com.omini.engine.domain.ecs.Entity;
import com.omini.engine.domain.ecs.components.SurfaceFieldComponent;
import com.omini.engine.domain.models.EnvironmentalReactionModel;
import com.omini.engine.domain.models.TileMap;
import com.omini.engine.domain.repositories.IEnvironmentalReactionRepository;

import java.util.*;

/**
 * Caso de Uso responsável por processar as reações químicas e elementais em cadeia nos tiles do mapa,
 * consultando environmental_reactions.json e gerando novos campos (como vapor/fumaça, choque ou detonações).
 */
public final class ProcessarReacaoAmbientalUseCase {

    public ResultadoReacaoVO executar(TileMap mapa, int x, int y, IEnvironmentalReactionRepository reactionRepo) {
        if (mapa == null || reactionRepo == null || !mapa.isDentroDosLimites(x, y)) {
            return ResultadoReacaoVO.semReacao();
        }

        List<Entity> entidadesNoTile = new ArrayList<>(mapa.getEntidadesNoTile(x, y));
        if (entidadesNoTile.size() < 2) {
            return ResultadoReacaoVO.semReacao(); // É necessário ao menos 2 elementos para reagir
        }

        List<String> tagsPresentes = new ArrayList<>();
        Map<String, Entity> entidadePorTag = new HashMap<>();

        for (Entity e : entidadesNoTile) {
            e.getComponent(SurfaceFieldComponent.class).ifPresent(field -> {
                String tag = field.getFieldTag();
                tagsPresentes.add(tag);
                entidadePorTag.put(tag, e);
            });
            for (String t : e.getTags()) {
                if (!tagsPresentes.contains(t)) {
                    tagsPresentes.add(t);
                    entidadePorTag.put(t, e);
                }
            }
        }

        Optional<EnvironmentalReactionModel> reacaoOpt = reactionRepo.buscarReacaoParaTags(tagsPresentes);
        if (reacaoOpt.isEmpty()) {
            return ResultadoReacaoVO.semReacao();
        }

        EnvironmentalReactionModel reacao = reacaoOpt.get();
        List<Entity> removidas = new ArrayList<>();
        List<Entity> geradas = new ArrayList<>();

        // Identifica e remove as entidades envolvidas na reação
        for (String triggerTag : reacao.getTriggerTags()) {
            Entity envolvida = entidadePorTag.get(triggerTag);
            if (envolvida != null && !removidas.contains(envolvida)) {
                mapa.removerEntidade(x, y, envolvida);
                removidas.add(envolvida);
            }
        }

        String acao = reacao.getAction();
        if ("extinguish_and_spawn".equalsIgnoreCase(acao)) {
            // Cria campo de fumaça / vapor que bloqueia visão
            String entityId = reacao.getSpawnEntity() != null ? reacao.getSpawnEntity() : "field_smoke";
            int duracao = reacao.getDurationTurns() > 0 ? reacao.getDurationTurns() : 5;

            Entity smoke = new Entity(entityId + "_" + UUID.randomUUID(), "Nuvem de Fumaça e Vapor");
            smoke.addTag("field_smoke");
            smoke.addTag("bloqueia_visao");
            smoke.addComponent(new SurfaceFieldComponent("field_smoke", duracao));

            mapa.adicionarEntidade(x, y, smoke);
            geradas.add(smoke);
        } else if ("detonate".equalsIgnoreCase(acao)) {
            // Detonação em área: destrói os reagentes e dispara onda de choque
            // Cria resíduo de fumaça pós-explosão
            Entity residuo = new Entity("residuo_explosao_" + UUID.randomUUID(), "Fuligem de Explosão");
            residuo.addTag("field_smoke");
            residuo.addTag("bloqueia_visao");
            residuo.addComponent(new SurfaceFieldComponent("field_smoke", 3));

            mapa.adicionarEntidade(x, y, residuo);
            geradas.add(residuo);
        } else if ("electrify_pool".equalsIgnoreCase(acao)) {
            // Eletrifica a poça de água
            Entity aguaEletrica = new Entity("agua_eletrica_" + UUID.randomUUID(), "Água Eletrificada");
            aguaEletrica.addTag("surface_electric");
            aguaEletrica.addComponent(new SurfaceFieldComponent("surface_electric", 4, reacao.getDamagePerTurn()));

            mapa.adicionarEntidade(x, y, aguaEletrica);
            geradas.add(aguaEletrica);
        }

        return new ResultadoReacaoVO(
                true,
                acao,
                reacao.getDescription(),
                removidas,
                geradas,
                reacao.getExplosionPower(),
                reacao.getDamagePerTurn()
        );
    }
}
