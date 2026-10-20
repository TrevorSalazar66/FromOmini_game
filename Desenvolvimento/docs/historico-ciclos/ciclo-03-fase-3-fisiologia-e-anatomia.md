# Log de Histórico de Ciclo: Fase 3 - Fisiologia do Personagem, Anatomia, Dor & Foco

**Data:** 20 de Outubro de 2026  
**Status do Ciclo:** Concluído com 100% de Aprovação  
**Branch Ativa:** `fase-3`

---

## 📌 1. Contexto de Encadeamento
- **Fase / Subfase Anterior:** Fase 2 (Entidades ECS, Inventário em Grade Tetris & Cálculo de Sobrecarga).
- **Fase / Subfase Atual:** Fase 3 (Subfases 3.1 a 3.5 - Fisiologia Anatômica, Combate Localizado, Fraturas, Sistema de Dor, Analgésicos e Foco Mental).
- **Próxima Fase / Subfase Planejada:** Fase 4 (Subfases 4.1 a 4.4 - Grid Espacial, Pathfinding A*, Alquimia Elemental e Motor de Turnos com Pontos de Ação).

---

## 🛠️ 2. O Que Foi Implementado

1. **Modelos e Repositório de Anatomia (`com.omini.engine.domain.models` / `data.repositories`):**
   - [BodyPartDefinitionModel.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/models/BodyPartDefinitionModel.java): Modelo imutável com parâmetros anatômicos (`id`, `name`, `hit_weight`, `vital`, `base_hp_ratio`, `armor_slot`).
   - [AnatomyDefinitionModel.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/models/AnatomyDefinitionModel.java): Modelo imutável com o conjunto de membros de uma espécie.
   - [IAnatomyRepository.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/repositories/IAnatomyRepository.java): Contrato de repositório.
   - [JsonAnatomyRepository.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/data/repositories/JsonAnatomyRepository.java): Implementação para leitura de [anatomy.json](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/base_game/data/anatomy.json) com suporte ao [CascadeModLoader.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/data/loader/CascadeModLoader.java).

2. **Componentes ECS de Fisiologia e Sobrevivência:**
   - [BodyPartStateVO.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/dtos/BodyPartStateVO.java): Estado vivo do membro com HP atual/máximo, flags de fratura, taxa de sangramento ativo, curativo e tala.
   - [AnatomyComponent.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/ecs/components/AnatomyComponent.java): Estado corporal total, letalidade vital (`isMorto()`), contagem de fraturas e soma de sangramento hemorrágico.
   - [PainFocusComponent.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/ecs/components/PainFocusComponent.java): Dor real, nível de analgésico no sangue, dor efetiva ($DorReal - Analgésico$) e teto dinâmico de foco ($100 - DorEfetiva$).
   - [SurvivalComponent.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/ecs/components/SurvivalComponent.java): Fome, Sede, Estamina e Sono discretizados de 0 a 100.
   - [StatsComponent.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/ecs/components/StatsComponent.java): Atributos centrais (Força, Destreza, Constituição, Percepção, Inteligência) e cálculo do HP base ($CON \times 10$).

3. **Casos de Uso Anatômicos & Combate Localizado:**
   - [SortearMembroAtingidoUseCase.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/usecases/SortearMembroAtingidoUseCase.java): Sorteio por área de superfície (`hit_weight`), cobertura física (anulação de membros inferiores atrás de muretas/trincheiras) e mira cirúrgica com Foco.
   - [AplicarDanoLocalizadoUseCase.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/usecases/AplicarDanoLocalizadoUseCase.java): Aplicação de dano localizada por membro, fraturas automáticas ao atingir 0 HP, sangramento proporcional, geração de dor e morte fulminante ao destruir membro vital (`vital == true`).
   - [TratarFerimentoUseCase.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/usecases/TratarFerimentoUseCase.java): Procedimentos de primeiros socorros (estancar sangramento com curativo, imobilizar fratura com tala para reduzir dor, e ministrar analgésicos).

