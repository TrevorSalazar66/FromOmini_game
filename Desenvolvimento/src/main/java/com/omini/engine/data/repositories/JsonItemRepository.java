package com.omini.engine.data.repositories;

import com.omini.engine.data.json.SimpleJsonParser;
import com.omini.engine.domain.models.ItemModel;
import com.omini.engine.domain.repositories.IItemRepository;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Repositório concreto que carrega e armazena definições de itens a partir de arquivos JSON.
 */
public final class JsonItemRepository implements IItemRepository {

    private final Map<String, ItemModel> itensPorId = new HashMap<>();

    public void carregarDeArquivo(Path caminhoArquivo) throws IOException {
        if (!Files.exists(caminhoArquivo)) {
            throw new IllegalArgumentException("O arquivo de itens não existe: " + caminhoArquivo);
        }
        String conteudo = Files.readString(caminhoArquivo);
        carregarDeJson(conteudo);
    }

    public void carregarDeDiretorio(Path diretorioItens) throws IOException {
        if (!Files.exists(diretorioItens) || !Files.isDirectory(diretorioItens)) {
            return;
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(diretorioItens, "*.json")) {
            for (Path arquivoJson : stream) {
                carregarDeArquivo(arquivoJson);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void carregarDeJson(String jsonConteudo) {
        Map<String, Object> raiz = SimpleJsonParser.parseObjeto(jsonConteudo);
        Object objItems = raiz.get("items");

        if (objItems instanceof Map<?, ?> mapaItems) {
            for (Map.Entry<?, ?> entrada : mapaItems.entrySet()) {
                if (entrada.getValue() instanceof Map<?, ?> dados) {
                    processarItem((Map<String, Object>) dados, entrada.getKey().toString());
                }
            }
        } else if (objItems instanceof List<?> listaItems) {
            for (Object itemObj : listaItems) {
                if (itemObj instanceof Map<?, ?> dados) {
                    processarItem((Map<String, Object>) dados, null);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void processarItem(Map<String, Object> dados, String chaveFallback) {
        String id = (String) dados.getOrDefault("id", chaveFallback);
        if (id == null || id.trim().isEmpty()) {
            return;
        }

        String nome = (String) dados.getOrDefault("name", id);
        String descricao = (String) dados.getOrDefault("description", "");
        String categoria = (String) dados.getOrDefault("category", "misc");
        int largura = ((Number) dados.getOrDefault("width_slots", 1)).intValue();
        int altura = ((Number) dados.getOrDefault("height_slots", 1)).intValue();
        String materialId = (String) dados.getOrDefault("material_id", "iron");

        Double pesoOverride = null;
        Object objPeso = dados.get("weight_kg_override");
        if (objPeso instanceof Number num) {
            pesoOverride = num.doubleValue();
        }

        boolean podeRotacionar = true;
        if (dados.containsKey("can_rotate")) {
            podeRotacionar = Boolean.TRUE.equals(dados.get("can_rotate"));
        }

        String equipSlot = (String) dados.get("equip_slot");

        Map<String, Object> propriedades = new HashMap<>();
        Object objProp = dados.get("properties");
        if (objProp instanceof Map<?, ?> mapaProp) {
            propriedades.putAll((Map<String, Object>) mapaProp);
        }

        ItemModel item = new ItemModel(
                id,
                nome,
                descricao,
                categoria,
                largura,
                altura,
                materialId,
                pesoOverride,
                podeRotacionar,
                equipSlot,
                propriedades
        );

        itensPorId.put(id, item);
    }

    @Override
    public Optional<ItemModel> buscarPorId(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(itensPorId.get(id));
    }

    @Override
    public Collection<ItemModel> listarPorCategoria(String categoria) {
        if (categoria == null) return Collections.emptyList();
        String catNormalizada = categoria.trim().toLowerCase();
        List<ItemModel> resultado = new ArrayList<>();
        for (ItemModel item : itensPorId.values()) {
            if (item.getCategory().equalsIgnoreCase(catNormalizada)) {
                resultado.add(item);
            }
        }
        return Collections.unmodifiableCollection(resultado);
    }

    @Override
    public Collection<ItemModel> listarTodos() {
        return Collections.unmodifiableCollection(itensPorId.values());
    }
}
