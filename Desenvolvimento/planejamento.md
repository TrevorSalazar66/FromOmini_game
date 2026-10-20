# Planejamento Estruturado do Projeto (Faseamento Rígido)

Este documento estabelece o roteiro de desenvolvimento do projeto, organizado em Fases e Subfases estritas, respeitando o princípio de **Qualidade sobre Velocidade** e a arquitetura em camadas.

---

## 📋 Lista Estruturada de Tópicos por Fase

### 🏗️ FASE 1: Núcleo do Motor de Dados, Física de Materiais & Testes Ponderados
- **1.1. Arquitetura Base do Projeto Java:** Configuração do ecossistema de build (Gradle), separação em camadas limpas (`UI`, `Controllers`, `Domain`, `Data`) e biblioteca para manipulação JSON ágil.
- **1.2. Abstração de Armazenamento e Scoped Storage Android:** Implementação do leitor de caminhos públicos (`Documentos/[NomeDoJogo]/`), injeção de dependências de arquivos e gerador da estrutura base de pastas no primeiro boot.
- **1.3. Motor de Propriedades, Danos e Materiais:** Mapeamento de `materials.json` e `damage_types.json`, implementando resistências, fraquezas e dureza base.
- **1.4. Motor Matemático de Testes Dinâmicos (Atributos & Curva Contínua):** Algoritmo de geração do range linear sem teto ($Min = Atributo$, $Max = Atributo \times 10$), Rampa de Decaimento para Nível 0 e curva de sorteio ponderado com 70 níveis contínuos ("Monte de Areia").
- **1.5. Resolução por Graus de Sucesso & Dificuldade Dinâmica:** Cálculo do Alvo a partir das propriedades das entidades e classificação da rolagem nas 5 faixas percentuais de resultado.

---

### 🎒 FASE 2: Entidades, Inventário em Grade & Massa Procedural
- **2.1. Arquitetura ECS Enxuta (Entity-Component-System):** Definição de Entidades e Componentes puros em código desacoplado para representar itens, estruturas e personagens.
- **2.2. Sistema de Inventário em Grade Bidimensional:** Lógica matricial de slots (estilo Tetris / *Resident Evil*), suporte a rotação de itens, encaixe geométrico e limites de bolsas.
- **2.3. Cálculo de Massa Procedural & Sobrecarga (*Encumbrance*):** Motor de cálculo automático de peso baseado em área de slots $\times$ densidade do material, com penalidades progressivas de velocidade e esquiva.
- **2.4. Mecanismo de Mod Loader & Sobrescrita de Dados:** Leitor de diretório `mods/` com injeção em memória e substituição de itens e propriedades com base em IDs idênticos.

---

### 🩺 FASE 3: Fisiologia do Personagem, Anatomia, Dor & Foco
- **3.1. Anatomia Expandida e Sorteio por Superfície:** Mapeamento de 16 partes do corpo em `anatomy.json`, cálculo de probabilidade ponderada de impacto e modificadores de mira e cobertura.
- **3.2. Saúde Localizada, Fraturas e Primeiros Socorros:** Integridade física independente por membro, impacto de membros fraturados na jogabilidade e curativos táticos.
- **3.3. Sistema Tático de Dor e Farmacologia:** Acúmulo de dor, aplicação de penalidades escalonadas nos graus de sucesso e remédios com tempo de ativação em turnos.
- **3.4. Mecânica de Foco e Progressão Ativa:** Cálculo do teto de foco ($100 - Dor$), multiplicador de ganho de XP por perícia e habilidade ativa de mira cirúrgica.
- **3.5. Ciclo de Sobrevivência Básica:** Gestão enxuta de Fome, Sede, Estamina e Sono sem microgerenciamento de vitaminas.

---

### 🗺️ FASE 4: Grid Espacial, Alquimia Ambiental & Turnos
- **4.1. Mapa Matricial de Tiles & Pathfinding:** Representação espacial 2D, navegação por A* e verificação de linha de visão (*Field of View* / Shadowcasting).
- **4.2. Superfícies Reativas & Alquimia Elemental:** Motor de campos voláteis (`environmental_reactions.json`) controlando turno a turno fogo, água, fumaça tóxica, poças elétricas e gases inflamáveis.
- **4.3. Motor de Turnos e Pontos de Ação (AP):** Sistema de iniciativa e consumo de pontos de ação proporcionais à velocidade, peso e debuffs corporais.
- **4.4. Gerador Procedural de Mapas Baseado em Dados:** Leitor de parâmetros de biomas em `mapgen/`, geração de salas, corredores e suporte a presets de estruturas pré-fabricadas.

---

