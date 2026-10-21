package com.omini.engine.unit;

import com.omini.engine.domain.dtos.BodyPartStateVO;
import com.omini.engine.domain.ecs.Entity;
import com.omini.engine.domain.ecs.components.AnatomyComponent;
import com.omini.engine.ui.widgets.AnatomyWidget;

import java.util.List;

public final class AnatomyWidgetTest {

    public static void main(String[] args) {
        AnatomyWidgetTest test = new AnatomyWidgetTest();
        test.deveMapearCoresCorretamentePorEstadoDeSaude();
        test.deveDisponibilizarAcoesTratamentoParaMembroLesionado();
        test.deveAplicarTratamentoComSucesso();
        System.out.println("✅ AnatomyWidgetTest: Todos os testes passaram!");
    }

    public void deveMapearCoresCorretamentePorEstadoDeSaude() {
        AnatomyWidget widget = new AnatomyWidget();
        Entity paciente = new Entity("p1", "Paciente");

        AnatomyComponent anatomy = new AnatomyComponent("humanoid", 100.0);
        // Membro 1: Íntegro (100% -> Verde #4CAF50)
        anatomy.adicionarParte(new BodyPartStateVO("head", "Cabeça", 75.0, 75.0, true, false, 0.0, false, false));
        // Membro 2: Moderado (50% -> Laranja #FF9800)
        anatomy.adicionarParte(new BodyPartStateVO("torso", "Tronco", 50.0, 100.0, true, false, 0.0, false, false));
        // Membro 3: Crítico (20% -> Vermelho #F44336)
        anatomy.adicionarParte(new BodyPartStateVO("arm", "Braço", 8.0, 40.0, false, false, 2.0, false, false));
        // Membro 4: Fraturado (0% -> Preto #212121)
        anatomy.adicionarParte(new BodyPartStateVO("leg", "Perna", 0.0, 50.0, false, true, 0.0, false, false));

        paciente.addComponent(anatomy);

        List<AnatomyWidget.MembroVisualDTO> silhueta = widget.extrairSilhueta(paciente);
        assert silhueta.size() == 4;

        var head = silhueta.stream().filter(m -> "head".equals(m.id())).findFirst().orElseThrow();
        assert "#4CAF50".equals(head.corHex()) : "Cabeça íntegra deve ser verde";

        var torso = silhueta.stream().filter(m -> "torso".equals(m.id())).findFirst().orElseThrow();
        assert "#FF9800".equals(torso.corHex()) : "Tronco 50% deve ser laranja";

        var arm = silhueta.stream().filter(m -> "arm".equals(m.id())).findFirst().orElseThrow();
        assert "#F44336".equals(arm.corHex()) : "Braço crítico deve ser vermelho";
        assert arm.sangrando() : "Braço deve indicar sangramento";

        var leg = silhueta.stream().filter(m -> "leg".equals(m.id())).findFirst().orElseThrow();
        assert "#212121".equals(leg.corHex()) : "Perna fraturada deve ser preta/cinza";
        assert leg.fraturado();
    }

    public void deveDisponibilizarAcoesTratamentoParaMembroLesionado() {
        AnatomyWidget widget = new AnatomyWidget();
        Entity paciente = new Entity("p1", "Paciente");
        AnatomyComponent anatomy = new AnatomyComponent("humanoid", 100.0);
        anatomy.adicionarParte(new BodyPartStateVO("arm", "Braço", 10.0, 40.0, false, true, 3.0, false, false));
        paciente.addComponent(anatomy);

        var acoes = widget.obterAcoesTratamento(paciente, "arm");
        assert acoes.stream().anyMatch(a -> "curativo".equals(a.id())) : "Deve permitir curativo em ferida sangrando";
        assert acoes.stream().anyMatch(a -> "tala".equals(a.id())) : "Deve permitir tala em membro fraturado";
    }

    public void deveAplicarTratamentoComSucesso() {
        AnatomyWidget widget = new AnatomyWidget();
        Entity paciente = new Entity("p1", "Paciente");
        AnatomyComponent anatomy = new AnatomyComponent("humanoid", 100.0);
        anatomy.adicionarParte(new BodyPartStateVO("arm", "Braço", 10.0, 40.0, false, true, 3.0, false, false));
        paciente.addComponent(anatomy);

        boolean curativoOk = widget.aplicarTratamento(paciente, "arm", "curativo");
        assert curativoOk : "Aplicação de curativo deve retornar true";
        assert anatomy.getParte("arm").orElseThrow().isEnfaixado() : "Membro deve estar enfaixado";
    }
}
