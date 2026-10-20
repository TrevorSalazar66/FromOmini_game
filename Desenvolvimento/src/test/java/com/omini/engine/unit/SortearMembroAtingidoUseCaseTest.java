package com.omini.engine.unit;

import com.omini.engine.domain.dtos.AtaqueContextoDTO;
import com.omini.engine.domain.models.AnatomyDefinitionModel;
import com.omini.engine.domain.models.BodyPartDefinitionModel;
import com.omini.engine.domain.models.IRandomProvider;
import com.omini.engine.domain.usecases.SortearMembroAtingidoUseCase;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public final class SortearMembroAtingidoUseCaseTest {

    public static void main(String[] args) {
        SortearMembroAtingidoUseCaseTest test = new SortearMembroAtingidoUseCaseTest();
        test.deveSortearMembrosProporcionalmente();
        test.deveAnularMembrosInferioresSobCobertura();
        test.deveDirecionarAcertoComFoco();
        System.out.println("✅ SortearMembroAtingidoUseCaseTest: Todos os testes passaram!");
    }

    private AnatomyDefinitionModel criarAnatomiaTeste() {
        Map<String, BodyPartDefinitionModel> partes = new LinkedHashMap<>();
        partes.put("head", new BodyPartDefinitionModel("head", "Cabeça", 10, true, 0.75, "head"));
        partes.put("torso", new BodyPartDefinitionModel("torso", "Tronco", 40, true, 1.0, "torso"));
        partes.put("arm_r", new BodyPartDefinitionModel("arm_r", "Braço Direito", 10, false, 0.40, "arms"));
        partes.put("arm_l", new BodyPartDefinitionModel("arm_l", "Braço Esquerdo", 10, false, 0.40, "arms"));
        partes.put("thigh_r", new BodyPartDefinitionModel("thigh_r", "Coxa Direita", 15, false, 0.60, "legs"));
        partes.put("thigh_l", new BodyPartDefinitionModel("thigh_l", "Coxa Esquerda", 15, false, 0.60, "legs"));
        return new AnatomyDefinitionModel("teste_humanoide", "Teste", partes);
    }

    public void deveSortearMembrosProporcionalmente() {
        AnatomyDefinitionModel anatomia = criarAnatomiaTeste();
        Random rng = new Random(42);
        SortearMembroAtingidoUseCase useCase = new SortearMembroAtingidoUseCase(limite -> rng.nextInt((int) limite));

        AtaqueContextoDTO contexto = AtaqueContextoDTO.padrao(anatomia);

        Map<String, Integer> contagem = new LinkedHashMap<>();
        int repeticoes = 10000;
        for (int i = 0; i < repeticoes; i++) {
            String membro = useCase.executar(contexto);
            contagem.put(membro, contagem.getOrDefault(membro, 0) + 1);
        }

        // Tronco tem peso 40 de 100 (40%), Cabeça tem peso 10 (10%)
        int torsoCount = contagem.getOrDefault("torso", 0);
        int headCount = contagem.getOrDefault("head", 0);

        assert torsoCount > 3500 && torsoCount < 4500 : "Tronco deveria ter por volta de 40% das ocorrências, obtido: " + torsoCount;
        assert headCount > 700 && headCount < 1300 : "Cabeça deveria ter por volta de 10% das ocorrências, obtido: " + headCount;
    }

    public void deveAnularMembrosInferioresSobCobertura() {
        AnatomyDefinitionModel anatomia = criarAnatomiaTeste();
        Random rng = new Random(123);
        SortearMembroAtingidoUseCase useCase = new SortearMembroAtingidoUseCase(limite -> rng.nextInt((int) limite));

        // Cobertura inferior ativa
        AtaqueContextoDTO contexto = new AtaqueContextoDTO(anatomia, true, null, 0.0);

        for (int i = 0; i < 1000; i++) {
            String membro = useCase.executar(contexto);
            assert !membro.contains("thigh") : "Membro inferior atingido apesar da cobertura: " + membro;
        }
    }

    public void deveDirecionarAcertoComFoco() {
        AnatomyDefinitionModel anatomia = criarAnatomiaTeste();
        Random rng = new Random(999);
        SortearMembroAtingidoUseCase useCase = new SortearMembroAtingidoUseCase(limite -> rng.nextInt((int) limite));

        // Gastando 90 de foco visando a cabeça (multiplica o peso de 10 por 10x -> peso 100 em um total de ~190)
        AtaqueContextoDTO contexto = new AtaqueContextoDTO(anatomia, false, "head", 90.0);

        int headCount = 0;
        int total = 1000;
        for (int i = 0; i < total; i++) {
            String membro = useCase.executar(contexto);
            if ("head".equals(membro)) headCount++;
        }

        // Sem foco seria ~10%, com foco salta para mais de 45%
        assert headCount > 400 : "Foco não direcionou o acerto na cabeça: apenas " + headCount + "/" + total;
    }
}