### 📱 FASE 5: Interface Mobile Touch-First, Áudio & Polimento
- **5.1. Camada de Apresentação e Renderizador 2D:** Implementação da tela em pixel art limpo com suporte a toque de baixa latência para Android.
- **5.2. Menus Contextuais & Gestos Naturais (Estilo SPD):** Toque para andar, pop-up radial de ações contextuais em objetos e arrastar-e-soltar fluido no inventário.
- **5.3. Silhueta de Status Anatômico & HUD Minimalista:** Painel de saúde do corpo, barras de carga e indicadores de status sem números excessivos na tela principal.
- **5.4. Persistência de Partida & Configurações de Sandbox:** Salvamento e carregamento atômico do estado do mundo em JSON e leitura de `world_rules.json`.
- **5.5. Validação Exaustiva, Pentest e Build Android (.apk):** Protocolo de testes de Camada 1, 2 e 3, remoção de mocks e geração do pacote final para instalação no celular.

---

## 📖 Parágrafos Explicativos Detalhados de Cada Tópico

### Detalhamento da FASE 1: Núcleo do Motor & Testes
- **1.1. Arquitetura Base do Projeto Java:** Configura a base do projeto utilizando Java 17+ com Gradle, estruturando os pacotes sob o padrão Clean Architecture em quatro camadas isoladas: UI (apresentação), Controllers (orquestração), Domain (regras de negócio e use cases puros) e Data (persistência e repositórios de leitura JSON).
- **1.2. Abstração de Armazenamento e Scoped Storage Android:** Desenvolve a camada de infraestrutura que resolve as restrições de permissão do Android 11+. O sistema verifica se o diretório `Documentos/[NomeDoJogo]/` existe; caso seja a primeira inicialização, extrai do pacote da aplicação os arquivos padrão do jogo base (`base_game/`) e cria a pasta de `mods/`, garantindo que os arquivos fiquem totalmente expostos e editáveis pelo usuário.
- **1.3. Motor de Propriedades, Danos e Materiais:** Cria os contratos e carregadores de `materials.json` e `damage_types.json`. Cada material especifica densidade, dureza base e multiplicadores contra tipos de dano (ex.: corte, contusão, perfuração, picareta), permitindo que qualquer nova matéria-prima criada por mods se integre automaticamente às equações físicas.
- **1.4. Motor Matemático de Testes Dinâmicos:** Implementa a lógica central de resolução de incerteza do jogo. O algoritmo recebe o Atributo do jogador e gera um intervalo contínuo de $Atributo$ a $Atributo \times 10$. Caso o jogador não tenha perícia (Nível 0), o motor aplica uma rampa decrescente de pesos onde o valor mais baixo tem a maior probabilidade. Se possuir perícia, o motor aplica a fórmula de interpolação contínua de 70 níveis ("Monte de Areia"), puxando os pesos para cima sem resetar na virada de faixas.
- **1.5. Resolução por Graus de Sucesso & Dificuldade Dinâmica:** Implementa o cálculo do Alvo Dinâmico confrontando a resistência do objeto contra o poder e ferramenta do jogador. O resultado sorteado é subtraído do Alvo e convertido em uma porcentagem que determina se a ação resultou em Falha Crítica ($\le -50\%$), Falha ($-15\%$ a $-49\%$), Sucesso Parcial (até $-14\%$), Sucesso Total ($0\%$ a $+19\%$) ou Sucesso Extremo ($\ge +20\%$).

---

### Detalhamento da FASE 2: Entidades, Inventário & Massa Procedural
- **2.1. Arquitetura ECS Enxuta:** Constrói uma implementação de Entity-Component-System em Java puro, desacoplada de frameworks. Cada objeto do mundo (jogadores, inimigos, paredes, itens caídos) é uma Entidade composta de Componentes de dados (`MaterialComponent`, `PositionComponent`, `HealthComponent`), permitindo flexibilidade total para extensão via JSON.
- **2.2. Sistema de Inventário em Grade Bidimensional:** Cria o sistema de inventário espacial onde bolsas e mochilas são representadas por matrizes de células (slots). Implementa validações geométricas para permitir inserção, rotação (90 graus) e remanejamento de itens com larguras e alturas diferentes, rejeitando inserções onde não houver espaço contínuo desocupado.
- **2.3. Cálculo de Massa Procedural & Sobrecarga:** Implementa a equação matemática que deriva o peso do item multiplicando sua área em slots pela densidade de seu material primário. O peso acumulado é confrontado contra a Força do personagem, gerando estados de carga que reduzem a velocidade e aumentam a dificuldade de esquivas conforme o limite é ultrapassado.
- **2.4. Mecanismo de Mod Loader & Sobrescrita de Dados:** Cria o algoritmo de carregamento em cascata. O motor lê todos os arquivos de `base_game/data/` e, em seguida, varre a pasta `mods/` em busca dos módulos ativos listados em `config.json`. Entidades ou itens que compartilham a mesma chave de identificação (`id`) substituem as definições base na memória, enquanto novos identificadores são incorporados à lista geral.

