# Log de Histórico de Ciclo: Fase 2 - Entidades, Inventário em Grade & Sobrecarga

**Data:** 20 de Outubro de 2026  
**Status do Ciclo:** Concluído com 100% de Aprovação  
**Branch Ativa:** `fase-2`

---

## 📌 1. Contexto de Encadeamento
- **Fase / Subfase Anterior:** Fase 1 (Núcleo do Motor de Dados, Física de Materiais & Testes Ponderados).
- **Fase / Subfase Atual:** Fase 2 (Subfases 2.1 a 2.5 - Arquitetura ECS Enxuta, Inventário em Grade Bidimensional Estilo Tetris, Cálculo de Sobrecarga e Catálogo de Itens Base).
- **Próxima Fase / Subfase Planejada:** Fase 3 (Subfases 3.1 a 3.5 - Fisiologia do Personagem, Anatomia Localizada, Sistema de Dor e Foco).

---

## 🛠️ 2. O Que Foi Implementado

1. **Arquitetura ECS Enxuta (`com.omini.engine.domain.ecs`):**
   - [Component.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/ecs/Component.java): Interface marcadora pura para componentes de dados.
   - [Entity.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/ecs/Entity.java): Entidade identificada de forma única (`UUID`/`String`), com suporte a tags (`player`, `item`, `monstro`) e contêiner tipado de componentes com `Optional<T> getComponent(Class<T>)`.
   - Componentes em `com.omini.engine.domain.ecs.components`:
     - [ItemComponent.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/ecs/components/ItemComponent.java): Propriedades espaciais de itens ($W \times H$ em slots), peso em kg e flag de permissão de rotação.
     - [InventoryGridComponent.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/ecs/components/InventoryGridComponent.java): Matriz bidimensional de slots de inventário e dicionário de instâncias alocadas.
     - [MaterialComponent.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/ecs/components/MaterialComponent.java): Associação de material, densidade e dureza à entidade.
     - [EquipableComponent.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/ecs/components/EquipableComponent.java) e [SlotEquipamentoEnum.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/ecs/components/SlotEquipamentoEnum.java): Slots anatômicos (Mão Principal, Cabeça, Torso, Costas, Cinto, etc.).
     - [PositionComponent.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/ecs/components/PositionComponent.java): Posição espacial $(X, Y)$ no mapa.

2. **Sistema de Inventário em Grade Bidimensional (Estilo Tetris):**
   - [PositionedItemVO.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/dtos/PositionedItemVO.java): Value Object que calcula dinamicamente a largura/altura efetivas sob rotação de 90° e resolve a ocupação matricial dos slots.
   - [ResultadoInsercaoGradeVO.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/dtos/ResultadoInsercaoGradeVO.java): Resultado atômico de inserção com mensagens descritivas de sucesso ou falha.
   - [InserirItemGradeUseCase.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/usecases/InserirItemGradeUseCase.java): Caso de uso com validações rigorosas de coordenadas negativas, limites matriciais (transbordo), proibição de rotação e colisão célula a célula contra slots ocupados.
   - [RemoverItemGradeUseCase.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/usecases/RemoverItemGradeUseCase.java): Remoção atômica por ID de instância liberando os slots na matriz e recalculando o peso total.

3. **Cálculo de Sobrecarga (*Encumbrance*):**
   - [NivelSobrecargaEnum.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/models/NivelSobrecargaEnum.java): Faixas `LEVE`, `CONFORTAVEL`, `PESADA`, `CRITICA` e `INCAPACITADO`.
   - [EstadoSobrecargaVO.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/dtos/EstadoSobrecargaVO.java): Estado completo de carga contendo penalidades percentuais de Pontos de Ação (AP) e penalidades na dificuldade de esquiva.
   - [CalcularSobrecargaUseCase.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/usecases/CalcularSobrecargaUseCase.java): Avalia peso total contra Força do personagem (capacidade confortável: $Forca \times 5.0$ kg; capacidade máxima: $Forca \times 10.0$ kg).

