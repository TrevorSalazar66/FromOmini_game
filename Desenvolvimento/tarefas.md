# Tarefas Ativas: Fase 4 (Grid Espacial, Alquimia Elemental & Turnos)

Este documento registra o checklist rigoroso de implementação da **Fase 4**, seguindo a ordem de dependência modular e o protocolo de testes em 3 camadas.

---

## 📌 Subfase 4.1: Modelo de Mapa de Tiles & Linha de Visão (Field of View)
- [x] Implementar enum/modelo `TileTypeEnum` (CHAO_PEDRA, PAREDE_GRANITO, PORTA, AGUA_RASA, GRAMA) com custo de movimento, passabilidade e opacidade de visão.
- [x] Implementar classe de domínio `TileMap` ($W \times H$) com matriz de tiles e suporte a busca espacial.
- [x] Implementar `CalcularLinhaDeVisaoUseCase` (algoritmo determinístico de linha de visão com bloqueio por paredes e fumaça).

---

## 📌 Subfase 4.2: Pathfinding A* (A-Star) com Custos Variáveis
- [x] Implementar `CoordenadaVO` (x, y) e utilitários de distância Manhattan, Euclidiana e Chebyshev.
- [x] Implementar `NavegarPathfindingAStarUseCase`:
  - [x] Fila de prioridade com custo acumulado $g(n)$ e heurística $h(n)$.
  - [x] Respeito estrito a obstáculos intransponíveis e custos variáveis de terreno.
  - [x] Prevenção de corte de quinas em paredes adjacentes.
  - [x] Retorno da lista ordenada de passos até o destino ou lista vazia se inalcançável.

---

## 📌 Subfase 4.3: Superfícies Reativas & Alquimia Elemental
- [x] Implementar modelo `EnvironmentalReactionModel` e contrato `IEnvironmentalReactionRepository`.
- [x] Implementar `JsonEnvironmentalReactionRepository` carregando `environmental_reactions.json`.
- [x] Atualizar `CascadeModLoader` para carregar reações ambientais.
- [x] Implementar componente ECS `SurfaceFieldComponent` (tipo/tags do campo, intensidade, duração em turnos).
- [x] Implementar `ProcessarReacaoAmbientalUseCase`:
  - [x] Detecção de pares antagônicos (`surface_fire` + `surface_water` gerando `field_smoke`).
  - [x] Detonação em área (`surface_fire` + `flammable_gas`).
  - [x] Propagação de choque (`surface_electric` + `surface_water`).

---

## 📌 Subfase 4.4: Motor de Turnos e Pontos de Ação (AP) com Penalidades
- [x] Implementar `TipoAcaoEnum` (MOVER, ATACAR, USAR_ITEM, RECARREGAR, ESPERAR) com custos base de AP.
- [x] Implementar `CalcularCustoAcaoAPUseCase`:
  - [x] Custo base da ação.
  - [x] Modificador de sobrecarga corporal (obtido de `EstadoSobrecargaVO`).
  - [x] Modificador de fraturas anatômicas (perna fraturada dobra custo de locomoção).
  - [x] Modificador de atraso neural por dor aguda (> 20 de dor).
- [x] Implementar `MotorTurnosAPUseCase`:
  - [x] Acúmulo de energia por velocidade base ($DES \times 2 + 100$).
  - [x] Consumo de AP por ação realizada e avanço do relógio global de ticks.

---

## 📌 Subfase 4.5: Gerador Procedural de Mapas Baseado em Dados
- [x] Implementar `ParametrosGeracaoMapaDTO` (largura, altura, maxSalas, tamanhoMinSala, tamanhoMaxSala, seed).
- [x] Implementar `ResultadoGeracaoMapaVO` contendo mapa, spawn e lista de centros de salas.
- [x] Implementar `GerarMapaProceduralUseCase`:
  - [x] Posicionamento determinístico e não sobreposto de salas retangulares.
  - [x] Abertura de corredores de ligação ortogonais conectando salas consecutivas.
  - [x] Paredes perimetrais sólidas e piso interno transitável.

---

## 📌 Subfase 4.6: Testes Automatizados (Camadas 1 e 2) & Documentação
- [x] Implementar testes unitários (Camada 1):
  - [x] `GridEPathfindingTest`: A* encontrando a rota mais curta, contornando paredes, bloqueio de visão por paredes e fumaça.
  - [x] `AlquimiaElementalTest`: Alquimia de fogo e água gerando campo de fumaça, detonação com gás, eletrificação de poça.
  - [x] `MotorTurnosAPTest`: Cálculo de AP considerando custos de perna fraturada (+100%), imobilização com tala (+40%) e peso excessivo.
  - [x] `GeradorMapaProceduralTest`: Geração procedural de mapa com determinismo de seed, conectividade navegável e ausência de transbordo.
- [x] Implementar teste de integração (Camada 2):
  - [x] `MapaTurnosAlquimiaIntegrationTest`: Carregamento de reações reais via `CascadeModLoader`, masmorra procedural, movimentação de sobrevivente, alquimia elemental gerando fumaça com bloqueio em tempo real de FOV e penalidade de fratura anatômica.
- [x] Registrar histórico de ciclo em `Desenvolvimento/docs/historico-ciclos/ciclo-04-fase-4-grid-alquimia-e-turnos.md`.
- [x] Commit na branch `fase-4`.
- [x] Merge na `main` e criação do `README.md` abrangente na raiz.
