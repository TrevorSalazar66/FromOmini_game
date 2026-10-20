package com.omini.engine.unit;

import com.omini.engine.domain.dtos.ResultadoInsercaoGradeVO;
import com.omini.engine.domain.ecs.Entity;
import com.omini.engine.domain.ecs.components.InventoryGridComponent;
import com.omini.engine.domain.ecs.components.ItemComponent;
import com.omini.engine.domain.usecases.InserirItemGradeUseCase;
import com.omini.engine.domain.usecases.RemoverItemGradeUseCase;

public final class InventarioGradeUseCaseTest {

    public static void main(String[] args) {
        InventarioGradeUseCaseTest test = new InventarioGradeUseCaseTest();
        test.deveInicializarGradeVazia();
        test.deveInserirItensSemRotacao();
        test.deveInserirItemRotacionado();
        test.deveRejeitarColisao();
        test.deveRejeitarTransbordo();
        test.deveRemoverItemEDesocuparSlots();
        test.deveRejeitarRotacaoNaoPermitida();
        System.out.println("✅ InventarioGradeUseCaseTest: Todos os testes passaram!");
    }

    public void deveInicializarGradeVazia() {
        InventoryGridComponent grid = new InventoryGridComponent(5, 5);
        assert grid.getLargura() == 5 : "Largura esperada 5";
        assert grid.getAltura() == 5 : "Altura esperada 5";
        assert grid.getTotalSlots() == 25 : "Total de slots esperado 25";
        assert grid.getSlotsLivresContagem() == 25 : "Slots livres esperado 25";
        assert grid.getSlotsOcupadosContagem() == 0 : "Slots ocupados esperado 0";
        assert grid.getQuantidadeItens() == 0 : "Qtd itens esperado 0";
    }

    public void deveInserirItensSemRotacao() {
        InventoryGridComponent grid = new InventoryGridComponent(5, 5);
        InserirItemGradeUseCase useCase = new InserirItemGradeUseCase();

        Entity pocao = new Entity("item_pocao", "Poção");
        pocao.addComponent(new ItemComponent("pocao_cura", "Poção de Cura", 1, 1, 0.5));

        Entity espada = new Entity("item_espada", "Espada");
        espada.addComponent(new ItemComponent("espada_ferro", "Espada Curta", 1, 3, 3.0));

        ResultadoInsercaoGradeVO resPocao = useCase.executar(grid, pocao, 0, 0);
        assert resPocao.isSucesso() : "Poção deveria ser inserida com sucesso";
        assert grid.isSlotOcupado(0, 0) : "Slot (0,0) deveria estar ocupado";
        assert "item_pocao".equals(grid.getItemIdNoSlot(0, 0).orElse(null)) : "ID do item no slot incorreto";

        ResultadoInsercaoGradeVO resEspada = useCase.executar(grid, espada, 2, 1);
        assert resEspada.isSucesso() : "Espada deveria ser inserida com sucesso";
        assert grid.isSlotOcupado(2, 1) : "Slot (2,1) deveria estar ocupado";
        assert grid.isSlotOcupado(2, 2) : "Slot (2,2) deveria estar ocupado";
        assert grid.isSlotOcupado(2, 3) : "Slot (2,3) deveria estar ocupado";

        assert grid.getSlotsOcupadosContagem() == 4 : "Deveria ter 4 slots ocupados";
        assert grid.getSlotsLivresContagem() == 21 : "Deveria ter 21 slots livres";
        assert grid.getQuantidadeItens() == 2 : "Deveria ter 2 itens";
        assert Math.abs(grid.getPesoTotalItens() - 3.5) < 0.001 : "Peso incorreto";
    }

    public void deveInserirItemRotacionado() {
        InventoryGridComponent grid = new InventoryGridComponent(5, 5);
        InserirItemGradeUseCase useCase = new InserirItemGradeUseCase();

        Entity lanca = new Entity("item_lanca", "Lança");
        lanca.addComponent(new ItemComponent("lanca_madeira", "Lança de Madeira", 1, 3, 2.0, true));

        // Inserir na horizontal na linha Y=4 de X=1 até X=3 (largura efetiva 3, altura efetiva 1)
        ResultadoInsercaoGradeVO res = useCase.executar(grid, lanca, 1, 4, true);
        assert res.isSucesso() : "Lança rotacionada deveria ser inserida com sucesso";
        assert res.getItemInserido().isPresent() : "Item inserido ausente no resultado";
        assert res.getItemInserido().get().isRotacionado90() : "Deveria estar rotacionado 90 graus";
        assert res.getItemInserido().get().getLarguraEfetiva() == 3 : "Largura efetiva esperada 3";
        assert res.getItemInserido().get().getAlturaEfetiva() == 1 : "Altura efetiva esperada 1";

        assert grid.isSlotOcupado(1, 4) : "Slot (1,4) ocupado";
        assert grid.isSlotOcupado(2, 4) : "Slot (2,4) ocupado";
        assert grid.isSlotOcupado(3, 4) : "Slot (3,4) ocupado";
        assert !grid.isSlotOcupado(0, 4) : "Slot (0,4) livre";
        assert !grid.isSlotOcupado(4, 4) : "Slot (4,4) livre";
    }

