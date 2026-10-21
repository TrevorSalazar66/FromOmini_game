# Log de Histórico de Ciclo: Fase 5 - Interface Mobile Touch-First, HUD Minimalista, Persistência Atômica & Game Loop

**Data:** 20 de Outubro de 2026  
**Status do Ciclo:** Concluído com 100% de Aprovação  
**Branch Ativa:** `fase-5`

---

## 📌 1. Contexto de Encadeamento
- **Fase / Subfase Anterior:** Fase 4 (Grid Espacial 2D, FOV por Bresenham, Pathfinding A*, Alquimia Elemental e Motor de Turnos baseado em AP).
- **Fase / Subfase Atual:** Fase 5 (Subfases 5.1 a 5.6 - Viewport e Câmera Móvel, MapView Desacoplado, Menus Radiais/Contextuais Touch-First no estilo Pixel Dungeon, Gestos de Inventário com Drag & Drop e Rotação 90°, Widget de Silhueta Anatômica com Cores Clínicas e Primeiros Socorros, HUD Minimalista com Consumo de AP e Sobrecarga, Persistência Atômica JSON à Prova de Falhas e GameLoopController Central).
- **Próxima Fase / Subfase Planejada:** Fase 6 (IA Tática Avançada de Criaturas, Estados Mentais, Alertas Sensoriais e Áudio Procedural).

---

## 🛠️ 2. O Que Foi Implementado

