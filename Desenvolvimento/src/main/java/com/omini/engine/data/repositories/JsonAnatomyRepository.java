package com.omini.engine.data.repositories;

import com.omini.engine.data.json.SimpleJsonParser;
import com.omini.engine.domain.models.AnatomyDefinitionModel;
import com.omini.engine.domain.models.BodyPartDefinitionModel;
import com.omini.engine.domain.repositories.IAnatomyRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Repositório concreto que carrega e armazena definições anatômicas a partir de arquivos JSON.
 */
public final class JsonAnatomyRepository implements IAnatomyRepository {

    private final Map<String, AnatomyDefinitionModel> anatomiasPorId = new HashMap<>();

    public void carregarDeArquivo(Path caminhoArquivo) throws IOException {
        if (!Files.exists(caminhoArquivo)) {
            throw new IllegalArgumentException("O arquivo de anatomia não existe: " + caminhoArquivo);
        }
        String conteudo = Files.readString(caminhoArquivo);
        carregarDeJson(conteudo);
    }

    @SuppressWarnings("unchecked")
    public void carregarDeJson(String jsonConteudo) {
        Map<String, Object> raiz = SimpleJsonParser.parseObjeto(jsonConteudo);
        Object objAnatomies = raiz.get("anatomies");
        if (!(objAnatomies instanceof Map<?, ?> mapaAnatomias)) {
            return;
        }

        for (Map.Entry<?, ?> entrada : mapaAnatomias.entrySet()) {
            if (!(entrada.getValue() instanceof Map<?, ?>)) {
                continue;
            }
            Map<String, Object> dadosAnatomia = (Map<String, Object>) entrada.getValue();

            String id = (String) dadosAnatomia.getOrDefault("id", entrada.getKey().toString());
            String nome = (String) dadosAnatomia.getOrDefault("name", id);

            Map<String, BodyPartDefinitionModel> partes = new LinkedHashMap<>();
            Object objPartes = dadosAnatomia.get("body_parts");
            if (objPartes instanceof Map<?, ?> mapaPartes) {
                for (Map.Entry<?, ?> parteEntrada : mapaPartes.entrySet()) {
                    if (parteEntrada.getValue() instanceof Map<?, ?>) {
                        Map<String, Object> dadosParte = (Map<String, Object>) parteEntrada.getValue();
                        String parteId = (String) dadosParte.getOrDefault("id", parteEntrada.getKey().toString());
                        String parteNome = (String) dadosParte.getOrDefault("name", parteId);
                        int pesoAcerto = ((Number) dadosParte.getOrDefault("hit_weight", 1)).intValue();
                        boolean vital = Boolean.TRUE.equals(dadosParte.get("vital"));
                        double hpRatio = ((Number) dadosParte.getOrDefault("base_hp_ratio", 1.0)).doubleValue();
                        String armorSlot = (String) dadosParte.getOrDefault("armor_slot", "none");

                        BodyPartDefinitionModel parteDef = new BodyPartDefinitionModel(
                                parteId,
                                parteNome,
                                pesoAcerto,
                                vital,
                                hpRatio,
                                armorSlot
                        );
                        partes.put(parteId.toLowerCase(), parteDef);
                    }
                }
            }

            if (!partes.isEmpty()) {
                AnatomyDefinitionModel anatomia = new AnatomyDefinitionModel(id, nome, partes);
                anatomiasPorId.put(id, anatomia);
            }
        }
    }

    @Override
    public Optional<AnatomyDefinitionModel> buscarPorId(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(anatomiasPorId.get(id));
    }

    @Override
    public Collection<AnatomyDefinitionModel> listarTodos() {
        return Collections.unmodifiableCollection(anatomiasPorId.values());
    }
}
