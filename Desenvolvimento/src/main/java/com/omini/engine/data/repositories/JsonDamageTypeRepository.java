package com.omini.engine.data.repositories;

import com.omini.engine.data.json.SimpleJsonParser;
import com.omini.engine.domain.models.DamageTypeModel;
import com.omini.engine.domain.repositories.IDamageTypeRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Repositório concreto que carrega e indexa tipos de dano a partir de arquivos JSON.
 */
public final class JsonDamageTypeRepository implements IDamageTypeRepository {

    private final Map<String, DamageTypeModel> tiposDanoPorId = new HashMap<>();

    public void carregarDeArquivo(Path caminhoArquivo) throws IOException {
        if (!Files.exists(caminhoArquivo)) {
            throw new IllegalArgumentException("O arquivo de tipos de dano não existe: " + caminhoArquivo);
        }
        String conteudo = Files.readString(caminhoArquivo);
        carregarDeJson(conteudo);
    }

    @SuppressWarnings("unchecked")
    public void carregarDeJson(String jsonConteudo) {
        Map<String, Object> raiz = SimpleJsonParser.parseObjeto(jsonConteudo);
        Object objDanos = raiz.get("damage_types");
        if (!(objDanos instanceof Map)) {
            throw new IllegalArgumentException("Estrutura inválida em damage_types.json: chave 'damage_types' ausente ou inválida.");
        }

        Map<String, Object> mapaDanos = (Map<String, Object>) objDanos;
        for (Map.Entry<String, Object> entrada : mapaDanos.entrySet()) {
            if (!(entrada.getValue() instanceof Map)) {
                continue;
            }
            Map<String, Object> dados = (Map<String, Object>) entrada.getValue();
            String id = (String) dados.getOrDefault("id", entrada.getKey());
            String nome = (String) dados.getOrDefault("name", id);
            String visualFx = (String) dados.getOrDefault("visual_fx", "");

            Map<String, Double> multiplicadoresTags = new HashMap<>();
            Object objTags = dados.get("tag_modifiers");
            if (objTags instanceof Map) {
                Map<String, Object> mapaTags = (Map<String, Object>) objTags;
                for (Map.Entry<String, Object> tag : mapaTags.entrySet()) {
                    if (tag.getValue() instanceof Number num) {
                        multiplicadoresTags.put(tag.getKey(), num.doubleValue());
                    }
                }
            }

            DamageTypeModel damageType = new DamageTypeModel(id, nome, visualFx, multiplicadoresTags);
            tiposDanoPorId.put(id, damageType);
        }
    }

    @Override
    public Optional<DamageTypeModel> buscarPorId(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(tiposDanoPorId.get(id));
    }

    @Override
    public Collection<DamageTypeModel> listarTodos() {
        return Collections.unmodifiableCollection(tiposDanoPorId.values());
    }
}