    public void deveRejeitarColisao() {
        InventoryGridComponent grid = new InventoryGridComponent(5, 5);
        InserirItemGradeUseCase useCase = new InserirItemGradeUseCase();

        Entity escudo = new Entity("escudo_1", "Escudo");
        escudo.addComponent(new ItemComponent("escudo_madeira", "Escudo", 2, 2, 4.0));
        useCase.executar(grid, escudo, 1, 1);

        Entity machado = new Entity("machado_1", "Machado");
        machado.addComponent(new ItemComponent("machado_ferro", "Machado", 2, 2, 5.0));

        // Tenta inserir machado em (2, 2), que sobrepõe o slot (2, 2) do escudo
        ResultadoInsercaoGradeVO res = useCase.executar(grid, machado, 2, 2);
        assert !res.isSucesso() : "Deveria falhar por colisão";
        assert res.getMensagem().contains("Colisão de inventário") : "Mensagem esperada de colisão";

        assert grid.getQuantidadeItens() == 1 : "Não deve ter adicionado segundo item";
        assert grid.getSlotsOcupadosContagem() == 4 : "Slots ocupados devem permanecer 4";
    }

    public void deveRejeitarTransbordo() {
        InventoryGridComponent grid = new InventoryGridComponent(5, 5);
        InserirItemGradeUseCase useCase = new InserirItemGradeUseCase();

        Entity arco = new Entity("arco_1", "Arco Longo");
        arco.addComponent(new ItemComponent("arco_longo", "Arco Longo", 2, 4, 1.5));

        // Em uma grade 5x5, arco de altura 4 na linha Y=2 precisaria ir até Y=5 (fora da grade 0..4)
        ResultadoInsercaoGradeVO res = useCase.executar(grid, arco, 0, 2);
        assert !res.isSucesso() : "Deveria falhar por transbordo de limites";
        assert res.getMensagem().contains("transborda os limites") : "Mensagem esperada de transbordo";
    }

    public void deveRemoverItemEDesocuparSlots() {
        InventoryGridComponent grid = new InventoryGridComponent(5, 5);
        InserirItemGradeUseCase inserirUseCase = new InserirItemGradeUseCase();
        RemoverItemGradeUseCase removerUseCase = new RemoverItemGradeUseCase();

        Entity peitoral = new Entity("peitoral_instancia", "Peitoral");
        peitoral.addComponent(new ItemComponent("peitoral_ferro", "Peitoral de Ferro", 2, 3, 15.0));

        inserirUseCase.executar(grid, peitoral, 1, 1);
        assert grid.getSlotsOcupadosContagem() == 6 : "Slots ocupados esperados 6";

        var itemRemovidoOpt = removerUseCase.executar(grid, "peitoral_instancia");
        assert itemRemovidoOpt.isPresent() : "Item deveria ter sido removido";
        assert "peitoral_instancia".equals(itemRemovidoOpt.get().getItemInstanceId()) : "ID divergente";

        assert grid.getSlotsOcupadosContagem() == 0 : "Slots ocupados deve ser 0 após remoção";
        assert grid.getSlotsLivresContagem() == 25 : "Slots livres deve ser 25 após remoção";
        assert grid.getQuantidadeItens() == 0 : "Qtd itens deve ser 0";
        assert Math.abs(grid.getPesoTotalItens()) < 0.001 : "Peso deve ser 0";
    }

    public void deveRejeitarRotacaoNaoPermitida() {
        InventoryGridComponent grid = new InventoryGridComponent(5, 5);
        InserirItemGradeUseCase useCase = new InserirItemGradeUseCase();

        Entity itemFixo = new Entity("item_fixo", "Item Fixo");
        itemFixo.addComponent(new ItemComponent("id_fixo", "Frasco Especial", 1, 2, 1.0, false));

        ResultadoInsercaoGradeVO res = useCase.executar(grid, itemFixo, 0, 0, true);
        assert !res.isSucesso() : "Deveria falhar quando rotação é proibida";
        assert res.getMensagem().contains("não permite rotação") : "Mensagem esperada de proibição de rotação";
    }
}