4. **Casos de Uso de Dor, Foco e Metabolismo:**
   - [CalcularPenalidadesDorUseCase.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/usecases/CalcularPenalidadesDorUseCase.java): Classificação de dor e penalidades percentuais escalonadas nas rolagens de ação e foco mental.
   - [ProcessarCicloTurnoFisiologicoUseCase.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/usecases/ProcessarCicloTurnoFisiologicoUseCase.java): Passagem de turnos com perda de HP contínua em feridas hemorrágicas não enfaixadas, decaimento de analgésicos no sangue, regeneração de foco e consumo de sede/fome.

---

## 🔬 3. Testes Executados & Resultados (100% de Sucesso)

### Camada 1: Testes Unitários dos UseCases Fisiológicos
- [SortearMembroAtingidoUseCaseTest](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/test/java/com/omini/engine/unit/SortearMembroAtingidoUseCaseTest.java): Aprovado (proporção estatística em 10.000 repetições, anulação de 100% dos membros inferiores sob cobertura de trincheira e aumento de probabilidade de acerto na cabeça com mira por Foco).
- [AplicarDanoLocalizadoUseCaseTest](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/test/java/com/omini/engine/unit/AplicarDanoLocalizadoUseCaseTest.java): Aprovado (dano não letal no braço, fratura ao zerar HP de membro não vital com geração de dor/sangramento sem morte, e morte clínica fulminante ao zerar membro vital).
- [DorEFocoFisiologiaTest](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/test/java/com/omini/engine/unit/DorEFocoFisiologiaTest.java): Aprovado (acúmulo de dor derrubando o teto de foco de 100 para 60; administração de analgésico aliviando a dor efetiva e reerguendo o teto para 85; estancamento de sangramento ativo com curativo; e redução de dor aguda ao colocar tala na fratura).

### Camada 2: Testes de Integração
- [FisiologiaAnatomiaIntegrationTest](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/test/java/com/omini/engine/integration/FisiologiaAnatomiaIntegrationTest.java): Aprovado.
  - Carregamento real dos 14 membros corporais humanos de `anatomy.json` via `CascadeModLoader`.
  - Construção do guerreiro sobrevivente no ECS com atributos, anatomia, sobrevivência e dor/foco.
  - Teste de combate com cobertura e mira cirúrgica na cabeça.
  - Fratura e hemorragia no antebraço gerando dor e debuffs de concentração.
  - Avanço de turnos com sangramento ativo e consumo metabólico.
  - Socorro tático em combate: curativo para estancar hemorragia, tala na fratura e dose de analgésico.
  - Dano letal fulminante no tronco vital levando o alvo à morte imediata (`isMorto() == true`).

---

## ⚠️ 4. Desafios Encontrados & Como Foram Superados
- **Compatibilidade Funcional com `IRandomProvider`:** A interface nativa utiliza assinatura `long proximoLong(long limiteSuperiorExclusivo)`. As lambdas de teste e use cases foram ajustadas para conversão explícita com segurança contra perda de precisão, mantendo execução pura sem frameworks externos.

---

## 🚀 5. Próximos Passos (Fase 4)
1. **Grid Espacial & Pathfinding A*:** Matriz bidimensional de tiles com custos de movimento, bloqueios físicos e visibilidade (*Field of View* / Shadowcasting).
2. **Superfícies Reativas & Alquimia Elemental:** Reações turno a turno lidas de `environmental_reactions.json` (fogo se espalhando em grama/madeira, vapor tóxico, choque elétrico em poças de água).
3. **Motor de Turnos & Pontos de Ação (AP):** Sistema de iniciativa e consumo de pontos de ação onde o peso, ferimentos e debuffs corporais retardam a velocidade do personagem.
4. **Gerador Procedural de Mapas Baseado em Dados:** Presets de salas (*prefabs*) e montagem procedural de masmorras/ambientes a partir de JSONs.
