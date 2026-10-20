# Log de Histórico de Ciclo: Fase 4 - Grid Espacial, Alquimia Elemental & Turnos (Pontos de Ação)

**Data:** 20 de Outubro de 2026  
**Status do Ciclo:** Concluído com 100% de Aprovação  
**Branch Ativa:** `fase-4`

---

## 📌 1. Contexto de Encadeamento
- **Fase / Subfase Anterior:** Fase 3 (Fisiologia Anatômica, Combate Localizado, Fraturas, Dor e Foco).
- **Fase / Subfase Atual:** Fase 4 (Subfases 4.1 a 4.6 - Grade Matricial 2D, FOV por Bresenham, Pathfinding A*, Alquimia Elemental com Reações Reativas, Motor de Turnos baseado em AP com Penalidades Fisiológicas/Sobrecarga e Gerador Procedural de Mapas).
- **Próxima Fase / Subfase Planejada:** Conclusão do README.md abrangente e transição para IA de Criaturas / Estados Mentais (Fase 5).

---

## 🛠️ 2. O Que Foi Implementado

1. **Topologia Espacial & Grade Bidimensional (`com.omini.engine.domain.models` / `dtos`):**
   - [TileTypeEnum.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/models/TileTypeEnum.java): Terrenos e superfícies (`CHAO_PEDRA`, `PAREDE_GRANITO`, `PORTA`, `AGUA_RASA`, `GRAMA`) com custo de movimento, passabilidade e opacidade para visão.
   - [TileMap.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/models/TileMap.java): Grade espacial $W \times H$, verificação de limites, consulta de custos, passabilidade, bloqueio de visão dinâmico e gestão de entidades localizadas no tile.
   - [CoordenadaVO.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/dtos/CoordenadaVO.java): Value Object bidimensional com cálculos de distância Manhattan, Euclidiana e Chebyshev.

2. **Linha de Visão (FOV) & Pathfinding A* (`com.omini.engine.domain.usecases`):**
   - [CalcularLinhaDeVisaoUseCase.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/usecases/CalcularLinhaDeVisaoUseCase.java): Traçado de raios discreto de Bresenham, calculando se há linha de visão desimpedida entre dois pontos com bloqueio dinâmico por paredes sólidas e nuvens de fumaça/vapor.
   - [NavegarPathfindingAStarUseCase.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/usecases/NavegarPathfindingAStarUseCase.java): Algoritmo de busca A* determinístico em 8 direções, com fila de prioridade, custos ponderados de terreno e prevenção estrita de corte de quinas em paredes.

3. **Alquimia Elemental & Superfícies Reativas (`com.omini.engine.data` / `domain`):**
   - [environmental_reactions.json](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/base_game/data/environmental_reactions.json): Catálogo oficial com reações reativas de extinção (`surface_fire` + `surface_water` -> `field_smoke`), detonação em área (`surface_fire` + `flammable_gas` -> explosão 60 poder) e eletrificação de poça (`surface_electric` + `surface_water` -> choque contínuo de 25 dano/turno).
   - [EnvironmentalReactionModel.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/models/EnvironmentalReactionModel.java) e [IEnvironmentalReactionRepository.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/repositories/IEnvironmentalReactionRepository.java).
   - [JsonEnvironmentalReactionRepository.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/data/repositories/JsonEnvironmentalReactionRepository.java): Parser e repositório de reações.
   - [SurfaceFieldComponent.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/ecs/components/SurfaceFieldComponent.java): Componente ECS para campos de superfície com tag, duração em turnos e dano contínuo.
   - [ResultadoReacaoVO.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/dtos/ResultadoReacaoVO.java): Resultado completo de reações ambientais.
   - [ProcessarReacaoAmbientalUseCase.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/usecases/ProcessarReacaoAmbientalUseCase.java): Identifica interações de elementos nos tiles, consome os reagentes e instancia novos campos (fumaça, poça eletrificada, resíduos).

