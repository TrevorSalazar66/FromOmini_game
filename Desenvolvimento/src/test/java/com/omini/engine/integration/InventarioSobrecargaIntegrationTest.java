package com.omini.engine.integration;

import com.omini.engine.data.loader.CascadeModLoader;
import com.omini.engine.data.repositories.JsonDamageTypeRepository;
import com.omini.engine.data.repositories.JsonItemRepository;
import com.omini.engine.data.repositories.JsonMaterialRepository;
import com.omini.engine.domain.dtos.EstadoSobrecargaVO;
import com.omini.engine.domain.dtos.ResultadoInsercaoGradeVO;
import com.omini.engine.domain.ecs.Entity;
import com.omini.engine.domain.ecs.components.InventoryGridComponent;
import com.omini.engine.domain.ecs.components.ItemComponent;
import com.omini.engine.domain.factories.ItemEntityFactory;
import com.omini.engine.domain.models.ItemModel;
import com.omini.engine.domain.models.NivelSobrecargaEnum;
import com.omini.engine.domain.usecases.CalcularMassaProceduralUseCase;
import com.omini.engine.domain.usecases.CalcularSobrecargaUseCase;
import com.omini.engine.domain.usecases.InserirItemGradeUseCase;
import com.omini.engine.domain.usecases.RemoverItemGradeUseCase;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

public final class InventarioSobrecargaIntegrationTest {

    public static void main(String[] args) throws IOException {
        InventarioSobrecargaIntegrationTest test = new InventarioSobrecargaIntegrationTest();
        test.deveCarregarCatalogoItensBaseGame();
        test.deveExecutarFluxoCompletoDeInventarioESobrecarga();
        System.out.println("✅ InventarioSobrecargaIntegrationTest: Todos os testes passaram!");
    }

    private CascadeModLoader inicializarModLoader(JsonMaterialRepository materialRepo,
                                                  JsonDamageTypeRepository damageRepo,
                                                  JsonItemRepository itemRepo) throws IOException {
        CascadeModLoader loader = new CascadeModLoader(materialRepo, damageRepo, itemRepo);
        Path raizJogo = Paths.get("Desenvolvimento");
        loader.carregar(raizJogo, Collections.emptyList());
        return loader;
    }

    public void deveCarregarCatalogoItensBaseGame() throws IOException {
        JsonMaterialRepository materialRepo = new JsonMaterialRepository();
        JsonDamageTypeRepository damageRepo = new JsonDamageTypeRepository();
        JsonItemRepository itemRepo = new JsonItemRepository();
        inicializarModLoader(materialRepo, damageRepo, itemRepo);

        var espadaOpt = itemRepo.buscarPorId("espada_curta_ferro");
        assert espadaOpt.isPresent() : "Espada de ferro deve estar presente";
        assertEquals("weapon", espadaOpt.get().getCategory());
        assert espadaOpt.get().getWidthSlots() == 1 : "Largura esperada 1";
        assert espadaOpt.get().getHeightSlots() == 3 : "Altura esperada 3";
        assertEquals("iron", espadaOpt.get().getMaterialId());

        var mochilaOpt = itemRepo.buscarPorId("mochila_tatica");
        assert mochilaOpt.isPresent() : "Mochila tática deve estar presente";
        assertEquals("container", mochilaOpt.get().getCategory());

        var marteloOpt = itemRepo.buscarPorId("martelo_guerra_chumbo");
        assert marteloOpt.isPresent() : "Martelo de chumbo deve estar presente";
        assertEquals("lead", marteloOpt.get().getMaterialId());
    }

