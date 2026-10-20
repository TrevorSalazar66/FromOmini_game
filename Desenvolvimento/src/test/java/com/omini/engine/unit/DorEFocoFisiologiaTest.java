package com.omini.engine.unit;

import com.omini.engine.domain.dtos.BodyPartStateVO;
import com.omini.engine.domain.dtos.EstadoDorVO;
import com.omini.engine.domain.ecs.Entity;
import com.omini.engine.domain.ecs.components.AnatomyComponent;
import com.omini.engine.domain.ecs.components.PainFocusComponent;
import com.omini.engine.domain.models.TipoTratamentoEnum;
import com.omini.engine.domain.usecases.CalcularPenalidadesDorUseCase;
import com.omini.engine.domain.usecases.TratarFerimentoUseCase;

import java.util.LinkedHashMap;
import java.util.Map;

public final class DorEFocoFisiologiaTest {

    public static void main(String[] args) {
        DorEFocoFisiologiaTest test = new DorEFocoFisiologiaTest();
        test.deveControlarDorEModificarTetoDeFoco();
        test.deveAtenuarDorComAnalgesico();
        test.deveEstancarSangramentoComCurativo();
        test.deveImobilizarFraturaComTala();
        System.out.println("✅ DorEFocoFisiologiaTest: Todos os testes passaram!");
    }

    public void deveControlarDorEModificarTetoDeFoco() {
        PainFocusComponent painFocus = new PainFocusComponent(0.0, 100.0);
        CalcularPenalidadesDorUseCase useCase = new CalcularPenalidadesDorUseCase();

        assert painFocus.getTetoFoco() == 100.0;
        assert painFocus.getFocoAtual() == 100.0;

        // Adiciona 40 de dor
        painFocus.adicionarDor(40.0);
        assert painFocus.getDorReal() == 40.0;
        assert painFocus.getDorEfetiva() == 40.0;
        assert painFocus.getTetoFoco() == 60.0 : "Teto deveria ser 60 (100 - 40)";
        assert painFocus.getFocoAtual() == 60.0 : "Foco atual deveria ter sido truncado no teto";

        EstadoDorVO estado = useCase.executar(painFocus);
        assert "Dor Moderada".equals(estado.getClassificacao());
        assert estado.getPenalidadeRolagensPercentual() == 0.15;
    }

    public void deveAtenuarDorComAnalgesico() {
        PainFocusComponent painFocus = new PainFocusComponent(60.0, 100.0);
        CalcularPenalidadesDorUseCase useCase = new CalcularPenalidadesDorUseCase();

        assert painFocus.getTetoFoco() == 40.0;

        // Administra analgésico de dosagem 35
        painFocus.administrarAnalgesico(35.0);
        assert painFocus.getNivelAnalgesico() == 35.0;
        assert painFocus.getDorEfetiva() == 25.0 : "Dor efetiva deveria ser 60 - 35 = 25";
        assert painFocus.getTetoFoco() == 75.0 : "Teto de foco deveria subir para 100 - 25 = 75";

        EstadoDorVO estado = useCase.executar(painFocus);
        assert "Dor Leve".equals(estado.getClassificacao());
        assert estado.getPenalidadeRolagensPercentual() == 0.05;
    }

    public void deveEstancarSangramentoComCurativo() {
        Entity paciente = new Entity("paciente_1", "Sobrevivente");
        Map<String, BodyPartStateVO> partes = new LinkedHashMap<>();
        // Braço com dano e sangramento de 4.0 por turno
        BodyPartStateVO braco = new BodyPartStateVO("arm_l", "Braço Esquerdo", 10.0, 20.0, false, false, 4.0, false, false);
        partes.put("arm_l", braco);

        paciente.addComponent(new AnatomyComponent("humanoide", 100.0, partes));

        TratarFerimentoUseCase tratarUseCase = new TratarFerimentoUseCase();
        boolean sucesso = tratarUseCase.executar(paciente, "arm_l", TipoTratamentoEnum.CURATIVO_SANGRAMENTO);
        assert sucesso : "Curativo deveria ser aplicado com sucesso";

        AnatomyComponent anatomy = paciente.getComponent(AnatomyComponent.class).orElseThrow();
        BodyPartStateVO tratado = anatomy.getParte("arm_l").orElseThrow();
        assert tratado.isEnfaixado() : "Deveria estar enfaixado";
        assert tratado.getSangramentoPorTurno() == 0.0 : "Sangramento ativo deveria ser 0";
    }

    public void deveImobilizarFraturaComTala() {
        Entity paciente = new Entity("paciente_2", "Sobrevivente");
        Map<String, BodyPartStateVO> partes = new LinkedHashMap<>();
        // Perna fraturada
        BodyPartStateVO perna = new BodyPartStateVO("leg_r", "Perna Direita", 0.0, 30.0, false, true, 0.0, false, false);
        partes.put("leg_r", perna);

        paciente.addComponent(new AnatomyComponent("humanoide", 100.0, partes));
        paciente.addComponent(new PainFocusComponent(50.0, 50.0));

        TratarFerimentoUseCase tratarUseCase = new TratarFerimentoUseCase();
        boolean sucesso = tratarUseCase.executar(paciente, "leg_r", TipoTratamentoEnum.TALA_FRATURA);
        assert sucesso : "Tala deveria ser aplicada com sucesso";

        AnatomyComponent anatomy = paciente.getComponent(AnatomyComponent.class).orElseThrow();
        BodyPartStateVO imobilizada = anatomy.getParte("leg_r").orElseThrow();
        assert imobilizada.isImobilizadoComTala();

        // Aplicar tala reduz dor aguda de fratura
        PainFocusComponent pain = paciente.getComponent(PainFocusComponent.class).orElseThrow();
        assert pain.getDorReal() == 40.0 : "Dor deveria ter sido reduzida em 10 pontos";
    }
}
