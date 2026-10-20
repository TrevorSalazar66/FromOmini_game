# Log de Histórico de Ciclo: Fase 1 - Núcleo do Motor & Testes

**Data:** 20 de Outubro de 2026  
**Status do Ciclo:** Concluído com 100% de Aprovação  
**Branch Ativa:** `fase-1`

---

## 📌 1. Contexto de Encadeamento
- **Fase / Subfase Anterior:** Fase 0 (Setup do Repositório, Tríade de Especificação e Governança de Pastas).
- **Fase / Subfase Atual:** Fase 1 (Subfases 1.1 a 1.5 - Núcleo do Motor de Dados, Física de Materiais & Testes Ponderados).
- **Próxima Fase / Subfase Planejada:** Fase 2 (Subfases 2.1 a 2.4 - Modelo ECS de Entidades, Inventário em Grade Bidimensional e Sistema de Sobrecarga).

---

## 🛠️ 2. O Que Foi Implementado

1. **Configuração de Projeto e Governança de Arquitetura:**
   - Criação da pasta oficial `Desenvolvimento/` contendo a Tríade de Especificação ([idealizacao.md](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/idealizacao.md), [planejamento.md](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/planejamento.md), [funcoes.md](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/funcoes.md)) e a Governança Viva ([estrutura-pastas.md](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/estrutura-pastas.md)).
   - Criação de `build.gradle.kts` e `settings.gradle.kts` para suportar build em qualquer ambiente/IDE com Java 17 LTS.

2. **Banco de Dados do Pacote Base (`base_game/`):**
   - [game_info.json](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/base_game/game_info.json): Metadados do jogo base.
   - [world_rules.json](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/base_game/world_rules.json): Feature flags e parâmetros matemáticos globais.
   - [materials.json](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/base_game/data/materials.json): Banco de materiais (madeiras, granito, ferro, chumbo, ouro, tecidos, penas, carne) com densidades, dureza e modificadores de dano.
   - [damage_types.json](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/base_game/data/damage_types.json): Tipos de dano com multiplicadores contra tags orgânicas, armadas e minerais.
   - [anatomy.json](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/base_game/data/anatomy.json): Distribuição anatômica humana de 14 partes corporais, pesos de superfície e vitalidade.
   - [environmental_reactions.json](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/base_game/data/environmental_reactions.json): Alquimia de superfícies (fogo + água gerando fumaça; fogo + gás detonando; eletricidade em poças).

3. **Camada de Domínio (Clean Architecture - Use Cases Puros):**
   - [CalcularRangeTesteUseCase](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/usecases/CalcularRangeTesteUseCase.java): Range dinâmico $[Atributo, Atributo \times 10]$.
   - [CalcularDistribuicaoPesosUseCase](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/usecases/CalcularDistribuicaoPesosUseCase.java): Rampa decrescente para Nível 0 e algoritmo do "Monte de Areia" contínuo para 70 níveis com herança monótona de peso.
   - [ExecutarSorteioPonderadoUseCase](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/usecases/ExecutarSorteioPonderadoUseCase.java): Sorteio acumulativo estocástico com suporte a seed determinística.
   - [CalcularAlvoDificuldadeUseCase](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/usecases/CalcularAlvoDificuldadeUseCase.java): Cálculo dinâmico da Classe de Dificuldade da interação.
   - [ResolverGrauSucessoUseCase](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/usecases/ResolverGrauSucessoUseCase.java): Resolução delta percentual nos 5 Graus de Sucesso.
   - [CalcularMassaProceduralUseCase](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/domain/usecases/CalcularMassaProceduralUseCase.java): Cálculo automático de massa baseado em slots $\times$ densidade.

4. **Camada de Dados & Repositórios:**
   - [SimpleJsonParser](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/data/json/SimpleJsonParser.java): Parser JSON nativo, recursivo e seguro, sem dependências externas, suportando comentários `//` no estilo CDDA/JSON5.
   - [JsonMaterialRepository](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/data/repositories/JsonMaterialRepository.java): Indexação de materiais em memória.
   - [JsonDamageTypeRepository](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/data/repositories/JsonDamageTypeRepository.java): Indexação de danos em memória.
   - [CascadeModLoader](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/main/java/com/omini/engine/data/loader/CascadeModLoader.java): Orquestrador de injeção em camadas (`base_game` sobrescrito por `mods/`).

---

## 🔬 3. Testes Executados & Resultados

### Camada 1: Testes Unitários dos UseCases
- [CalcularRangeTesteUseCaseTest](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/test/java/com/omini/engine/unit/CalcularRangeTesteUseCaseTest.java): Aprovado (ranges corretos para atributos 10, 50, 100 e validação *fail-fast* de limites).
- [CalcularDistribuicaoPesosUseCaseTest](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/test/java/com/omini/engine/unit/CalcularDistribuicaoPesosUseCaseTest.java): Aprovado (Rampa decrescente comprovada; herança do Monte de Areia onde Grão-Mestre 1 $\ge$ Mestre 10; ausência de pesos nulos).
- [ResolverGrauSucessoUseCaseTest](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/test/java/com/omini/engine/unit/ResolverGrauSucessoUseCaseTest.java): Aprovado (Classificação exata dos 5 graus com deltas $\le -50\%$, $-15\%$, $0\%$, $+20\%$).
- [CalcularMassaProceduralUseCaseTest](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/test/java/com/omini/engine/unit/CalcularMassaProceduralUseCaseTest.java): Aprovado (Travesseiro de penas pesando 0.4 vs bloco de ouro pesando 77.2 nos mesmos 4 slots).

### Camada 2: Testes de Integração
- [NucleoMotorIntegrationTest](file:///c:/Users/João/Documents/Programas/Java/From%20Omini/Desenvolvimento/src/test/java/com/omini/engine/integration/NucleoMotorIntegrationTest.java): Aprovado (Carregamento real dos JSONs de `materials.json` e `damage_types.json` via `CascadeModLoader`, cálculo do alvo da parede de granito nível 60, comprovação matemática de que uma espada de corte gera Falha Crítica absoluta contra granito devido ao multiplicador 0.05, e sucesso do jogador forte com picareta).

---

## ⚠️ 4. Desafios Encontrados & Como Foram Superados
- **Codificação de Caminhos no Windows (`João`):** Caminhos absolutos com caracteres acentuados geravam falhas de arquivo ao invocar ferramentas externas via arquivo de argumentos (`@sources.txt`). **Solução:** O script de compilação foi ajustado para utilizar caminhos relativos normalizados sem caracteres especiais e salvamento UTF-8 sem BOM.
- **Sintaxe PowerShell vs Bash:** Operadores encadeados do Bash (`&&`) não são suportados nativamente pelo PowerShell 5.1 no Windows. **Solução:** Uso estrito do separador de comandos do PowerShell (`;`) ou scripts dedicados.

---

## 🛡️ 5. Alertas & Pontos de Atenção para a Fase 2
- Ao criar o sistema de inventário em grade bidimensional, garantir que itens rotacionados (90°) invertam sua largura e altura geométricas sem alterar sua área total ou massa procedural.
- O carregamento de entidades em JSON na Fase 2 deverá vincular os materiais carregados na Fase 1 por meio de referências por ID (`material_id`), preservando a consistência referencial.
