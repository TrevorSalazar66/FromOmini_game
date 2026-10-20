package com.omini.engine.data.loader;

import com.omini.engine.data.repositories.JsonAnatomyRepository;
import com.omini.engine.data.repositories.JsonDamageTypeRepository;
import com.omini.engine.data.repositories.JsonItemRepository;
import com.omini.engine.data.repositories.JsonMaterialRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Carregador em cascata para injeção e sobreposição de mods sobre o jogo base.
 * Segue a ordem:
 * 1. Carrega base_game/data/
 * 2. Varre a lista de mods ativos em mods/ e sobrescreve definições por ID ou adiciona novas.
 */
public final class CascadeModLoader {

    private final JsonMaterialRepository materialRepository;
    private final JsonDamageTypeRepository damageTypeRepository;
    private final JsonItemRepository itemRepository;
    private final JsonAnatomyRepository anatomyRepository;

    public CascadeModLoader(
        JsonMaterialRepository materialRepository,
        JsonDamageTypeRepository damageTypeRepository,
        JsonItemRepository itemRepository,
        JsonAnatomyRepository anatomyRepository
    ) {
        if (materialRepository == null || damageTypeRepository == null) {
            throw new IllegalArgumentException("Os repositórios de materiais e tipos de dano não podem ser nulos.");
        }
        this.materialRepository = materialRepository;
        this.damageTypeRepository = damageTypeRepository;
        this.itemRepository = itemRepository;
        this.anatomyRepository = anatomyRepository;
    }

    public CascadeModLoader(
        JsonMaterialRepository materialRepository,
        JsonDamageTypeRepository damageTypeRepository,
        JsonItemRepository itemRepository
    ) {
        this(materialRepository, damageTypeRepository, itemRepository, null);
    }

    public CascadeModLoader(
        JsonMaterialRepository materialRepository,
        JsonDamageTypeRepository damageTypeRepository
    ) {
        this(materialRepository, damageTypeRepository, null, null);
    }

    /**
     * Carrega os dados primários do jogo base e aplica os mods na ordem especificada.
     */
    public void carregar(Path diretorioRaizJogo, List<String> modsAtivos) throws IOException {
        if (diretorioRaizJogo == null || !Files.exists(diretorioRaizJogo)) {
            throw new IllegalArgumentException("Diretório raiz do jogo inexistente: " + diretorioRaizJogo);
        }

        // 1. Carregar Base Game
        Path dirBaseData = diretorioRaizJogo.resolve("base_game").resolve("data");
        carregarDiretorioData(dirBaseData);

        // 2. Carregar Mods Ativos
        if (modsAtivos != null && !modsAtivos.isEmpty()) {
            Path dirMods = diretorioRaizJogo.resolve("mods");
            for (String modId : modsAtivos) {
                Path dirModData = dirMods.resolve(modId).resolve("data");
                if (Files.exists(dirModData)) {
                    carregarDiretorioData(dirModData);
                }
            }
        }
    }

    private void carregarDiretorioData(Path dataPath) throws IOException {
        if (!Files.exists(dataPath)) {
            return;
        }

        Path materialsPath = dataPath.resolve("materials.json");
        if (Files.exists(materialsPath)) {
            materialRepository.carregarDeArquivo(materialsPath);
        }

        Path damageTypesPath = dataPath.resolve("damage_types.json");
        if (Files.exists(damageTypesPath)) {
            damageTypeRepository.carregarDeArquivo(damageTypesPath);
        }

        if (itemRepository != null) {
            Path itemsDir = dataPath.resolve("items");
            if (Files.exists(itemsDir) && Files.isDirectory(itemsDir)) {
                itemRepository.carregarDeDiretorio(itemsDir);
            }
            Path itemsSingleFile = dataPath.resolve("items.json");
            if (Files.exists(itemsSingleFile)) {
                itemRepository.carregarDeArquivo(itemsSingleFile);
            }
        }

        if (anatomyRepository != null) {
            Path anatomyPath = dataPath.resolve("anatomy.json");
            if (Files.exists(anatomyPath)) {
                anatomyRepository.carregarDeArquivo(anatomyPath);
            }
        }
    }
}
