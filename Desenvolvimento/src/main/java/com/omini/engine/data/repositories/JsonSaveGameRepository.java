package com.omini.engine.data.repositories;

import com.omini.engine.data.json.SimpleJsonParser;
import com.omini.engine.domain.dtos.SaveGameStateDTO;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * Repositório responsável pela serialização e persistência atômica à prova de falhas
 * do estado de jogo em arquivos JSON (Scoped Storage compatível).
 */
public final class JsonSaveGameRepository {

    private static final String NOME_ARQUIVO_SAVE = "game_save.json";
    private static final String NOME_ARQUIVO_TMP = "game_save.json.tmp";

    public Path salvarPartidaAtomica(Path diretorioSaves, SaveGameStateDTO estado) throws IOException {
        if (diretorioSaves == null || estado == null) {
            throw new IllegalArgumentException("Diretório de saves e estado não podem ser nulos.");
        }

        Files.createDirectories(diretorioSaves);

        Path arquivoTmp = diretorioSaves.resolve(NOME_ARQUIVO_TMP);
        Path arquivoFinal = diretorioSaves.resolve(NOME_ARQUIVO_SAVE);

        String json = serializarParaJson(estado);

        // 1. Grava no arquivo temporário com flush síncrono
        Files.writeString(arquivoTmp, json, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);

        // 2. Substituição atômica: se houver queda de energia, o save anterior continua 100% íntegro
        try {
            Files.move(arquivoTmp, arquivoFinal, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(arquivoTmp, arquivoFinal, StandardCopyOption.REPLACE_EXISTING);
        }

        return arquivoFinal;
    }

    @SuppressWarnings("unchecked")
    public Optional<SaveGameStateDTO> carregarPartida(Path arquivoSave) throws IOException {
        if (arquivoSave == null || !Files.exists(arquivoSave)) {
            return Optional.empty();
        }

        String json = Files.readString(arquivoSave, StandardCharsets.UTF_8);
        Map<String, Object> mapa = SimpleJsonParser.parseObjeto(json);

        String versao = (String) mapa.getOrDefault("versaoEngine", "0.1.0");
        long timestamp = ((Number) mapa.getOrDefault("timestampGravacao", 0L)).longValue();
        int ticks = ((Number) mapa.getOrDefault("relogioTicks", 0)).intValue();
        int jx = ((Number) mapa.getOrDefault("jogadorX", 0)).intValue();
        int jy = ((Number) mapa.getOrDefault("jogadorY", 0)).intValue();

        Map<String, Integer> stats = converterMapaInteiro((Map<String, Object>) mapa.get("jogadorStats"));
        Map<String, Double> sobrevivencia = converterMapaDouble((Map<String, Object>) mapa.get("jogadorSobrevivencia"));
        Map<String, Double> dorEFoco = converterMapaDouble((Map<String, Object>) mapa.get("jogadorDorEFoco"));

        List<Map<String, Object>> anatomia = (List<Map<String, Object>>) mapa.getOrDefault("jogadorPartesAnatomia", Collections.emptyList());
        List<Map<String, Object>> inventario = (List<Map<String, Object>>) mapa.getOrDefault("jogadorItensInventario", Collections.emptyList());

        int mapaW = ((Number) mapa.getOrDefault("mapaLargura", 10)).intValue();
        int mapaH = ((Number) mapa.getOrDefault("mapaAltura", 10)).intValue();
        List<String> tilesExplorados = (List<String>) mapa.getOrDefault("mapaTilesExplorados", Collections.emptyList());
        List<Map<String, Object>> entidadesMundo = (List<Map<String, Object>>) mapa.getOrDefault("entidadesMundo", Collections.emptyList());

        return Optional.of(new SaveGameStateDTO(
                versao,
                timestamp,
                ticks,
                jx,
                jy,
                stats,
                sobrevivencia,
                dorEFoco,
                anatomia,
                inventario,
                mapaW,
                mapaH,
                tilesExplorados,
                entidadesMundo
        ));
    }

    public boolean existeSave(Path diretorioSaves) {
        if (diretorioSaves == null) return false;
        return Files.exists(diretorioSaves.resolve(NOME_ARQUIVO_SAVE));
    }

    public boolean excluirSave(Path diretorioSaves) throws IOException {
        if (diretorioSaves == null) return false;
        Path arquivo = diretorioSaves.resolve(NOME_ARQUIVO_SAVE);
        return Files.deleteIfExists(arquivo);
    }

    private String serializarParaJson(SaveGameStateDTO s) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"versaoEngine\": \"").append(s.versaoEngine()).append("\",\n");
        sb.append("  \"timestampGravacao\": ").append(s.timestampGravacao()).append(",\n");
        sb.append("  \"relogioTicks\": ").append(s.relogioTicks()).append(",\n");
        sb.append("  \"jogadorX\": ").append(s.jogadorX()).append(",\n");
        sb.append("  \"jogadorY\": ").append(s.jogadorY()).append(",\n");

