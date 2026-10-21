package com.omini.engine.integration;

import com.omini.engine.controllers.GameLoopController;
import com.omini.engine.controllers.InteractionController;
import com.omini.engine.controllers.InventoryController;
import com.omini.engine.data.loader.CascadeModLoader;
import com.omini.engine.data.repositories.*;
import com.omini.engine.domain.dtos.ParametrosGeracaoMapaDTO;
import com.omini.engine.domain.dtos.ResultadoGeracaoMapaVO;
import com.omini.engine.domain.dtos.SaveGameStateDTO;
import com.omini.engine.domain.ecs.Entity;
import com.omini.engine.domain.ecs.components.*;
import com.omini.engine.domain.factories.ItemEntityFactory;
import com.omini.engine.domain.models.AnatomyDefinitionModel;
import com.omini.engine.domain.models.ItemModel;
import com.omini.engine.domain.models.TileMap;
import com.omini.engine.domain.usecases.*;
import com.omini.engine.ui.views.ContextMenuView;
import com.omini.engine.ui.views.HUDView;
import com.omini.engine.ui.views.MapView;
import com.omini.engine.ui.views.ViewportCamera;
import com.omini.engine.ui.widgets.AnatomyWidget;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class InterfaceMobileEGameStateIntegrationTest {

    public static void main(String[] args) throws IOException {
        InterfaceMobileEGameStateIntegrationTest test = new InterfaceMobileEGameStateIntegrationTest();
        test.executarSessaoCompletaMobileEPersistencia();
        System.out.println("🌟 InterfaceMobileEGameStateIntegrationTest (Camada 2): Todos os testes passaram com 100% de sucesso!");
    }

    public void executarSessaoCompletaMobileEPersistencia() throws IOException {
        System.out.println("--- [FASE 5 INTEGRATION] Inicializando Ambiente & ModLoader ---");
        JsonMaterialRepository matRepo = new JsonMaterialRepository();
        JsonDamageTypeRepository dmgRepo = new JsonDamageTypeRepository();
        JsonItemRepository itemRepo = new JsonItemRepository();
        JsonAnatomyRepository anatomyRepo = new JsonAnatomyRepository();
        JsonEnvironmentalReactionRepository reactionRepo = new JsonEnvironmentalReactionRepository();

        CascadeModLoader loader = new CascadeModLoader(matRepo, dmgRepo, itemRepo, anatomyRepo, reactionRepo);
        Path raizJogo = Paths.get("Desenvolvimento");
        loader.carregar(raizJogo, Collections.emptyList());

        // 1. Geração de Masmorra Procedural
        System.out.println("--- [FASE 5 INTEGRATION] Gerando Masmorra Procedural ---");
        GerarMapaProceduralUseCase mapGen = new GerarMapaProceduralUseCase();
        ParametrosGeracaoMapaDTO params = new ParametrosGeracaoMapaDTO(40, 40, 6, 4, 8, 12345L);
        ResultadoGeracaoMapaVO masmorra = mapGen.executar(params);
        TileMap mapa = masmorra.mapa();

        // 2. Instanciação do Jogador
        AnatomyDefinitionModel anatomyDef = anatomyRepo.buscarPorId("humanoid_standard").orElseThrow();
        Entity jogador = new Entity("player_mobile", "Sobrevivente Tático");
        jogador.addTag("player");
        jogador.addComponent(new PositionComponent(masmorra.pontoSpawn().x(), masmorra.pontoSpawn().y()));
        jogador.addComponent(AnatomyComponent.aPartirDeDefinicao(anatomyDef, 100.0));
        jogador.addComponent(new PainFocusComponent(0.0, 100.0));
        jogador.addComponent(new SurvivalComponent(0.0, 0.0, 100.0, 0.0));
        jogador.addComponent(new StatsComponent(12, 14, 10, 10, 10));
        jogador.addComponent(new TurnEnergyComponent(150));
        jogador.addComponent(new InventoryGridComponent(6, 8)); // Mochila tática com 48 slots

        mapa.adicionarEntidade(masmorra.pontoSpawn().x(), masmorra.pontoSpawn().y(), jogador);

        // 3. Camada de Apresentação e Câmera
        System.out.println("--- [FASE 5 INTEGRATION] Configurando ViewportCamera e MapView ---");
        ViewportCamera camera = new ViewportCamera(1080, 1920, 48);
        camera.centralizarEm(masmorra.pontoSpawn().x(), masmorra.pontoSpawn().y());
        MapView mapView = new MapView(camera, mapa);

        CalcularLinhaDeVisaoUseCase fovUseCase = new CalcularLinhaDeVisaoUseCase();
        mapView.atualizarFov(fovUseCase.calcularCampoVisao(mapa, masmorra.pontoSpawn().x(), masmorra.pontoSpawn().y(), 8));

        assert mapView.isVisivelFov(masmorra.pontoSpawn().x(), masmorra.pontoSpawn().y()) : "Spawn do jogador deve estar visível no FOV";

        // 4. Interação Touch & Menus Contextuais
        System.out.println("--- [FASE 5 INTEGRATION] Testando Toques na Tela e Menus Contextuais ---");
        ContextMenuView contextMenu = new ContextMenuView();
        InteractionController interaction = new InteractionController(
                mapView, contextMenu, jogador,
                new NavegarPathfindingAStarUseCase(),
                fovUseCase,
                new CalcularCustoAcaoAPUseCase(),
                new MotorTurnosAPUseCase()
        );

        // Toque no próprio jogador abre menu do jogador
        int[] posTelaJogador = camera.mundoParaTela(masmorra.pontoSpawn().x(), masmorra.pontoSpawn().y());
        interaction.processarToqueTela(posTelaJogador[0], posTelaJogador[1]);
        assert contextMenu.isVisivel() : "Menu do jogador deveria estar visível";
        contextMenu.fechar();

        // 5. Teste de Gestos no Inventário (Drag-and-Drop)
        System.out.println("--- [FASE 5 INTEGRATION] Testando Gestos de Inventário (Arrasto e Rotação) ---");
        ItemEntityFactory itemFactory = new ItemEntityFactory(matRepo, new CalcularMassaProceduralUseCase());
        ItemModel templateEspada = itemRepo.buscarPorId("espada_curta_ferro").orElseThrow();
        Entity espada = itemFactory.criarInstancia(templateEspada);

        InventoryGridComponent gridJogador = jogador.getComponent(InventoryGridComponent.class).orElseThrow();
        InserirItemGradeUseCase inserirItemUseCase = new InserirItemGradeUseCase();
        inserirItemUseCase.executar(gridJogador, espada, 0, 0);

        InventoryController inventoryController = new InventoryController();
        boolean iniciouArrasto = inventoryController.iniciarArrasto(gridJogador, 0, 0);
        assert iniciouArrasto : "Deveria iniciar arrasto da espada";
        assert inventoryController.isArrastando();

        // Rotacionar em 90 graus
        boolean rotacionou = inventoryController.alternarRotacao();
        assert rotacionou : "Deveria permitir rotação da espada";
        assert inventoryController.isRotacionado90();

        // Soltar na nova posição (0, 4)
        var resSoltura = inventoryController.soltar(gridJogador, 0, 4);
        assert resSoltura.isSucesso() : "Deveria soltar com sucesso rotacionada";
        assert !inventoryController.isArrastando();

        // 6. Teste de Tratamento na Silhueta Anatômica
        System.out.println("--- [FASE 5 INTEGRATION] Testando Silhueta Anatômica e Tratamento Médico ---");
        AplicarDanoLocalizadoUseCase danoUseCase = new AplicarDanoLocalizadoUseCase();
        danoUseCase.executar(jogador, "forearm_l", 36.0, "blunt"); // Fratura o antebraço

        AnatomyWidget anatomyWidget = new AnatomyWidget();
        var silhueta = anatomyWidget.extrairSilhueta(jogador);
        var antebraco = silhueta.stream().filter(m -> "forearm_l".equals(m.id())).findFirst().orElseThrow();
        assert antebraco.fraturado();
        assert "#212121".equals(antebraco.corHex()) : "Membro fraturado deve ser cinza/preto";

        // Aplica tala e curativo
        boolean talaAplicada = anatomyWidget.aplicarTratamento(jogador, "forearm_l", "tala");
        assert talaAplicada;
        boolean curativoAplicado = anatomyWidget.aplicarTratamento(jogador, "forearm_l", "curativo");
        assert curativoAplicado;

        // 7. HUD View minimalista
        System.out.println("--- [FASE 5 INTEGRATION] Extraindo Estado do HUD ---");
        HUDView hudView = new HUDView();
        hudView.adicionarMensagemLog("Iniciou a exploração da masmorra procedural.");
        var estadoHud = hudView.extrairEstado(jogador);
        assert estadoHud.prontoParaAgir();
        assert estadoHud.logEventosRecentes().size() == 1;

        // 8. Persistência Atômica do Estado da Partida
        System.out.println("--- [FASE 5 INTEGRATION] Testando Persistência Atômica de Jogo (Save & Load) ---");
        JsonSaveGameRepository saveRepo = new JsonSaveGameRepository();
        Path dirSaves = Paths.get("build", "save_test_integration");

        SaveGameStateDTO saveDto = new SaveGameStateDTO(
                "0.1.0-alpha",
                System.currentTimeMillis(),
                42,
                jogador.getComponent(PositionComponent.class).orElseThrow().getX(),
                jogador.getComponent(PositionComponent.class).orElseThrow().getY(),
                Map.of("forca", 12, "destreza", 14),
                Map.of("estamina", 100.0, "fome", 0.0, "sede", 0.0),
                Map.of("dorReal", 10.0, "analgesico", 0.0, "focoAtual", 90.0),
                List.of(
                        Map.of("partId", "forearm_l", "hpAtual", 0.0, "vital", false, "fraturado", true)
                ),
                List.of(
                        Map.of("itemId", "espada_curta_ferro", "instanceId", espada.getId(), "gridX", 0, "gridY", 4, "rotacionado90", true)
                ),
                mapa.getLargura(),
                mapa.getAltura(),
                List.of(masmorra.pontoSpawn().x() + "," + masmorra.pontoSpawn().y()),
                List.of()
        );

        Path arquivoGravado = saveRepo.salvarPartidaAtomica(dirSaves, saveDto);
        assert saveRepo.existeSave(dirSaves) : "Save deve existir no disco";

        Optional<SaveGameStateDTO> carregadoOpt = saveRepo.carregarPartida(arquivoGravado);
        assert carregadoOpt.isPresent() : "Save deve carregar sem falhas";
        SaveGameStateDTO carregado = carregadoOpt.get();

        assert carregado.relogioTicks() == 42;
        assert carregado.jogadorX() == masmorra.pontoSpawn().x();
        assert carregado.jogadorY() == masmorra.pontoSpawn().y();
        assert carregado.jogadorStats().get("forca") == 12;
        assert carregado.jogadorItensInventario().size() == 1;
        assert Boolean.TRUE.equals(carregado.jogadorItensInventario().get(0).get("rotacionado90"));

        saveRepo.excluirSave(dirSaves);
        System.out.println("--- [FASE 5 INTEGRATION] Concluído com Sucesso Absoluto ---");
    }
}
