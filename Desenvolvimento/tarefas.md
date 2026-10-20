# Tarefas Ativas: Fase 3 (Fisiologia do Personagem, Anatomia, Dor & Foco)

Este documento registra o checklist rigoroso de implementação da **Fase 3**, seguindo a ordem de dependência modular e o protocolo de testes em 3 camadas.

---

## 📌 Subfase 3.1: Modelos e Repositório de Anatomia
- [x] Implementar modelos de dados anatômicos:
  - [x] `BodyPartDefinitionModel` (id, nome, hit_weight, vital, base_hp_ratio, armor_slot).
  - [x] `AnatomyDefinitionModel` (id, nome, mapa de partes do corpo).
- [x] Implementar contrato `IAnatomyRepository` e implementação concreta `JsonAnatomyRepository`.
- [x] Atualizar `CascadeModLoader` para carregar `anatomy.json` do jogo base e de mods.

---

## 📌 Subfase 3.2: Componentes ECS de Fisiologia e Sobrevivência
- [x] Implementar `BodyPartStateVO` (id, hpAtual, hpMaximo, fraturado, sangramentoPorTurno, tratado).
- [x] Implementar `AnatomyComponent` (mapa de partes vivas, verificação de fraturas, membros vitais e status vital geral).
- [x] Implementar `PainFocusComponent` (dor acumulada, foco atual, analgésicos ativos e taxa de absorção).
- [x] Implementar `SurvivalComponent` (fome, sede, estamina e sono discretizados de 0 a 100).
- [x] Implementar `StatsComponent` (Força, Destreza, Constituição, Percepção, Inteligência).

---

## 📌 Subfase 3.3: Casos de Uso Anatômicos & Combate Localizado
- [x] Implementar `SortearMembroAtingidoUseCase`:
  - [x] Leitura dos pesos de superfície (`hit_weight`).
  - [x] Aplicação de modificadores contextuais (postura, cobertura que anula membros inferiores).
  - [x] Mira cirúrgica assistida por Foco (redirecionamento de probabilidade).
  - [x] Sorteio estocástico com `IRandomProvider`.
- [x] Implementar `AplicarDanoLocalizadoUseCase`:
  - [x] Redução de HP do membro atingido.
  - [x] Aplicação de fraturas ao zerar o membro.
  - [x] Letalidade de membros vitais (cabeça/tronco).
  - [x] Disparo de sangramento e dor proporcional ao dano.
- [x] Implementar `TratarFerimentoUseCase`:
  - [x] Aplicação de curativo para estancar sangramento.
  - [x] Imobilização de fraturas com tala.
  - [x] Administração de analgésicos.

---

## 📌 Subfase 3.4: Casos de Uso de Dor, Foco e Metabolismo
- [x] Implementar `CalcularPenalidadesDorUseCase`:
  - [x] Atenuação de dor por analgésicos ativos.
  - [x] Cálculo do Teto de Foco: $Teto = \max(0, 100 - DorEfetiva)$.
  - [x] Penalidades escalonadas de concentração e ações.
- [x] Implementar `ProcessarCicloTurnoFisiologicoUseCase`:
  - [x] Processamento de sangramento contínuo em membros abertos.
  - [x] Decaimento e absorção de remédios/analgésicos.
  - [x] Consumo metabólico de estamina, sede e fome por turno.

---

## 📌 Subfase 3.5: Testes Automatizados (Camadas 1 e 2) & Documentação
- [x] Implementar testes unitários (Camada 1):
  - [x] Sorteio anatômico proporcional à área de superfície.
  - [x] Modificador de cobertura anulando membros inferiores.
  - [x] Mira cirúrgica com Foco concentrando probabilidade.
  - [x] Aplicação de dano localizado, fratura e letalidade em membro vital.
  - [x] Equação de dor, analgésicos e teto dinâmico de foco.
  - [x] Estancamento de sangramento com curativos e tala em fraturas.
- [x] Implementar teste de integração (Camada 2):
  - [x] Carregamento de `anatomy.json` e materiais reais via `CascadeModLoader`.
  - [x] Criação de humanoide completo com anatomia, dor, foco e atributos.
  - [x] Simulação de combate com dano localizado, sangramento e aplicação de primeiros socorros.
  - [x] Avanço turno a turno do metabolismo e dor.
  - [x] Morte clínica de membro vital.
- [x] Registrar histórico de ciclo em `Desenvolvimento/docs/historico-ciclos/ciclo-03-fase-3-fisiologia-e-anatomia.md`.
- [x] Commit na branch `fase-3`.
