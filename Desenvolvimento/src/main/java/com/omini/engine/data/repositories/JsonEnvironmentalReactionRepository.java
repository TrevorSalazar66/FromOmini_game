package com.omini.engine.data.repositories;

import com.omini.engine.data.json.SimpleJsonParser;
import com.omini.engine.domain.models.EnvironmentalReactionModel;
import com.omini.engine.domain.repositories.IEnvironmentalReactionRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Repositório concreto que carrega e armazena regras de reações ambientais a partir de JSON.
 */
public final class JsonEnvironmentalReactionRepository implements IEnvironmentalReactionRepository {

    private final List<EnvironmentalReactionModel> reacoes = new ArrayList<>();

    public void carregarDeArquivo(Path caminhoArquivo) throws IOException {
        if (!Files.exists(caminhoArquivo)) {
            throw new IllegalArgumentException("O arquivo de reações ambientais não existe: " + caminhoArquivo);
        }
        String conteudo = Files.readString(caminhoArquivo);
        carregarDeJson(conteudo);
    }

    @SuppressWarnings("unchecked")
    public void carregarDeJson(String jsonConteudo) {
        Map<String, Object> raiz = SimpleJsonParser.parseObjeto(jsonConteudo);
        Object objReactions = raiz.get("reactions");
        if (!(objReactions instanceof List<?> lista)) {
            return;
        }

        for (Object item : lista) {
            if (!(item instanceof Map<?, ?>)) {
                continue;
            }
            Map<String, Object> dados = (Map<String, Object>) item;

            List<String> triggerTags = new ArrayList<>();
            Object objTags = dados.get("trigger_tags");
            if (objTags instanceof List<?> tagsLista) {
                for (Object t : tagsLista) {
                    if (t != null) triggerTags.add(t.toString().trim().toLowerCase());
                }
            }

            String action = (String) dados.getOrDefault("action", "none");
            String spawnEntity = (String) dados.get("spawn_entity");
            int spawnRadius = ((Number) dados.getOrDefault("spawn_radius", 0)).intValue();
            int durationTurns = ((Number) dados.getOrDefault("duration_turns", 0)).intValue();
            int explosionPower = ((Number) dados.getOrDefault("explosion_power", 0)).intValue();
            int soundVolume = ((Number) dados.getOrDefault("sound_volume", 0)).intValue();
            double damagePerTurn = ((Number) dados.getOrDefault("damage_per_turn", 0.0)).doubleValue();
            String description = (String) dados.getOrDefault("description", "");

            EnvironmentalReactionModel reacao = new EnvironmentalReactionModel(
                    triggerTags,
                    action,
                    spawnEntity,
                    spawnRadius,
                    durationTurns,
                    explosionPower,
                    soundVolume,
                    damagePerTurn,
                    description
            );

            reacoes.add(reacao);
        }
    }

    @Override
    public Optional<EnvironmentalReactionModel> buscarReacaoParaTags(List<String> tagsPresentes) {
        if (tagsPresentes == null || tagsPresentes.isEmpty()) {
            return Optional.empty();
        }
        for (EnvironmentalReactionModel reacao : reacoes) {
            if (reacao.todasTagsPresentes(tagsPresentes)) {
                return Optional.of(reacao);
            }
        }
        return Optional.empty();
    }

    @Override
    public Collection<EnvironmentalReactionModel> listarTodas() {
        return Collections.unmodifiableCollection(reacoes);
    }
}