    public void deveExecutarFluxoCompletoDeInventarioESobrecarga() throws IOException {
        JsonMaterialRepository materialRepo = new JsonMaterialRepository();
        JsonDamageTypeRepository damageRepo = new JsonDamageTypeRepository();
        JsonItemRepository itemRepo = new JsonItemRepository();
        inicializarModLoader(materialRepo, damageRepo, itemRepo);

        ItemEntityFactory entityFactory = new ItemEntityFactory(materialRepo, new CalcularMassaProceduralUseCase());
        InserirItemGradeUseCase inserirUseCase = new InserirItemGradeUseCase();
        RemoverItemGradeUseCase removerUseCase = new RemoverItemGradeUseCase();
        CalcularSobrecargaUseCase sobrecargaUseCase = new CalcularSobrecargaUseCase();

        // 1. Instanciar Mochila Tática (container)
        ItemModel templateMochila = itemRepo.buscarPorId("mochila_tatica").orElseThrow();
        Entity mochilaEntity = entityFactory.criarInstancia(templateMochila);
        assert mochilaEntity.hasComponent(InventoryGridComponent.class) : "Deveria ter InventoryGridComponent";
        InventoryGridComponent grid = mochilaEntity.getComponent(InventoryGridComponent.class).orElseThrow();
        assert grid.getLargura() == 6 : "Largura 6";
        assert grid.getAltura() == 8 : "Altura 8";
        assert grid.getTotalSlots() == 48 : "Total 48 slots";

        // 2. Instanciar itens usando materiais para cálculo procedural da massa
        // Espada: 1x3, ferro (densidade 7.8) -> 1 * 3 * 7.8 = 23.4 kg
        ItemModel templateEspada = itemRepo.buscarPorId("espada_curta_ferro").orElseThrow();
        Entity espada = entityFactory.criarInstancia(templateEspada);
        ItemComponent compEspada = espada.getComponent(ItemComponent.class).orElseThrow();
        assert Math.abs(compEspada.getPesoKg() - 23.4) < 0.001 : "Peso espada divergente: " + compEspada.getPesoKg();

        // Adaga: 1x2, ferro (densidade 7.8) -> 1 * 2 * 7.8 = 15.6 kg
        ItemModel templateAdaga = itemRepo.buscarPorId("adaga_ferro").orElseThrow();
        Entity adaga = entityFactory.criarInstancia(templateAdaga);
        ItemComponent compAdaga = adaga.getComponent(ItemComponent.class).orElseThrow();
        assert Math.abs(compAdaga.getPesoKg() - 15.6) < 0.001 : "Peso adaga divergente: " + compAdaga.getPesoKg();

        // Tocha: 1x2, madeira macia (densidade 1.2) -> 1 * 2 * 1.2 = 2.4 kg
        ItemModel templateTocha = itemRepo.buscarPorId("tocha_madeira").orElseThrow();
        Entity tocha = entityFactory.criarInstancia(templateTocha);
        ItemComponent compTocha = tocha.getComponent(ItemComponent.class).orElseThrow();
        assert Math.abs(compTocha.getPesoKg() - 2.4) < 0.001 : "Peso tocha divergente: " + compTocha.getPesoKg();

        // 3. Inserir itens na grade da mochila
        ResultadoInsercaoGradeVO resEspada = inserirUseCase.executar(grid, espada, 0, 0);
        assert resEspada.isSucesso() : "Falha ao inserir espada";

        ResultadoInsercaoGradeVO resAdaga = inserirUseCase.executar(grid, adaga, 1, 0);
        assert resAdaga.isSucesso() : "Falha ao inserir adaga";

        ResultadoInsercaoGradeVO resTocha = inserirUseCase.executar(grid, tocha, 2, 0);
        assert resTocha.isSucesso() : "Falha ao inserir tocha";

        // Slots ocupados: 3 (espada) + 2 (adaga) + 2 (tocha) = 7 slots
        assert grid.getSlotsOcupadosContagem() == 7 : "Esperado 7 slots ocupados";
        assert grid.getSlotsLivresContagem() == 41 : "Esperado 41 slots livres";

        // Peso acumulado: 23.4 + 15.6 + 2.4 = 41.4 kg
        double pesoMochila = grid.getPesoTotalItens();
        assert Math.abs(pesoMochila - 41.4) < 0.001 : "Peso total na mochila divergente: " + pesoMochila;

        // 4. Avaliar sobrecarga para personagem com Força 8 (Confortável: 40kg, Máximo: 80kg)
        EstadoSobrecargaVO sobrecarga = sobrecargaUseCase.executar(pesoMochila, 8);
        assert sobrecarga.isEstaSobrecarregado() : "Peso 41.4kg deve sobrecarregar personagem com limite 40kg";
        assert sobrecarga.getNivelSobrecarga() == NivelSobrecargaEnum.PESADA : "Deveria ser PESADA";
        assert sobrecarga.getPenalidadeAPPercentual() == 15 : "Penalidade AP esperada 15%";
        assert Math.abs(sobrecarga.getPenalidadeEsquivaPercentual() - 0.20) < 0.001 : "Penalidade esquiva 20%";

        // 5. Inserir Martelo de Chumbo (2x3, chumbo densidade 11.3 -> 67.8 kg)
        // Inserir rotacionado a 90 graus (ocupando 3 de largura e 2 de altura) na posição (0, 4)
        ItemModel templateMartelo = itemRepo.buscarPorId("martelo_guerra_chumbo").orElseThrow();
        Entity martelo = entityFactory.criarInstancia(templateMartelo);
        ItemComponent compMartelo = martelo.getComponent(ItemComponent.class).orElseThrow();
        assert Math.abs(compMartelo.getPesoKg() - 67.8) < 0.001 : "Peso martelo divergente: " + compMartelo.getPesoKg();

        ResultadoInsercaoGradeVO resMartelo = inserirUseCase.executar(grid, martelo, 0, 4, true);
        assert resMartelo.isSucesso() : "Martelo deve caber rotacionado em (0, 4)";
        assert resMartelo.getItemInserido().orElseThrow().isRotacionado90() : "Deveria estar rotacionado";

        // Novos slots ocupados: 7 + 6 = 13 slots
        assert grid.getSlotsOcupadosContagem() == 13 : "Esperado 13 slots ocupados";
        assert grid.getSlotsLivresContagem() == 35 : "Esperado 35 slots livres";

        // Novo peso total: 41.4 + 67.8 = 109.2 kg
        double pesoCritico = grid.getPesoTotalItens();
        assert Math.abs(pesoCritico - 109.2) < 0.001 : "Peso crítico divergente: " + pesoCritico;

        // 6. Reavaliar sobrecarga para Força 8 (Máximo absoluto é 80kg) -> 109.2kg ultrapassa o máximo!
        EstadoSobrecargaVO sobrecargaCritica = sobrecargaUseCase.executar(pesoCritico, 8);
        assert sobrecargaCritica.getNivelSobrecarga() == NivelSobrecargaEnum.INCAPACITADO : "Deveria ser INCAPACITADO";
        assert sobrecargaCritica.getPenalidadeAPPercentual() == 100 : "Penalidade AP 100%";
        assert Math.abs(sobrecargaCritica.getPenalidadeEsquivaPercentual() - 1.00) < 0.001 : "Penalidade esquiva 100%";

        // 7. Remover o martelo e conferir se desocupa perfeitamente a grade
        var marteloRemovidoOpt = removerUseCase.executar(grid, martelo.getId());
        assert marteloRemovidoOpt.isPresent() : "Martelo deveria ser removido";

        assert grid.getSlotsOcupadosContagem() == 7 : "Deveria voltar para 7 slots ocupados";
        assert Math.abs(grid.getPesoTotalItens() - 41.4) < 0.001 : "Peso deveria voltar para 41.4kg";
        EstadoSobrecargaVO sobrecargaAliviada = sobrecargaUseCase.executar(grid.getPesoTotalItens(), 8);
        assert sobrecargaAliviada.getNivelSobrecarga() == NivelSobrecargaEnum.PESADA : "Deveria voltar para PESADA";
    }

    private static void assertEquals(Object expected, Object actual) {
        assert java.util.Objects.equals(expected, actual) : "Esperado [" + expected + "], mas foi [" + actual + "]";
    }
}
