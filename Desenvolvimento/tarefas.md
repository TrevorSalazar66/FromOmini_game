# Tarefas Ativas: Fase 5 (Interface Mobile Touch-First, HUD Minimalista, Persistência & Game Loop)

Este documento registra o checklist de implementação da **Fase 5**, seguindo a ordem de dependência modular e o protocolo de testes em 3 camadas.

---

## 📌 Subfase 5.1: Camada de Apresentação, Viewport e Renderizador 2D
- [x] Implementar `ViewportCamera` com cálculo de projeção ortográfica, zoom, centralização em entidades e conversão mundo $\leftrightarrow$ tela.
- [x] Implementar `MapView` desacoplado gerenciando renderização em camadas:
  - [x] Camada de terreno (tiles transitáveis, paredes, água).
  - [x] Camada de neblina de guerra (*FOV* e tiles explorados).
  - [x] Camada de superfícies reativas e alquimia (fogo, fumaça, poças).
  - [x] Camada de entidades e itens caídos no chão.

---

## 📌 Subfase 5.2: Menus Contextuais & Gestos Touch-First (Estilo SPD)
- [x] Implementar modelo `AcaoContextualVO` (id, rótulo, ícone, habilitado, tipoAcao).
- [x] Implementar `ContextMenuView` (Menu radial/ações de contexto avaliando componentes ECS da entidade tocada).
- [x] Implementar `InteractionController`:
  - [x] Toque em tile vazio: movimentação automática via `NavegarPathfindingAStarUseCase` consumindo AP.
  - [x] Toque em entidade interativa: abertura de menu radial com ações contextuais (`[Abrir]`, `[Examinar]`, `[Atacar]`, `[Primeiros Socorros]`).
- [x] Implementar `InventoryController`:
  - [x] Operação de arrastar e soltar (drag-and-drop).
  - [x] Gesto de rotação em 90 graus de itens compatíveis.

---

## 📌 Subfase 5.3: Silhueta Anatômica & HUD Minimalista
- [x] Implementar `AnatomyWidget`:
  - [x] Renderização da silhueta segmentada em 14 membros corporais.
  - [x] Código de cores dinâmico por nível de lesão: Verde (Íntegro) $\rightarrow$ Amarelo $\rightarrow$ Laranja $\rightarrow$ Vermelho $\rightarrow$ Cinza/Preto (Fraturado).
  - [x] Indicadores visuais de ferida enfaixada e membro imobilizado com tala.
  - [x] Painel rápido de primeiros socorros ao tocar em membro ferido.
- [x] Implementar `HUDView`:
  - [x] Medidor tático de AP disponível e custos projetados de ação.
  - [x] Indicador visual de sobrecarga corporal (Verde/Confortável, Amarelo/Pesada, Vermelho/Incapacitado).
  - [x] Barras de sobrevivência (Estamina, Fome, Sede) e Foco Mental reprimido por Dor.
  - [x] Log de eventos de combate e ações em texto enxuto.

---

## 📌 Subfase 5.4: Persistência Atômica de Partida & Configurações de Sandbox
- [x] Implementar `SaveGameStateDTO` estruturando o snapshot completo do mundo (jogador, inventários, mapa explorado, relógio de turnos e regras).
- [x] Implementar `JsonSaveGameRepository`:
  - [x] Serialização do estado de jogo em formato JSON limpo e auditável.
  - [x] Gravação atômica à prova de corrupção (salva em arquivo `.tmp` antes de sobrescrever o `.json` oficial).
  - [x] Deserialização e restauração integral do ecossistema ECS, inventário em grade e mapa.

---

## 📌 Subfase 5.5: Loop de Jogo Central (GameLoopController)
- [x] Implementar `GameLoopController`:
  - [x] Orquestração do loop de turnos e iniciativa.
  - [x] Consumo de AP por ação do jogador e avanço das entidades no mapa.
  - [x] Acionamento automático de `ProcessarCicloTurnoFisiologicoUseCase` ao fim de cada rodada de tempo.
  - [x] Avaliação de Game Over caso a condição de morte vital seja atingida.

---

## 📌 Subfase 5.6: Bateria de Testes em Camadas (1 e 2) & Documentação
- [x] Implementar testes unitários (Camada 1):
  - [x] `ViewportCameraTest`: Conversão de coordenadas e enquadramento.
  - [x] `ContextMenuViewTest`: Detecção de ações contextuais baseadas em componentes ECS.
  - [x] `AnatomyWidgetTest`: Mapeamento correto de cores e estados clínicos por membro.
  - [x] `JsonSaveGameRepositoryTest`: Salvamento atômico, integridade de dados e restauração.
  - [x] `GameLoopControllerTest`: Ciclo de turnos, consumo de AP e condição de derrota.
- [x] Implementar teste de integração (Camada 2):
  - [x] `InterfaceMobileEGameStateIntegrationTest`: Sessão completa integrando masmorra procedural, interação touch, combate, curativos na silhueta, salvamento e carregamento sem perda de dados.
- [x] Registrar histórico de ciclo em `Desenvolvimento/docs/historico-ciclos/ciclo-05-fase-5-interface-mobile-e-persistencia.md`.