4. **Motor de Turnos & Pontos de Ação (AP) Dinâmicos:**
   - [TipoAcaoEnum.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/models/TipoAcaoEnum.java): Custos base de AP: `MOVER` (100), `ATACAR` (120), `USAR_ITEM` (80), `RECARREGAR` (150), `ESPERAR` (100).
   - [CalcularCustoAcaoAPUseCase.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/usecases/CalcularCustoAcaoAPUseCase.java): Modifica os custos de AP integrando a sobrecarga da Fase 2 (+15% a +100%), pernas fraturadas da Fase 3 (+100% de esforço em locomoção; +40% com tala) e atraso neural por dor aguda (> 20 de dor).
   - [TurnEnergyComponent.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/ecs/components/TurnEnergyComponent.java): Reserva de energia e prontidão para ação (limiar de 100 AP).
   - [MotorTurnosAPUseCase.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/usecases/MotorTurnosAPUseCase.java): Relógio global de ticks de tempo, acúmulo de energia baseado na velocidade ($100 + DES \times 2$) e consumo de AP.

5. **Gerador Procedural de Mapas Baseado em Dados:**
   - [ParametrosGeracaoMapaDTO.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/dtos/ParametrosGeracaoMapaDTO.java): Configuração de largura, altura, salas, tamanhos e seed.
   - [ResultadoGeracaoMapaVO.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/dtos/ResultadoGeracaoMapaVO.java): Retorna o mapa gerado, o ponto inicial de spawn e centros de salas.
   - [GerarMapaProceduralUseCase.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/usecases/GerarMapaProceduralUseCase.java): Posicionamento determinístico de salas retangulares não sobrepostas e escavação de túneis ortogonais interligados.

---

## 🔬 3. Testes Executados & Resultados (100% de Sucesso)

### Camada 1: Testes Unitários da Fase 4
- [GridEPathfindingTest.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/test/java/com/omini/engine/unit/GridEPathfindingTest.java): Aprovado (cálculos de distância Manhattan/Euclidiana/Chebyshev, linha de visão de Bresenham desimpedida e bloqueada por paredes, bloqueio de visão por fumaça, pathfinding A* contornando obstáculos intransponíveis e detecção de destino inalcançável).
- [AlquimiaElementalTest.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/test/java/com/omini/engine/unit/AlquimiaElementalTest.java): Aprovado (fogo + água gerando fumaça densa com tag `bloqueia_visao`, fogo + gás gerando detonação com 60 de poder e eletrificação de poças de água com 25 de dano/turno).
- [MotorTurnosAPTest.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/test/java/com/omini/engine/unit/MotorTurnosAPTest.java): Aprovado (custos base de AP, penalidades de sobrecarga pesada e incapacitante, aumento de custo de locomoção com perna fraturada sem tala para 200 AP e redução para 140 AP com tala, e resolução de iniciativa no motor de turnos com avanço do relógio de ticks).
- [GeradorMapaProceduralTest.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/test/java/com/omini/engine/unit/GeradorMapaProceduralTest.java): Aprovado (geração 100% determinística por seed, respeito estrito aos limites da matriz 2D e conectividade navegável comprovada via A* entre salas).

### Camada 2: Teste de Integração da Fase 4
- [MapaTurnosAlquimiaIntegrationTest.java](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/test/java/com/omini/engine/integration/MapaTurnosAlquimiaIntegrationTest.java): Aprovado.
  - Carregamento real dos catálogos via `CascadeModLoader`.
  - Geração de masmorra procedural determinística com 5 salas e corredores escavados.
  - Criação de sobrevivente com componentes de posição, stats, inventário e anatomia.
  - Planejamento de trajeto entre salas via A* e movimentação consumindo AP.
  - Detecção de fogo e água no trajeto disparando reação ambiental química que apaga as chamas e ergue nuvem de vapor/fumaça.
  - Validação em tempo real de que a linha de visão do sobrevivente foi bloqueada pela fumaça gerada proceduralmente.
  - Trauma anatômico com fratura na perna e validação de que o custo de locomoção dobrou imediatamente para 200 AP.

---

## 📈 4. Estado das Fases do Projeto
- **Fase 1 (Núcleo do Motor & Física de Materiais):** 100% Concluída.
- **Fase 2 (Entidades ECS, Grade Tetris & Sobrecarga):** 100% Concluída.
- **Fase 3 (Fisiologia Anatômica, Fraturas, Dor & Foco):** 100% Concluída.
- **Fase 4 (Grid Espacial, Alquimia Elemental & Turnos AP):** 100% Concluída.
- **Total de Testes Automatizados no Pipeline:** 14 suítes de teste (10 unitárias e 4 integradas) rodando nativamente sem frameworks externos e com 100% de sucesso.
