package com.omini.engine.unit;

import com.omini.engine.data.repositories.JsonSaveGameRepository;
import com.omini.engine.domain.dtos.SaveGameStateDTO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class JsonSaveGameRepositoryTest {

    public static void main(String[] args) throws IOException {
        JsonSaveGameRepositoryTest test = new JsonSaveGameRepositoryTest();
        test.deveSalvarECarregarPartidaComPrecisao();
        System.out.println("✅ JsonSaveGameRepositoryTest: Todos os testes passaram!");
    }

    public void deveSalvarECarregarPartidaComPrecisao() throws IOException {
        JsonSaveGameRepository repo = new JsonSaveGameRepository();
        Path dirTemp = Paths.get("build", "test_saves");

        SaveGameStateDTO saveOriginal = new SaveGameStateDTO(
                "0.1.0-alpha",
                System.currentTimeMillis(),
                142,
                15,
                22,
                Map.of("forca", 14, "destreza", 12, "constituicao", 10),
                Map.of("estamina", 88.5, "fome", 12.0, "sede", 25.0, "sono", 5.0),
                Map.of("dorReal", 20.0, "analgesico", 10.0, "focoAtual", 80.0),
                List.of(
                        Map.of("partId", "head", "hpAtual", 75.0, "vital", true, "fraturado", false),
                        Map.of("partId", "forearm_l", "hpAtual", 0.0, "vital", false, "fraturado", true)
                ),
                List.of(
                        Map.of("itemId", "espada_curta_ferro", "instanceId", "esp_1", "gridX", 0, "gridY", 0, "rotacionado90", false)
                ),
                30,
                30,
                List.of("15,22", "15,23", "16,22"),
                List.of(
                        Map.of("id", "zombie_01", "name", "Zumbi", "x", 18, "y", 22)
                )
        );

        // 1. Salvar partida atomicamente
        Path arquivoSalvo = repo.salvarPartidaAtomica(dirTemp, saveOriginal);
        assert Files.exists(arquivoSalvo) : "Arquivo de save deve existir fisicamente";
        assert repo.existeSave(dirTemp) : "Deveria detectar existência do save";

        // 2. Carregar partida e comparar campos
        Optional<SaveGameStateDTO> carregadoOpt = repo.carregarPartida(arquivoSalvo);
        assert carregadoOpt.isPresent() : "Partida deve ser carregada com sucesso";
        SaveGameStateDTO carregado = carregadoOpt.get();

        assert "0.1.0-alpha".equals(carregado.versaoEngine());
        assert carregado.relogioTicks() == 142;
        assert carregado.jogadorX() == 15;
        assert carregado.jogadorY() == 22;
        assert carregado.jogadorStats().get("forca") == 14;
        assert Math.abs(carregado.jogadorSobrevivencia().get("estamina") - 88.5) < 0.001;
        assert Math.abs(carregado.jogadorDorEFoco().get("dorReal") - 20.0) < 0.001;
        assert carregado.jogadorPartesAnatomia().size() == 2;
        assert carregado.jogadorItensInventario().size() == 1;
        assert carregado.mapaTilesExplorados().size() == 3;
        assert carregado.entidadesMundo().size() == 1;

        // 3. Excluir save e verificar limpeza
        boolean excluido = repo.excluirSave(dirTemp);
        assert excluido : "Exclusão do save deve retornar true";
        assert !repo.existeSave(dirTemp) : "Save não deve mais existir após exclusão";
    }
}