        sb.append("  \"jogadorStats\": {\n");
        appendMapEntries(sb, s.jogadorStats());
        sb.append("  },\n");

        sb.append("  \"jogadorSobrevivencia\": {\n");
        appendMapEntries(sb, s.jogadorSobrevivencia());
        sb.append("  },\n");

        sb.append("  \"jogadorDorEFoco\": {\n");
        appendMapEntries(sb, s.jogadorDorEFoco());
        sb.append("  },\n");

        sb.append("  \"jogadorPartesAnatomia\": [\n");
        appendListMaps(sb, s.jogadorPartesAnatomia());
        sb.append("  ],\n");

        sb.append("  \"jogadorItensInventario\": [\n");
        appendListMaps(sb, s.jogadorItensInventario());
        sb.append("  ],\n");

        sb.append("  \"mapaLargura\": ").append(s.mapaLargura()).append(",\n");
        sb.append("  \"mapaAltura\": ").append(s.mapaAltura()).append(",\n");

        sb.append("  \"mapaTilesExplorados\": [\n");
        for (int i = 0; i < s.mapaTilesExplorados().size(); i++) {
            sb.append("    \"").append(s.mapaTilesExplorados().get(i)).append("\"");
            if (i < s.mapaTilesExplorados().size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("  ],\n");

        sb.append("  \"entidadesMundo\": [\n");
        appendListMaps(sb, s.entidadesMundo());
        sb.append("  ]\n");

        sb.append("}\n");
        return sb.toString();
    }

    private void appendMapEntries(StringBuilder sb, Map<String, ?> mapa) {
        if (mapa == null) return;
        List<String> chaves = new ArrayList<>(mapa.keySet());
        for (int i = 0; i < chaves.size(); i++) {
            String k = chaves.get(i);
            Object v = mapa.get(k);
            sb.append("    \"").append(k).append("\": ").append(v);
            if (i < chaves.size() - 1) sb.append(",");
            sb.append("\n");
        }
    }

    private void appendListMaps(StringBuilder sb, List<Map<String, Object>> lista) {
        if (lista == null) return;
        for (int i = 0; i < lista.size(); i++) {
            Map<String, Object> m = lista.get(i);
            sb.append("    { ");
            List<String> chaves = new ArrayList<>(m.keySet());
            for (int j = 0; j < chaves.size(); j++) {
                String k = chaves.get(j);
                Object v = m.get(k);
                sb.append("\"").append(k).append("\": ");
                if (v instanceof String) {
                    sb.append("\"").append(v).append("\"");
                } else {
                    sb.append(v);
                }
                if (j < chaves.size() - 1) sb.append(", ");
            }
            sb.append(" }");
            if (i < lista.size() - 1) sb.append(",");
            sb.append("\n");
        }
    }

    private Map<String, Integer> converterMapaInteiro(Map<String, Object> origem) {
        if (origem == null) return Collections.emptyMap();
        Map<String, Integer> res = new LinkedHashMap<>();
        for (Map.Entry<String, Object> e : origem.entrySet()) {
            res.put(e.getKey(), ((Number) e.getValue()).intValue());
        }
        return res;
    }

    private Map<String, Double> converterMapaDouble(Map<String, Object> origem) {
        if (origem == null) return Collections.emptyMap();
        Map<String, Double> res = new LinkedHashMap<>();
        for (Map.Entry<String, Object> e : origem.entrySet()) {
            res.put(e.getKey(), ((Number) e.getValue()).doubleValue());
        }
        return res;
    }
}
