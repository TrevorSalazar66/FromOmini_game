package com.omini.engine.unit;

import com.omini.engine.data.repositories.JsonEnvironmentalReactionRepository;
import com.omini.engine.domain.dtos.ResultadoReacaoVO;
import com.omini.engine.domain.ecs.Entity;
import com.omini.engine.domain.ecs.components.SurfaceFieldComponent;
import com.omini.engine.domain.models.TileMap;
import com.omini.engine.domain.models.TileTypeEnum;
import com.omini.engine.domain.usecases.ProcessarReacaoAmbientalUseCase;

import java.io.File;

public final class AlquimiaElementalTest {

    public static void main(String[] args) {
        AlquimiaElementalTest test = new AlquimiaElementalTest();
        test.deveProcessarReacaoFogoEAguaGerandoFumaca();
        test.deveProcessarDetonacaoGasComFogo();
        test.deveProcessarEletrificacaoDeAgua();
        test.deveIgnorarTilesSemReagentesSuficientes();
        System.out.println("✅ AlquimiaElementalTest: Todos os testes passaram!");
    }

    private JsonEnvironmentalReactionRepository obterRepositorioReacoes() {
        File file = new File("Desenvolvimento/base_game/data/environmental_reactions.json");
        if (!file.exists()) {
            file = new File("base_game/data/environmental_reactions.json");
        }
        JsonEnvironmentalReactionRepository repo = new JsonEnvironmentalReactionRepository();
        try {
            repo.carregarDeArquivo(file.toPath());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar reações nos testes: " + e.getMessage(), e);
        }
        return repo;
    }

    public void deveProcessarReacaoFogoEAguaGerandoFumaca() {
        JsonEnvironmentalReactionRepository repo = obterRepositorioReacoes();
        TileMap mapa = new TileMap(5, 5, TileTypeEnum.CHAO_PEDRA);
        ProcessarReacaoAmbientalUseCase useCase = new ProcessarReacaoAmbientalUseCase();

        Entity fogo = new Entity("fogo_01", "Chamas");
        fogo.addComponent(new SurfaceFieldComponent("surface_fire", 5));
        fogo.addTag("surface_fire");

        Entity agua = new Entity("agua_01", "Poça de Água");
        agua.addComponent(new SurfaceFieldComponent("surface_water", 10));
        agua.addTag("surface_water");

        mapa.adicionarEntidade(2, 2, fogo);
        mapa.adicionarEntidade(2, 2, agua);

        assert mapa.getEntidadesNoTile(2, 2).size() == 2 : "Deveria ter 2 entidades antes da reação";

        ResultadoReacaoVO resultado = useCase.executar(mapa, 2, 2, repo);

        assert resultado.isOcorreuReacao() : "Deveria ocorrer reação entre fogo e água";
        assert "extinguish_and_spawn".equalsIgnoreCase(resultado.getAcao()) : "Ação incorreta";
        assert resultado.getEntidadesRemovidas().size() == 2 : "Fogo e água deveriam ter sido removidos";
        assert resultado.getEntidadesGeradas().size() == 1 : "Deveria gerar campo de fumaça";

        Entity gerada = resultado.getEntidadesGeradas().get(0);
        assert gerada.hasTag("field_smoke") : "Entidade gerada deve ser fumaça";
        assert gerada.hasTag("bloqueia_visao") : "Fumaça deve bloquear visão";

        // Verifica o estado atual do mapa
        assert mapa.isBloqueiaVisao(2, 2) : "O tile agora deve bloquear a visão devido à fumaça";
    }

    public void deveProcessarDetonacaoGasComFogo() {
        JsonEnvironmentalReactionRepository repo = obterRepositorioReacoes();
        TileMap mapa = new TileMap(5, 5, TileTypeEnum.CHAO_PEDRA);
        ProcessarReacaoAmbientalUseCase useCase = new ProcessarReacaoAmbientalUseCase();

        Entity fogo = new Entity("fogo_02", "Chamas");
        fogo.addComponent(new SurfaceFieldComponent("surface_fire", 5));
        fogo.addTag("surface_fire");

        Entity gas = new Entity("gas_01", "Vapor de Gás Inflamável");
        gas.addComponent(new SurfaceFieldComponent("flammable_gas", 5));
        gas.addTag("flammable_gas");

        mapa.adicionarEntidade(1, 1, fogo);
        mapa.adicionarEntidade(1, 1, gas);

        ResultadoReacaoVO resultado = useCase.executar(mapa, 1, 1, repo);

        assert resultado.isOcorreuReacao() : "Deveria detonar";
        assert "detonate".equalsIgnoreCase(resultado.getAcao()) : "Ação deve ser detonate";
        assert resultado.getExplosionPower() == 60 : "Poder de explosão incorreto";
    }

    public void deveProcessarEletrificacaoDeAgua() {
        JsonEnvironmentalReactionRepository repo = obterRepositorioReacoes();
        TileMap mapa = new TileMap(5, 5, TileTypeEnum.CHAO_PEDRA);
        ProcessarReacaoAmbientalUseCase useCase = new ProcessarReacaoAmbientalUseCase();

        Entity choque = new Entity("choque_01", "Arco Elétrico");
        choque.addComponent(new SurfaceFieldComponent("surface_electric", 3));
        choque.addTag("surface_electric");

        Entity agua = new Entity("agua_02", "Poça de Água");
        agua.addComponent(new SurfaceFieldComponent("surface_water", 10));
        agua.addTag("surface_water");

        mapa.adicionarEntidade(3, 3, choque);
        mapa.adicionarEntidade(3, 3, agua);

        ResultadoReacaoVO resultado = useCase.executar(mapa, 3, 3, repo);

        assert resultado.isOcorreuReacao() : "Deveria eletrificar poça";
        assert "electrify_pool".equalsIgnoreCase(resultado.getAcao()) : "Ação deve ser electrify_pool";
        assert resultado.getDamagePerTurn() == 25 : "Dano elétrico por turno incorreto";
    }

    public void deveIgnorarTilesSemReagentesSuficientes() {
        JsonEnvironmentalReactionRepository repo = obterRepositorioReacoes();
        TileMap mapa = new TileMap(5, 5, TileTypeEnum.CHAO_PEDRA);
        ProcessarReacaoAmbientalUseCase useCase = new ProcessarReacaoAmbientalUseCase();

        Entity fogo = new Entity("fogo_sozinho", "Chama Solitária");
        fogo.addTag("surface_fire");
        mapa.adicionarEntidade(0, 0, fogo);

        ResultadoReacaoVO resultado = useCase.executar(mapa, 0, 0, repo);
        assert !resultado.isOcorreuReacao() : "Não deveria reagir com apenas 1 elemento";
    }
}