---

### Detalhamento da FASE 3: Fisiologia, Anatomia, Dor & Foco
- **3.1. Anatomia Expandida e Sorteio por Superfície:** Modela a representação do corpo em 16 partes a partir de `anatomy.json`. Cada golpe recebido consulta a tabela de pesos de superfície da anatomia do alvo, aplicando modificadores contextuais (como tiros através de muretas que anulam pernas ou ataques de varredura baixa que focam canelas e pés) para definir o membro atingido.
- **3.2. Saúde Localizada, Fraturas e Primeiros Socorros:** Gerencia a integridade individual de cada membro. Membros vitais (Cabeça e Tronco) que atingem zero de vida causam morte imediata, enquanto membros secundários fraturam, impedindo o manuseio de armas pesadas (braços) ou reduzindo a locomoção (pernas), demandando imobilização com talas e bandagens.
- **3.3. Sistema Tático de Dor e Farmacologia:** Implementa o medidor de dor de 0 a 100 que atua como penalidade redutora no resultado dos testes físicos do jogador. Modela consumíveis médicos (como analgésicos e morfina) com efeito em turnos de combate, criando uma dinâmica tática de recuo enquanto a medicação atua no organismo.
- **3.4. Mecânica de Foco e Progressão Ativa:** Estabelece o Foco mental do personagem, cujo valor máximo é reprimido diretamente pelo nível de dor atual. O foco atua como um multiplicador direto do ganho de experiência nas ações e pode ser drenado ativamente pelo jogador para desferir ataques com garantia de precisão em pontos vitais.
- **3.5. Ciclo de Sobrevivência Básica:** Implementa o consumo gradual de Fome, Sede e Estamina por turno e por esforço físico. O sistema descarta deliberadamente o controle minucioso de micronutrientes/vitaminas para manter a agilidade da partida, focando no risco iminente de desidratação, inanição ou exaustão física.

---

### Detalhamento da FASE 4: Grid Espacial, Alquimia & Turnos
- **4.1. Mapa Matricial de Tiles & Pathfinding:** Modela a grade de navegação bidimensional do mapa com algoritmo A* otimizado para celulares. Implementa cálculo de campo de visão (*FOV*) e neblina de guerra (*Fog of War*), revelando salas e corredores à medida que o personagem avança.
- **4.2. Superfícies Reativas & Alquimia Elemental:** Constrói o subsistema de efeitos de terreno turn-based. Quando duas ou mais propriedades elementares coexistem no mesmo tile (ex.: poça d'água e fonte de ignição), o motor extingue o fogo e gera uma nuvem de vapor/fumaça com raio de expansão e tempo de dissipação definidos em `environmental_reactions.json`.
- **4.3. Motor de Turnos e Pontos de Ação (AP):** Cria o relógio do jogo baseado em turnos discretos. Cada ação (andar, atacar, abrir inventário, recarregar) consome uma quantidade específica de Pontos de Ação, sincronizando o avanço do tempo das entidades de acordo com a velocidade individual de cada uma.
- **4.4. Gerador Procedural de Mapas Baseado em Dados:** Implementa o gerador de masmorras e cenários abertos interpretando arquivos JSON de bioma. O algoritmo lê pesos de conexão de salas, probabilidade de spawn de monstros, tipos de paredes e armadilhas, suportando também salas artesanais fixas (*prefabs*).

---

### Detalhamento da FASE 5: Interface Mobile, Áudio & Polimento
- **5.1. Camada de Apresentação e Renderizador 2D:** Constrói o motor de visualização em pixel art para Android com alta taxa de quadros e baixo consumo de bateria, renderizando tilesets, entidades, efeitos visuais e iluminação dinâmica.
- **5.2. Menus Contextuais & Gestos Naturais:** Desenvolve o sistema de controle touch-first na linha de design do *Shattered Pixel Dungeon*, permitindo navegação por toques diretos no mapa, abertura instantânea de menus de contexto em objetos e suporte a gestos de arrastar e pinça para zoom.
- **5.3. Silhueta de Status Anatômico & HUD Minimalista:** Projeta uma interface de usuário enxuta e livre de poluição visual. Um painel exibe a silhueta corporal com cores indicativas de lesão nos membros, acompanhado de barras dinâmicas para carga de peso, fome e estamina.
- **5.4. Persistência de Partida & Configurações de Sandbox:** Desenvolve o gerenciador de serialização de estado em formato JSON na pasta `saves/`, garantindo salvamento automático à prova de falhas a cada turno ou ao fechar o app, com leitura das regras configuráveis de `world_rules.json`.
- **5.5. Validação Exaustiva, Pentest e Build Android:** Executa as três camadas de testes unitários e de integração, sanitiza todo o código removendo dados mockados, audita vulnerabilidades locais e compila o pacote final instalável (.apk) pronto para testes em dispositivos reais.
