# Governança de Arquitetura Viva & Estrutura de Pastas (estrutura-pastas.md)

Este documento define formalmente a arquitetura física e a responsabilidade estrita de cada diretório do projeto ativo dentro de `Desenvolvimento/`. 

> [!IMPORTANT]
> **REGRA VIVA & RESTRITIVA:**  
> A IA (Antigravity IDE / Gemini) e os desenvolvedores humanos devem consultar este documento antes de criar qualquer novo arquivo ou subpasta. É terminantemente proibido criar arquivos fora dos locais designados abaixo sem prévia revisão e atualização formal deste documento.

---

## 🌳 Árvore de Diretórios Oficial de `Desenvolvimento/`

```text
Desenvolvimento/
├── idealizacao.md                      # Tríade 1: Conceito Humano & GDD
├── planejamento.md                     # Tríade 2: Faseamento Estruturado
├── funcoes.md                          # Tríade 3: Especificação Técnica Detalhada
├── estrutura-pastas.md                 # Governança de Arquitetura Viva (Este arquivo)
├── tarefas.md                          # Tarefas ativas da fase/subfase em desenvolvimento
│
├── docs/                               # Registros e auditorias contínuas do projeto
│   ├── historico-ciclos/               # Logs formais de cada subfase concluída
│   └── incidencias-e-refatoracoes/     # Relatórios técnicos de bugs e refatorações
│
├── base_game/                          # O Pacote Oficial Vanilla (Copiado para Documentos/ no primeiro boot)
│   ├── game_info.json                  # Metadados do jogo base (versão, nome, autor)
│   ├── world_rules.json                # Feature flags de simulação (termodinâmica, eletricidade, etc.)
│   ├── assets/                         # Mídias e recursos gráficos do jogo base
│   │   ├── sprites/                    # Sprites de itens, entidades e tilesets
│   │   ├── ui/                         # Ícones vetoriais (.svg) e elementos de interface
│   │   └── audio/                      # Efeitos sonoros (.wav/.ogg) e músicas
│   └── data/                           # Banco de dados JSON do jogo base
│       ├── materials.json              # Densidades, dureza base e multiplicadores de materiais
│       ├── damage_types.json           # Tipos de dano e multiplicadores contra tags
│       ├── anatomy.json                # Mapeamento anatômico de membros e área de superfície
│       ├── environmental_reactions.json# Regras de alquimia ambiental e campos reativos
│       ├── items/                      # JSONs de armas, armaduras, ferramentas e consumíveis
│       ├── entities/                   # JSONs de monstros, NPCs e facções
│       └── mapgen/                     # Presets de salas e regras procedurais de biomas
│
└── src/                                # Código-fonte do Motor em Java Puro
    ├── main/
    │   ├── java/
    │   │   └── com/
    │   │       └── omini/
    │   │           └── engine/
    │   │               ├── core/        # Loop do jogo, relógio e barramento de eventos
    │   │               ├── domain/      # Camada 3: Regras de negócio puras (Clean Architecture)
    │   │               │   ├── usecases/# Casos de uso (estritamente 1 ação por arquivo)
    │   │               │   ├── models/  # Entidades de domínio e componentes ECS
    │   │               │   └── dtos/    # Objetos de transferência de dados e Vo's imutáveis
    │   │               ├── controllers/ # Camada 2: Orquestração de turnos, inputs e telas
    │   │               ├── data/        # Camada 4: Repositórios concretos e adaptadores JSON
    │   │               │   ├── repositories/ # Implementação concreta de leitura JSON
    │   │               │   └── loader/  # CascadeModLoader e gerenciador do Scoped Storage
    │   │               └── ui/          # Camada 1: Componentes gráficos touch-first e HUD
    │   │                   ├── views/   # Telas principais (mapa, inventário, anatomia)
    │   │                   └── widgets/ # Widgets reutilizáveis (grade, silhueta, pop-ups)
    │   └── resources/                   # Configurações internas de empacotamento
    │
    └── test/                            # Protocolo de Testes em 3 Camadas
        └── java/
            └── com/
                └── omini/
                    └── engine/
                        ├── unit/        # Camada 1: Testes unitários locais dos UseCases
                        ├── integration/ # Camada 2: Testes de integração entre módulos
                        └── regression/  # Camada 3: Varreduras sistêmicas e pentest de regras
```

---

## 📋 Responsabilidade e Restrições por Diretório

### 1. Raiz de `Desenvolvimento/`
- **Finalidade:** Governança e especificação do projeto ativo.
- **Arquivos Permitidos:** `idealizacao.md`, `planejamento.md`, `funcoes.md`, `estrutura-pastas.md`, `tarefas.md` e arquivos de configuração de compilação na raiz (ex.: `build.gradle.kts`, `settings.gradle.kts`).
- **Restrição:** Nenhum arquivo de código `.java` pode ser colocado na raiz.

### 2. `Desenvolvimento/docs/`
- **`historico-ciclos/`:** Guarda os registros ao final de cada subfase concluída, detalhando implementações, superação de erros e pontos de atenção.
- **`incidencias-e-refatoracoes/`:** Guarda registros formais de bugs ou refatorações de módulos anteriores, documentando causas, soluções e impactos.

### 3. `Desenvolvimento/base_game/`
- **Finalidade:** Contém todos os arquivos originais que o jogo precisa para funcionar. É a pasta que o motor clona para `Documentos/[NomeDoJogo]/` no primeiro carregamento do aplicativo no Android.
- **Restrição:** Não deve conter lógica de compilação ou arquivos Java. Apenas arquivos `.json`, `.png`, `.svg` e `.ogg`.

### 4. `Desenvolvimento/src/main/java/.../domain/`
- **Finalidade:** O coração das regras de negócio do jogo.
- **Regra de Ouro:** Não importa bibliotecas de UI, gráficos ou Android. É Java puro e determinístico.
- **`usecases/`:** Cada caso de uso é uma classe com uma única função pública executável (ex.: `CalcularMassaProceduralUseCase.java`). Proibido acumular múltiplos casos de uso em um único arquivo.

### 5. `Desenvolvimento/src/main/java/.../data/`
- **Finalidade:** Responsável por carregar, parsear e persistir os arquivos JSON.
- **`loader/CascadeModLoader.java`:** Orquestra a injeção em camadas (`base_game` + `mods`).
- **`repositories/`:** Implementações concretas de interfaces de repositório definidas no domínio.

### 6. `Desenvolvimento/src/main/java/.../ui/`
- **Finalidade:** Renderização visual para telas de toque mobile (estilo *Shattered Pixel Dungeon*).
- **Restrição:** Componentes de UI não executam cálculos de negócio; eles apenas emitem eventos para os `controllers` e renderizam dados recebidos.

### 7. `Desenvolvimento/src/test/`
- **`unit/` (Camada 1):** Valida a matemática de range, pesos probabilísticos, graus de sucesso e massa procedural.
- **`integration/` (Camada 2):** Valida a comunicação entre o `CascadeModLoader`, repositórios JSON e os UseCases.
- **`regression/` (Camada 3):** Valida a estabilidade do sistema como um todo antes de cada grande deploy ou release de fase.
