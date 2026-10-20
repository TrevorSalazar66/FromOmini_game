package com.omini.engine.unit;

import com.omini.engine.domain.dtos.BodyPartStateVO;
import com.omini.engine.domain.dtos.ResultadoDanoLocalizadoVO;
import com.omini.engine.domain.ecs.Entity;
import com.omini.engine.domain.ecs.components.AnatomyComponent;
import com.omini.engine.domain.ecs.components.PainFocusComponent;
import com.omini.engine.domain.usecases.AplicarDanoLocalizadoUseCase;

import java.util.LinkedHashMap;
import java.util.Map;

public final class AplicarDanoLocalizadoUseCaseTest {

    public static void main(String[] args) {
        AplicarDanoLocalizadoUseCaseTest test = new AplicarDanoLocalizadoUseCaseTest();
        test.deveAplicarDanoNaoLetal();
        test.deveCausarFraturaAoZerarMembroNaoVital();
        test.deveCausarMorteAoZerarMembroVital();
        System.out.println("✅ AplicarDanoLocalizadoUseCaseTest: Todos os testes passaram!");
    }

    private Entity criarAlvoTeste() {
        Entity entity = new Entity("alvo_01", "Guerreiro Alvo");
        Map<String, BodyPartStateVO> partes = new LinkedHashMap<>();
        partes.put("head", BodyPartStateVO.criarSaudavel("head", "Cabeça", 30.0, true));
        partes.put("torso", BodyPartStateVO.criarSaudavel("torso", "Tronco", 60.0, true));
        partes.put("arm_r", BodyPartStateVO.criarSaudavel("arm_r", "Braço Direito", 20.0, false));

        entity.addComponent(new AnatomyComponent("humanoide", 100.0, partes));
        entity.addComponent(new PainFocusComponent());
        return entity;
    }

    public void deveAplicarDanoNaoLetal() {
        Entity alvo = criarAlvoTeste();
        AplicarDanoLocalizadoUseCase useCase = new AplicarDanoLocalizadoUseCase();

        ResultadoDanoLocalizadoVO res = useCase.executar(alvo, "arm_r", 8.0, "slash");
        assert Math.abs(res.getHpRestante() - 12.0) < 0.001 : "HP restante deveria ser 12.0";
        assert !res.isCausouFratura() : "Não deveria causar fratura com 8 de dano";
        assert !res.isCausouMorte() : "Não deveria causar morte";
        assert res.getSangramentoGerado() > 0.0 : "Dano de corte de 8 deve abrir sangramento";
        assert res.getDorGerada() > 0.0 : "Deveria ter gerado dor";

        AnatomyComponent anatomy = alvo.getComponent(AnatomyComponent.class).orElseThrow();
        assert !anatomy.isMorto();
    }

    public void deveCausarFraturaAoZerarMembroNaoVital() {
        Entity alvo = criarAlvoTeste();
        AplicarDanoLocalizadoUseCase useCase = new AplicarDanoLocalizadoUseCase();

        // Braço tem 20 de HP. Dano de 25 deve zerá-lo e fraturar
        ResultadoDanoLocalizadoVO res = useCase.executar(alvo, "arm_r", 25.0, "blunt");
        assert res.getHpRestante() == 0.0 : "HP restante deve ser 0";
        assert res.isCausouFratura() : "Deveria ter causado fratura";
        assert !res.isCausouMorte() : "Braço quebrado não deve matar a criatura";

        AnatomyComponent anatomy = alvo.getComponent(AnatomyComponent.class).orElseThrow();
        assert !anatomy.isMorto();
        assert anatomy.isMembroFraturado("arm_r");
        assert !alvo.hasTag("morto");
    }

    public void deveCausarMorteAoZerarMembroVital() {
        Entity alvo = criarAlvoTeste();
        AplicarDanoLocalizadoUseCase useCase = new AplicarDanoLocalizadoUseCase();

        // Cabeça tem 30 de HP e é vital. Dano de 35 deve matar
        ResultadoDanoLocalizadoVO res = useCase.executar(alvo, "head", 35.0, "blunt");
        assert res.getHpRestante() == 0.0 : "Cabeça deve ter 0 HP";
        assert res.isCausouMorte() : "Deveria ter causado morte imediata";

        AnatomyComponent anatomy = alvo.getComponent(AnatomyComponent.class).orElseThrow();
        assert anatomy.isMorto() : "Anatomia deve reportar morte";
        assert alvo.hasTag("morto") : "Entidade deve ter a tag 'morto'";
    }
}
