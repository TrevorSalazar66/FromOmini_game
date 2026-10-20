# Tarefas Ativas: Fase 1 (Núcleo do Motor de Dados, Física & Testes)

Este documento registra o checklist rigoroso de implementação da **Fase 1**, seguindo a ordem de dependência modular e o protocolo de testes em 3 camadas.

---

## 📌 Subfase 1.1: Configuração do Projeto Gradle & Estrutura de Pastas
- [x] Criar estrutura física de diretórios em `Desenvolvimento/` conforme `estrutura-pastas.md`.
- [x] Configurar arquivos base de build: `build.gradle.kts` e `settings.gradle.kts` com suporte a Java 17 LTS e JUnit 5 para testes.
- [x] Configurar parser JSON desacoplado (`SimpleJsonParser`) e adaptador de serialização nativo sem dependências externas.
- [x] Criar pastas do pacote base do motor: `com.omini.engine.core`, `domain`, `controllers`, `data`, `ui`.

---

## 📌 Subfase 1.2: Infraestrutura de Armazenamento & Scoped Storage
- [x] Implementar carregador em cascata de mods e dados (`CascadeModLoader`).
- [x] Mapear a arquitetura da pasta pública `Documentos/[NomeDoJogo]/` no Android.
- [x] Criar arquivos base do jogo em `Desenvolvimento/base_game/`:
  - [x] `game_info.json`
  - [x] `world_rules.json` (com Feature Flags ativas/desativadas)

---

## 📌 Subfase 1.3: Motor de Propriedades, Danos & Materiais
- [x] Criar schemas JSON em `base_game/data/`:
  - [x] `data/materials.json` (Madeira, Pedra, Ferro, Ouro, Chumbo, Tecido, Carne, etc.)
  - [x] `data/damage_types.json` (Corte, Contusão, Perfuração, Fogo, Eletricidade, Mineração, Sagrado)
  - [x] `data/anatomy.json` (14 membros humanos com área de superfície e vitalidade)
  - [x] `data/environmental_reactions.json` (Reações de alquimia ambiental)
- [x] Implementar modelos de domínio imutáveis: `MaterialModel`, `DamageTypeModel`.
- [x] Implementar repositórios de leitura: `IMaterialRepository`, `IDamageTypeRepository` e suas implementações concretas JSON (`JsonMaterialRepository`, `JsonDamageTypeRepository`).
- [x] Validação Fail-Fast: testes unitários (Camada 1) para integridade e leitura correta dos multiplicadores.

---

## 📌 Subfase 1.4: Motor Matemático de Testes Dinâmicos & Curva Contínua
- [x] Implementar `CalcularRangeTesteUseCase`:
  - [x] Entrada: Atributo base ($\ge 1$).
  - [x] Saída: Range $[Atributo, Atributo \times 10]$.
- [x] Implementar `CalcularDistribuicaoPesosUseCase`:
  - [x] Nível 0: Rampa de Decaimento linear (peso máximo no início até peso 1 no final).
  - [x] Níveis 1 a 70: Interpolação contínua ("Monte de Areia") sem reset na troca de faixas (Aprendiz a Grão-Mestre).
- [x] Implementar `ExecutarSorteioPonderadoUseCase`:
  - [x] Sorteio acumulativo de pool sobre o array de pesos com interface `IRandomProvider`.
- [x] Implementar testes unitários (Camada 1) com assertivas estatísticas (verificando se Grão-Mestre 1 é estritamente superior a Mestre 10 e se Nível 0 tende à falha).

---

## 📌 Subfase 1.5: Resolução por Graus de Sucesso & Dificuldade Dinâmica
- [x] Implementar `CalcularAlvoDificuldadeUseCase`:
  - [x] Entrada: Dureza do material, nível do alvo, modificador da ferramenta do atacante.
  - [x] Saída: Classe de Dificuldade (Alvo) numérica.
- [x] Implementar `ResolverGrauSucessoUseCase`:
  - [x] Enumeração `GrauSucessoEnum` (`FALHA_CRITICA`, `FALHA`, `SUCESSO_PARCIAL`, `SUCESSO_TOTAL`, `SUCESSO_EXTREMO`).
  - [x] Comparação percentual delta ($\Delta = \frac{Sorteado - Alvo}{Alvo}$).
- [x] Implementar `CalcularMassaProceduralUseCase`:
  - [x] Cálculo procedural de massa: $(\text{Largura} \times \text{Altura}) \times \text{Densidade}$.
- [x] Testes de integração (Camada 2) conectando Materiais $\rightarrow$ Range $\rightarrow$ Pesos $\rightarrow$ Sorteio $\rightarrow$ Grau de Sucesso (`NucleoMotorIntegrationTest`).
- [x] Registrar Log do Ciclo da Fase 1 em `docs/historico-ciclos/ciclo-01-fase-1-nucleo-e-testes.md`.
