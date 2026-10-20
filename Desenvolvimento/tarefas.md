# Tarefas Ativas: Fase 2 (Entidades, Inventário em Grade & Massa Procedural)

Este documento registra o checklist rigoroso de implementação da **Fase 2**, seguindo a ordem de dependência modular e o protocolo de testes em 3 camadas.

---

## 📌 Subfase 2.1: Arquitetura ECS Enxuta (Entity-Component-System)
- [x] Implementar classe base `Entity` com identificador único (`UUID`/`String`), tags e mapa tipado de componentes.
- [x] Implementar interface marcadora `Component`.
- [x] Implementar componentes essenciais de domínio:
  - [x] `ItemComponent` (id do item, nome, largura, altura, peso, flags de rotação).
  - [x] `InventoryGridComponent` (matriz de ocupação $W \times H$, lista de itens posicionados e ocupação espacial).
  - [x] `PositionedItemVO` (referência ao item, posição $X, Y$, flag de rotação 90°).
  - [x] `MaterialComponent` (id do material base, dureza, densidade).
  - [x] `EquipableComponent` (slot anatômico compatível: mão primária, mão secundária, torso, etc.).
  - [x] `PositionComponent` (coordenadas espaciais $X, Y$ no mapa).

---

## 📌 Subfase 2.2: Sistema de Inventário em Grade Bidimensional (Estilo Tetris)
- [x] Implementar `InserirItemGradeUseCase`:
  - [x] Validação geométrica nos limites da matriz ($X + L \le Largura$, $Y + A \le Altura$).
  - [x] Suporte a rotação de 90° (inversão de largura e altura).
  - [x] Detecção de colisão matricial (rejeição se qualquer slot estiver ocupado).
  - [x] Ocupação atômica dos slots e registro de `PositionedItemVO`.
- [x] Implementar `RemoverItemGradeUseCase`:
  - [x] Localização do item por ID único da instância.
  - [x] Liberação atômica dos slots matriciais ocupados pela geometria do item.
- [x] Implementar consultas de ocupação e inspeção de slots no `InventoryGridComponent`.

---

## 📌 Subfase 2.3: Cálculo de Massa Procedural & Sobrecarga
- [x] Implementar enumeração `NivelSobrecargaEnum` (`LEVE`, `CONFORTAVEL`, `PESADA`, `CRITICA`, `INCAPACITADO`).
- [x] Implementar DTO/VO `EstadoSobrecargaVO` contendo:
  - [x] `estaSobrecarregado` (booleano).
  - [x] `taxaCarga` ($\frac{PesoTotal}{CapacidadeBase}$).
  - [x] `penalidadeVelocidadeAP` (custo adicional de pontos de ação por turno).
  - [x] `modificadorDificuldadeEsquiva` (aumento de dificuldade nas rolagens defensivas).
  - [x] `nivelSobrecarga` (`NivelSobrecargaEnum`).
- [x] Implementar `CalcularSobrecargaUseCase`:
  - [x] Capacidade Confortável: $Forca \times 5.0$ kg.
  - [x] Capacidade Máxima: $Forca \times 10.0$ kg.
  - [x] Progressão gradual de penalidades conforme a carga ultrapassa as faixas.

---

## 📌 Subfase 2.4: Schemas JSON de Itens & Repositório com CascadeModLoader
- [x] Criar pasta `Desenvolvimento/base_game/data/items/`.
- [x] Criar arquivos JSON de catálogo base:
  - [x] `items/weapons.json` (espada curta de ferro, picareta de ferro, machado de madeira, adaga de aço, etc.).
  - [x] `items/armor.json` (peitoral de ferro, túnica de tecido, elmo de ferro, botas reforçadas, etc.).
  - [x] `items/tools.json` (tocha, cantil, kit de gazua, corda).
  - [x] `items/containers.json` (bolsa de cintura, mochila pequena, mochila tática militar).
- [x] Implementar modelo `ItemModel` integrando com materiais da Fase 1 para massa procedural.
- [x] Implementar contrato `IItemRepository` e implementação `JsonItemRepository` consumindo `CascadeModLoader`.
- [x] Implementar fábrica `ItemEntityFactory` para criação de entidades de itens e contêineres com suas grades internas.

---

## 📌 Subfase 2.5: Testes Automatizados (Camadas 1 e 2) & Documentação
- [x] Implementar testes unitários (Camada 1):
  - [x] Inserção com sucesso de itens $1 \times 1$, $1 \times 3$ e rotacionados $3 \times 1$.
  - [x] Rejeição de colisão e sobreposição de itens na grade.
  - [x] Rejeição de transbordo (out of bounds) fora dos limites da grade.
  - [x] Remoção com liberação limpa de slots.
  - [x] Cálculo de sobrecarga e penalidades para diferentes valores de força e peso.
  - [x] Gerenciamento tipado de entidades e componentes ECS.
- [x] Implementar teste de integração (Camada 2):
  - [x] Carregamento de itens a partir dos JSONs reais via `CascadeModLoader`.
  - [x] Criação de entidade mochila com `InventoryGridComponent`.
  - [x] Inserção e arranjo de armas e armaduras com cálculo procedural de peso (usando materiais de `materials.json`).
  - [x] Avaliação do estado de sobrecarga da criatura portadora da mochila.
  - [x] Transição de estado de sobrecarga para `INCAPACITADO` com remoção atômica posterior.
- [x] Registrar histórico de ciclo em `Desenvolvimento/docs/historico-ciclos/ciclo-02-fase-2-inventario-e-ecs.md`.
- [x] Commit na branch `fase-2`.