4. **Catálogo Base de Itens JSON e Carregamento:**
   - Criação da pasta `Desenvolvimento/base_game/data/items/`:
     - [weapons.json](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/base_game/data/items/weapons.json): Espada curta de ferro ($1 \times 3$), adaga ($1 \times 2$), martelo colossal de chumbo ($2 \times 3$, $67.8$ kg), machado ($2 \times 3$), picareta ($2 \times 2$), clava ($1 \times 3$).
     - [armor.json](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/base_game/data/items/armor.json): Túnica de tecido ($2 \times 2$), peitoral de ferro ($2 \times 3$), elmo ($2 \times 2$), botas ($2 \times 2$), luvas ($1 \times 1$).
     - [tools.json](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/base_game/data/items/tools.json): Tocha de madeira, cantil, kit de gazua, corda.
     - [containers.json](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/base_game/data/items/containers.json): Bolsa de cintura ($2 \times 2$, grade interna $3 \times 3$), mochila pequena ($2 \times 3$, grade interna $4 \times 5$), mochila tática ($2 \times 4$, grade interna $6 \times 8$).
   - [ItemModel.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/models/ItemModel.java) e contrato [IItemRepository.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/repositories/IItemRepository.java).
   - [JsonItemRepository.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/data/repositories/JsonItemRepository.java): Carregamento e indexação em memória via `CascadeModLoader`.
   - [ItemEntityFactory.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/factories/ItemEntityFactory.java): Fábrica desacoplada que calcula a massa procedural do item a partir da densidade do material em `materials.json` e associa grades internas quando o item é um contêiner.

---

## 🔬 3. Testes Executados & Resultados (100% de Sucesso)

### Camada 1: Testes Unitários dos UseCases e ECS
- [EntityEcsTest](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/test/java/com/omini/engine/unit/EntityEcsTest.java): Aprovado (criação de entidade, tags case-insensitive, adição/remoção type-safe de componentes, validações fail-fast para IDs e componentes nulos).
- [InventarioGradeUseCaseTest](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/test/java/com/omini/engine/unit/InventarioGradeUseCaseTest.java): Aprovado (inserção de itens $1 \times 1$ e $1 \times 3$, rotação de 90° ocupando $3 \times 1$, rejeição de colisão de slots ocupados, rejeição de transbordo de limites, remoção limpa com liberação de slots e rejeição de rotação para itens fixos).
- [CalcularSobrecargaUseCaseTest](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/test/java/com/omini/engine/unit/CalcularSobrecargaUseCaseTest.java): Aprovado (classificação rigorosa nas faixas `LEVE`, `CONFORTAVEL`, `PESADA` (+15% AP, 20% esquiva), `CRITICA` (+40% AP, 50% esquiva) e `INCAPACITADO` (+100% AP e esquiva)).

### Camada 2: Testes de Integração
- [InventarioSobrecargaIntegrationTest](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/test/java/com/omini/engine/integration/InventarioSobrecargaIntegrationTest.java): Aprovado.
  - Carregamento real dos catálogos JSON (`materials.json`, `damage_types.json` e `items/*.json`) via `CascadeModLoader`.
  - Instanciação de `mochila_tatica` como entidade e contêiner com grade $6 \times 8$ (48 slots).
  - Instanciação de espada de ferro ($1 \times 3 \times 7.8 = 23.4$ kg), adaga de ferro ($1 \times 2 \times 7.8 = 15.6$ kg) e tocha de madeira macia ($1 \times 2 \times 1.2 = 2.4$ kg) com cálculo procedural de peso.
  - Alocação na grade da mochila ocupando 7 slots e acumulando $41.4$ kg.
  - Para um personagem de Força 8 (confortável 40 kg), constata-se a transição para estado `PESADA` (+15% AP).
  - Inserção do martelo de chumbo ($2 \times 3 \times 11.3 = 67.8$ kg) rotacionado 90° ($3 \times 2$). O peso salta para $109.2$ kg, ultrapassando o máximo de 80 kg e colocando o personagem em estado `INCAPACITADO`.
  - Remoção atômica do martelo com liberação dos 6 slots e retorno do estado para `PESADA`.

---

## ⚠️ 4. Desafios Encontrados & Como Foram Superados
- **Testes Autônomos Sem Dependência de Framework Externo:** Para manter total conformidade com o ambiente local (sem dependência de download de JARs do JUnit 5 ou Gradle Daemon), todos os testes foram projetados nativamente com `assert` do Java (`-ea`), métodos `main` executáveis e asserções matemáticas determinísticas.

---

## 🚀 5. Próximos Passos (Fase 3)
1. **Mapeamento Anatômico Completo:** Leitura e sorteio ponderado de membros corporais baseados em `anatomy.json` com área de superfície e cobertura contextual.
2. **Saúde Localizada, Sangramento e Fraturas:** Integridade física independente por membro e impacto mecânico na locomoção e ataque.
3. **Mecânica Tática de Dor e Farmacologia:** Acúmulo de dor, redução de precisão e tempo de liberação gradual de medicamentos.
4. **Sistema de Foco:** Teto de foco inversamente proporcional à dor acumulada e habilidade de mira cirúrgica.
