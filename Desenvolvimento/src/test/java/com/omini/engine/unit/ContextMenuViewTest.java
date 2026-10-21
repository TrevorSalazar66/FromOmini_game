package com.omini.engine.unit;

import com.omini.engine.domain.dtos.AcaoContextualVO;
import com.omini.engine.domain.ecs.Entity;
import com.omini.engine.domain.ecs.components.InventoryGridComponent;
import com.omini.engine.domain.models.TileTypeEnum;
import com.omini.engine.ui.views.ContextMenuView;

import java.util.List;

public final class ContextMenuViewTest {

    public static void main(String[] args) {
        ContextMenuViewTest test = new ContextMenuViewTest();
        test.deveGerarAcoesContextuaisParaJogador();
        test.deveGerarAcoesContextuaisParaItemEContainer();
        test.deveGerarAcoesContextuaisParaMonstro();
        test.deveGerarAcoesContextuaisParaPorta();
        System.out.println("✅ ContextMenuViewTest: Todos os testes passaram!");
    }

    public void deveGerarAcoesContextuaisParaJogador() {
        ContextMenuView menu = new ContextMenuView();
        Entity jogador = new Entity("p1", "Jogador");
        jogador.addTag("player");

        menu.abrirParaEntidade(jogador, 200, 300, true);
        assert menu.isVisivel() : "Menu deve estar visível";
        assert menu.getTelaX() == 200;
        assert menu.getTelaY() == 300;

        List<AcaoContextualVO> acoes = menu.getAcoesAtivas();
        assert acoes.stream().anyMatch(a -> "abrir_inventario".equals(a.id())) : "Deveria ter ação de abrir inventário";
        assert acoes.stream().anyMatch(a -> "primeiros_socorros".equals(a.id())) : "Deveria ter ação de primeiros socorros";
        assert acoes.stream().anyMatch(a -> "descansar".equals(a.id())) : "Deveria ter ação de descansar";
    }

    public void deveGerarAcoesContextuaisParaItemEContainer() {
        ContextMenuView menu = new ContextMenuView();
        Entity mochila = new Entity("m1", "Mochila Caída");
        mochila.addTag("item");
        mochila.addComponent(new InventoryGridComponent(4, 5));

        menu.abrirParaEntidade(mochila, 150, 150, true);
        List<AcaoContextualVO> acoes = menu.getAcoesAtivas();
        assert acoes.stream().anyMatch(a -> "pegar".equals(a.id())) : "Deveria ter opção pegar";
        assert acoes.stream().anyMatch(a -> "abrir_container".equals(a.id())) : "Deveria ter opção abrir container";
        assert acoes.stream().anyMatch(a -> "examinar".equals(a.id()));

        // Distante: deve desabilitar interação física
        menu.abrirParaEntidade(mochila, 150, 150, false);
        assert menu.getAcoesAtivas().stream().anyMatch(a -> !a.habilitado()) : "Ações físicas devem estar desabilitadas à distância";
    }

    public void deveGerarAcoesContextuaisParaMonstro() {
        ContextMenuView menu = new ContextMenuView();
        Entity zumbi = new Entity("z1", "Zumbi Rastejante");
        zumbi.addTag("monstro");

        menu.abrirParaEntidade(zumbi, 100, 100, true);
        assert menu.getAcoesAtivas().stream().anyMatch(a -> "atacar".equals(a.id())) : "Deveria ter opção atacar adjacente";

        menu.abrirParaEntidade(zumbi, 100, 100, false);
        assert menu.getAcoesAtivas().stream().anyMatch(a -> "disparar".equals(a.id())) : "Deveria ter opção mirar/disparar à distância";
    }

    public void deveGerarAcoesContextuaisParaPorta() {
        ContextMenuView menu = new ContextMenuView();
        menu.abrirParaTile(TileTypeEnum.PORTA_FECHADA, 50, 50, true);
        assert menu.getAcoesAtivas().stream().anyMatch(a -> "abrir_porta".equals(a.id()));
        assert menu.getAcoesAtivas().stream().anyMatch(a -> "arrombar".equals(a.id()));
    }
}
