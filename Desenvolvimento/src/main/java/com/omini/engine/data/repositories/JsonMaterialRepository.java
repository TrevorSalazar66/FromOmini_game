package com.omini.engine.data.repositories;

import com.omini.engine.data.json.SimpleJsonParser;
import com.omini.engine.domain.models.MaterialModel;
import com.omini.engine.domain.repositories.IMaterialRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Repositório concreto que carrega e indexa materiais a partir de arquivos JSON.
 */
public final class JsonMaterialRepository implements IMaterialRepository {

    private final Map<String, MaterialModel> materiaisPorId = new HashMap<>();

    public void carregarDeArquivo(Path caminhoArquivo) throws IOException {
        if (!Files.exists(caminhoArquivo)) {
            throw new IllegalArgumentException("O arquivo de materiais não existe: " + caminhoArquivo);
        }
        String conteudo = Files.readString(caminhoArquivo);
        carregarDeJson(conteudo);
    }

    @SuppressWarnings("unchecked")
    public void carregarDeJson(String jsonConteudo) {
        Map<String, Object> raiz = SimpleJsonParser.parseObjeto(jsonConteudo);
        Object objMateriais = raiz.get("materials");
        if (!(objMateriais instanceof Map)) {
            throw new IllegalArgumentException("Estrutura inválida em materials.json: chave 'materials' ausente ou inválida.");
        }

        Map<String, Object> mapaMateriais = (Map<String, Object>) objMateriais;
        for (Map.Entry<String, Object> entrada : mapaMateriais.entrySet()) {
            if (!(entrada.getValue() instanceof Map)) {
                continue;
            }
            Map<String, Object> dados = (Map<String, Object>) entrada.getValue();
            String id = (String) dados.getOrDefault("id", entrada.getKey());
            String nome = (String) dados.getOrDefault("name", id);
            double densidade = ((Number) dados.getOrDefault("density_multiplier", 1.0)).doubleValue();
            double durezaBase = ((Number) dados.getOrDefault("base_hardness", 10.0)).doubleValue();
            boolean inflamavel = Boolean.TRUE.equals(dados.get("flammable"));

            Map<String, Double> modificadoresDano = new HashMap<>();
            Object objMods = dados.get("damage_modifiers");
            if (objMods instanceof Map) {
                Map<String, Object> mapaMods = (Map<String, Object>) objMods;
                for (Map.Entry<String, Object> mod : mapaMods.entrySet()) {
                    if (mod.getValue() instanceof Number num) {
                        modificadoresDano.put(mod.getKey(), num.doubleValue());
                    }
                }
            }

            MaterialModel material = new MaterialModel(id, nome, densidade, durezaBase, inflamavel, modificadoresDano);
            materiaisPorId.put(id, material);
        }
    }

    @Override
    public Optional<MaterialModel> buscarPorId(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(materiaisPorId.get(id));
    }

    @Override
    public Collection<MaterialModel> listarTodos() {
        return Collections.unmodifiableCollection(materiaisPorId.values());
    }
}