### 1. Camada de Apresentação & Câmera Móvel (`com.omini.engine.ui.views` / `dtos`):
- [ViewportCamera.java](file:///home/ultratelecom/Documentos/Prjts/omini/Desenvolvimento/src/main/java/com/omini/engine/ui/views/ViewportCamera.java):
  - Projeção ortográfica bidimensional para telas mobile verticais e horizontais.
  - Conversão bidirecional perfeita entre coordenadas de grade do mundo e pixels de tela (`mundoParaTela` e `telaParaMundo`).
  - Suporte a zoom contínuo e centralização suave de foco sobre o jogador ou entidades interativas.
  - Verificação de visibilidade na viewport (`isTileVisivelNaTela`).
- [RenderCellVO.java](file:///home/ultratelecom/Documentos/Prjts/omini/Desenvolvimento/src/main/java/com/omini/engine/domain/dtos/RenderCellVO.java):
  - Value Object imutável contendo coordenadas mundiais, glifo representativo, código hexadecimal de cor, indicador de FOV visível, indicador de tile explorado na neblina de guerra e camada lógica de renderização (`TERRAIN`, `SURFACE`, `ITEM`, `ACTOR`).
- [MapView.java](file:///home/ultratelecom/Documentos/Prjts/omini/Desenvolvimento/src/main/java/com/omini/engine/ui/views/MapView.java):
  - Renderizador desacoplado de mapas 2D que extrai as células visíveis na tela e aplica sombreamento dinâmico.
  - Quatro camadas renderizadas em ordem hierárquica estrita:
    1. **Terreno:** Paredes (`#`), Chão de Pedra (`.`), Água Rasa (`~`), Portas Fechadas/Abertas (`+`/`/`), Grama (`"`).
    2. **Neblina de Guerra:** Tiles explorados exibidos em tom escurecido atenuado; áreas sob linha de visão ativa (FOV) destacadas com cores vibrantes.
    3. **Superfícies Reativas:** Efeitos químicos de superfícies ativas como chamas (`^`, `#FF5722`), fumaça densa (`%`, `#9E9E9E`) e poças eletrificadas (`z`, `#FFEB3B`).
    4. **Itens e Atores:** Itens caídos (`?`, `#FFC107`), monstros inimigos (`M`, `#F44336`) e o jogador (`@`, `#4CAF50`).

### 2. Interações Touch-First & Menus Radiais/Contextuais (`com.omini.engine.controllers` / `ui.views` / `dtos`):
- [AcaoContextualVO.java](file:///home/ultratelecom/Documentos/Prjts/omini/Desenvolvimento/src/main/java/com/omini/engine/domain/dtos/AcaoContextualVO.java):
  - Modelo de ação contextual com identificador, rótulo textual formatado, ícone representativo, custo estimado de AP e estado de habilitação.
- [ContextMenuView.java](file:///home/ultratelecom/Documentos/Prjts/omini/Desenvolvimento/src/main/java/com/omini/engine/ui/views/ContextMenuView.java):
  - Menu radial contextual touch-first (estilo *Shattered Pixel Dungeon*).
  - Inspeciona os componentes ECS da entidade tocada em tempo de execução:
    - Entidades vivas: ações de `[Atacar]`, `[Examinar]`.
    - Jogador: ações de `[Inventário]`, `[Silhueta Anatômica]`, `[Descansar/Passar Turno]`.
    - Contêineres e baús: `[Abrir / Saquear]`.
    - Terrenos e portas: `[Abrir Porta]`, `[Fechar Porta]`.
- [InteractionController.java](file:///home/ultratelecom/Documentos/Prjts/omini/Desenvolvimento/src/main/java/com/omini/engine/controllers/InteractionController.java):
  - Roteador principal de toques na tela mobile.
  - Ao tocar em um tile vazio: calcula automaticamente o trajeto mais eficiente via `NavegarPathfindingAStarUseCase`, move a entidade do jogador, deduz os Pontos de Ação (AP) correspondentes e recentraliza a câmera suavemente.
  - Ao tocar no próprio personagem ou em entidades no mesmo tile ou adjacentes: abre o menu contextual radial com as opções pertinentes.
- [InventoryController.java](file:///home/ultratelecom/Documentos/Prjts/omini/Desenvolvimento/src/main/java/com/omini/engine/controllers/InventoryController.java):
  - Gerenciador de gestos táteis sobre a grade de inventário estilo Tetris.
  - Suporta operação de arrastar e soltar (*drag-and-drop*) com detecção de colisões espaciais, verificação de sobreposição e fallback seguro para a posição original caso o movimento seja inválido.
  - Suporte a rotação tátil em 90 graus de itens que possuem geometria rotacionável.

### 3. Silhueta Anatômica & HUD Minimalista (`com.omini.engine.ui.widgets` / `ui.views`):
- [AnatomyWidget.java](file:///home/ultratelecom/Documentos/Prjts/omini/Desenvolvimento/src/main/java/com/omini/engine/ui/widgets/AnatomyWidget.java):
  - Widget tátil visualizando o modelo de 14 partes corporais (Cabeça, Olhos, Mandíbula, Tronco, Braços, Antebraços, Mãos, Coxas, Pernas, Pés).
  - Gradiente clínico estrito de cores baseado no percentual de integridade física:
    - $\ge 80\%$: Verde (`#4CAF50` - Íntegro).
    - $50\% - 79\%$: Amarelo (`#FFEB3B` - Lesão Leve).
    - $25\% - 49\%$: Laranja (`#FF9800` - Lesão Moderada).
    - $> 0\%$: Vermelho (`#F44336` - Lesão Crítica).
    - $0\%$ ou Fraturado: Cinza Escuro (`#424242` - Fratura / Destruição Óssea).
  - Indicadores táticos para membros com curativo aplicado (`[ENFAIXADO]`) e membros com tala ortopédica (`[IMOBILIZADO]`).
  - Menu instantâneo de primeiros socorros ao tocar diretamente no membro lesionado.
- [HUDView.java](file:///home/ultratelecom/Documentos/Prjts/omini/Desenvolvimento/src/main/java/com/omini/engine/ui/views/HUDView.java):
  - HUD minimalista projetado para telas de smartphones, com leitura rápida sem poluição visual.
  - Medidor de AP disponível em tempo real e indicador do custo estimado da ação planejada.
  - Indicador de sobrecarga corporal e encumbrance com codificação de alerta visual (`Verde`, `Amarelo`, `Vermelho`).
  - Métricas de sobrevivência (Estamina, Fome, Sede) e Foco Mental com teto reduzido por dor acumulada.
  - Histórico de combate e eventos táticos recentes em feed compacto.

### 4. Persistência Atômica de Partida (`com.omini.engine.domain.dtos` / `data.repositories`):
- [SaveGameStateDTO.java](file:///home/ultratelecom/Documentos/Prjts/omini/Desenvolvimento/src/main/java/com/omini/engine/domain/dtos/SaveGameStateDTO.java):
  - DTO completo consolidando todo o estado do jogo (versão do save, semente procedural da masmorra, relógio global de ticks, posição e dados do jogador, lista de itens no inventário, tiles explorados na neblina de guerra e lista de monstros ativos).
- [JsonSaveGameRepository.java](file:///home/ultratelecom/Documentos/Prjts/omini/Desenvolvimento/src/main/java/com/omini/engine/data/repositories/JsonSaveGameRepository.java):
  - Serializador e deserializador JSON determinístico e livre de bibliotecas externas pesadas.
  - Protocolo de gravação atômica: salva primeiro o estado em um arquivo temporário (`.tmp`) e realiza a substituição atômica (`Files.move(..., ATOMIC_MOVE, REPLACE_EXISTING)`). Caso ocorra falha de energia, encerramento abrupto do app no smartphone ou erro de I/O, o arquivo original de save nunca é corrompido.

### 5. Loop de Jogo Central & Orquestrador (`com.omini.engine.controllers`):
- [GameLoopController.java](file:///home/ultratelecom/Documentos/Prjts/omini/Desenvolvimento/src/main/java/com/omini/engine/controllers/GameLoopController.java):
  - Orquestra os turnos discretos do ecossistema roguelike.
  - Avalia a prontidão de ação de todas as entidades via `MotorTurnosAPUseCase`.
  - Executa ações dos monstros (aproximação via A* ou ataque tático adjacente).
  - Dispara automaticamente o `ProcessarCicloTurnoFisiologicoUseCase` ao final de cada rodada, aplicando sangramento, decaimento farmacológico e impacto de dor.
  - Avalia condições de término de partida e aciona o estado de *Game Over* caso algum órgão vital (`torso`, `head`, etc.) seja destruído.

---

## 🔬 3. Testes Executados & Validação Rigorosa (100% de Sucesso)

### Camada 1: Testes Unitários da Fase 5
1. [ViewportCameraTest.java](file:///home/ultratelecom/Documentos/Prjts/omini/Desenvolvimento/src/test/java/com/omini/engine/unit/ViewportCameraTest.java):
   - Projeção de coordenadas mundo $\leftrightarrow$ tela validada com precisão de pixels.
   - Centralização de câmera em entidades móveis.
   - Aplicação e cálculo correto do fator de zoom.
2. [ContextMenuViewTest.java](file:///home/ultratelecom/Documentos/Prjts/omini/Desenvolvimento/src/test/java/com/omini/engine/unit/ContextMenuViewTest.java):
   - Reconhecimento dinâmico de componentes ECS (`AnatomyComponent`, `InventoryGridComponent`, `DoorComponent`).
   - Abertura de ações contextuais para inimigos (`Atacar`, `Examinar`) e para o próprio sobrevivente (`Inventário`, `Tratar Ferimentos`).
3. [AnatomyWidgetTest.java](file:///home/ultratelecom/Documentos/Prjts/omini/Desenvolvimento/src/test/java/com/omini/engine/unit/AnatomyWidgetTest.java):
   - Validação da paleta clínica de cores em função do percentual de HP restante em cada membro.
   - Detecção de membros fraturados assumindo cor cinza de alerta.
   - Marcação de curativo e tala em tempo real.
4. [JsonSaveGameRepositoryTest.java](file:///home/ultratelecom/Documentos/Prjts/omini/Desenvolvimento/src/test/java/com/omini/engine/unit/JsonSaveGameRepositoryTest.java):
   - Serialização e restauração completa de um estado de jogo.
   - Verificação da preservação da integridade do arquivo através de gravação atômica via `.tmp`.
   - Recuperação fiel das coordenadas do jogador, semente da masmorra, itens na mochila e relógio de turnos.
5. [GameLoopControllerTest.java](file:///home/ultratelecom/Documentos/Prjts/omini/Desenvolvimento/src/test/java/com/omini/engine/unit/GameLoopControllerTest.java):
   - Condução da fila de iniciativa baseada em energia e AP.
   - Resolução de ataque de monstro adjacente contra o tronco do jogador, gerando dano de impacto e hemorragia ativa, processada autonomamente pelo ciclo fisiológico subsequente.
   - Detecção precisa de condição de *Game Over* ao destruir membro vital.

### Camada 2: Teste de Integração da Fase 5
- [InterfaceMobileEGameStateIntegrationTest.java](file:///home/ultratelecom/Documentos/Prjts/omini/Desenvolvimento/src/test/java/com/omini/engine/integration/InterfaceMobileEGameStateIntegrationTest.java):
  - **Sessão Holística de Jogo Mobile:**
    1. Inicialização do motor e catálogos via `CascadeModLoader`.
    2. Geração determinística de masmorra procedural com corredores e salas.
    3. Posicionamento do sobrevivente, inicialização da `ViewportCamera` e do `MapView`.
    4. Cálculo e renderização da neblina de guerra e linha de visão (FOV).
    5. Simulação de toques na tela mobile, disparando menu contextual e movimentação via A*.
    6. Gestos no inventário tático (movimentação e rotação em 90 graus de frasco de remédio e atadura).
    7. Aplicação de dano e tratamento de emergência através do menu de primeiros socorros do `AnatomyWidget`.
    8. Extração e auditoria dos dados do HUD minimalista (AP, peso e sobrecarga).
    9. Salvamento atômico em disco, persistindo em formato JSON.
    10. Restauração integral a partir do arquivo salvo e verificação de igualdade absoluta dos estados.

---

## 📈 4. Estado Consolidado das Fases do Projeto
- **Fase 1 (Núcleo do Motor, Física de Materiais & Modularidade):** 100% Concluída.
- **Fase 2 (Entidades ECS, Grade Tetris & Sobrecarga Corporal):** 100% Concluída.
- **Fase 3 (Fisiologia Anatômica, Fraturas, Dor & Foco Mental):** 100% Concluída.
- **Fase 4 (Grid Espacial 2D, Alquimia Elemental & Turnos AP):** 100% Concluída.
- **Fase 5 (Interface Mobile Touch-First, HUD, Persistência & Game Loop):** 100% Concluída.
- **Bateria Global de Testes:** 24 suítes de teste (19 unitárias e 5 integradas) executadas nativamente via `java -ea` sem dependências externas e com 100% de aprovação.
