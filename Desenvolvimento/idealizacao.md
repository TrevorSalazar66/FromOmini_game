# Idealização & Documento de Design de Jogo (GDD)

## 🌌 1. Conceito Central & Propósito

O projeto nasce da convergência de dois expoentes dos jogos roguelike e sobrevivência: a **profundidade sistêmica, física e emergente do *Cataclysm: Dark Days Ahead (CDDA)*** aliada à **usabilidade moderna, ergonomia de toque e elegância de interface do *Shattered Pixel Dungeon (SPD)***.

### A Dor do Mercado & Nossa Solução
Historicamente, jogos de sobrevivência ultradetalhados sofrem com barreiras de entrada intransponíveis em dispositivos móveis:
- O *CDDA* oferece uma simulação sem paralelo (reações químicas, física de materiais, anatomia detalhada e termodinâmica), mas exige um teclado inteiro de atalhos e telas densas com texto puro, tornando a experiência em telas de toque frustrante e inacessível para a maioria dos jogadores.
- O *Shattered Pixel Dungeon* domina com maestria a experiência mobile: controle com uma mão só, menus contextuais que aparecem na ponta dos dedos, arte visual em pixel art clara e ciclo de gameplay ágil por turnos. No entanto, possui escopo mecânico deliberadamente simplificado.

Nossa meta é criar o **padrão-ouro dos roguelikes de sobrevivência mobile**: um jogo completo, tático e de ritmo rápido por turnos, no qual cada ação é acessível com toques naturais na tela, enquanto nos bastidores roda uma simulação física implacável, viva e reativa a cada decisão.

---

## 🏛️ 2. Filosofia "Engine-as-a-Game" & Modding Livre

O jogo não é um sistema fechado; ele foi concebido como uma **Engine Sandbox empacotada com um Jogo Base Oficial completo**.

1. **Código Puro e Desacoplado (Java):**
   - O motor é desenvolvido em código limpo, sem dependência de interfaces visuais de engines comerciais para definir lógica, tornando o sistema enxuto, portável e ideal para manutenção e expansão assistida por IA.
2. **Arquitetura 100% Orientada a Dados (Data-Oriented Design via JSON):**
   - O motor em Java é "agnóstico": ele não sabe previamente o que é um "martelo", um "zumbi" ou uma "parede de pedra". Ele atua como um interpretador matemático de propriedades, entidades e componentes descritos em arquivos JSON.
3. **Superação do Scoped Storage no Android:**
   - Para garantir facilidade de modificação no ecossistema Android moderno (versões 11+ com restrições severas em `Android/data` e `Android/obb`), o executável do motor fica protegido na instalação do app, mas gera e consome toda a sua biblioteca de arquivos e mods diretamente na pasta pública de usuário: `Documentos/[NomeDoJogo]/`.
   - Qualquer jogador ou modder pode conectar o celular ao computador ou usar um simples aplicativo de bloco de notas no próprio smartphone para alterar valores, balanceamentos, criar novos itens, monstros, biomas ou até conversões totais.
4. **Camadas de Simulação Configuráveis (Feature Flags):**
   - O arquivo `world_rules.json` permite alternar interruptores de simulação (ex.: termodinâmica profunda, condução elétrica, encumbrance avançado). Caso desativados, o motor automaticamente faz o *fallback* para proteções e cálculos planos, atendendo desde o público casual até os jogadores veteranos sem que seja necessário recompilar o código.

---

## 📱 3. Visão Geral das Telas e Experiência do Usuário (UX/UI)

A interface é concebida a partir do princípio de **esconder a matemática pesada sob uma apresentação gráfica limpa e responsiva**:

### 🗺️ A. Tela de Exploração e Combate (Tela Principal)
- **Câmera & Visual:** Perspectiva superior (Top-Down) com grade visível sutil, renderização fluida e sprites limpos em pixel art.
- **Interação Touch-First:** O jogador toca em um tile para se movimentar até lá (com *pathfinding* automático A*). 
- **Ações Contextuais:** Tocar em uma entidade interativa (ex.: porta de madeira, armário, veículo abandonado) abre instantaneamente um menu radial/pop-up limpo com as opções permitidas pelas propriedades daquele objeto:
  - `[Abrir]` `[Arrombar]` `[Desmontar]` `[Examinar]`
- **Indicadores de Estado (Feedback Imediato):** Ícones minimalistas no HUD indicam condições críticas do personagem (ex.: termômetro azul para hipotermia, peso vermelho para sobrecarga, gota para sangramento). Um toque no ícone abre uma breve descrição contextual com orientações de primeiros socorros ou sobrevivência.

### 🎒 B. Tela de Inventário (Grade Geométrica & Massa Oculta)
- **Estrutura Visual:** Grade matricial inspirada em clássicos táticos (*Resident Evil 4*, *Tarkov* e *Titan Quest*).
- **Gestão de Espaço:** Cada item ocupa uma geometria física (ex.: Faca $1 \times 2$, Mochila $3 \times 4$, Munição $1 \times 1$). O jogador organiza, rotaciona e encaixa seus itens como um quebra-cabeça tátil via *drag-and-drop*.
- **Barra de Carga Dinâmica:** Uma barra visual na parte inferior exibe a carga total em relação à Força do personagem. O cálculo do peso ocorre automaticamente pela densidade dos materiais do item, eliminando cálculos manuais e alertando visualmente quando o personagem entra em estado de sobrecarga (*Encumbrance*).

### 🩺 C. Tela de Fisiologia & Anatomia do Personagem
- **Silhueta Anatômica Segmentada:** O corpo do personagem é dividido em membros individuais:
  - Cabeça, Tronco, Braço Superior (E/D), Antebraço (E/D), Mão (E/D), Coxa (E/D), Canela (E/D) e Pé (E/D).
- **Estado dos Membros:** Cada membro possui sua própria integridade física (HP), suscetibilidade a fraturas e curativos específicos (talas, bandagens).
- **Métricas Vitais Simplificadas:**
  - **Fome e Sede:** Indicadores de saciedade estomacal e hidratação. O microgerenciamento de vitaminas do CDDA foi descartado em prol do ritmo tático, focando na necessidade básica de alimentação e água potável.
  - **Estamina & Fôlego:** Energia gasta ao correr, golpear ou carregar excesso de peso.
  - **Dor (Pain System):** Medidor de agonia que diminui o desempenho em testes físicos e limita a clareza mental do personagem.
  - **Foco (Mental Focus):** Recurso tático que multiplica o ganho de experiência e pode ser consumido ativamente para desferir golpes cirúrgicos em partes vitais.

---

## 🎲 4. Pilares de Game Design & Mecânicas Sistêmicas

### 📈 A. Sistema de Testes com Range Dinâmico e Herança de Curva
- O jogo descarta dados clássicos de probabilidade plana (1d20, 1d100).
- Os **Atributos** definem a amplitude do intervalo de sorteio ($Min = Atributo$, $Max = Atributo \times 10$), escalando continuamente sem teto rígido (*cap*).
- A **Inexperiência (Nível 0)** gera uma rampa de decaimento: o pior resultado tem peso máximo e o valor mais alto tem peso 1, incentivando o investimento em perícias.
- As **Perícias (70 Níveis em 7 Faixas)** funcionam sob o conceito do **"Monte de Areia"**: ao subir de nível ou transicionar de faixa (Aprendiz até Grão-Mestre), os pesos do sorteio não resetam. O motor herda a distribuição do nível anterior e empurra o pico de probabilidade gradualmente em direção ao topo do range, garantindo que o jogador nunca se sinta mais fraco ao atingir uma nova categoria.

### 🎯 B. Resolução por Graus de Sucesso
- Toda ação no mundo confronta o número sorteado contra uma **Classe de Dificuldade Dinâmica (Alvo)** gerada pelas propriedades do objeto (ex.: dureza do material $\times$ nível da estrutura).
- A margem percentual entre o valor obtido e o alvo divide o desfecho em 5 graus orgânicos:
  1. **Falha Crítica ($\le -50\%$):** Consequência severa (ferramenta quebra, recochete, autolesão).
  2. **Falha ($-15\%$ a $-49\%$):** Desperdício de tempo, ruído e estamina sem sucesso.
  3. **Sucesso Parcial (até $-14\%$):** O objetivo é atingido, mas com alto custo de esforço ou rendimento inferior.
  4. **Sucesso Total ($0\%$ a $+19\%$):** Sucesso padrão esperado.
  5. **Sucesso Extremo ($\ge +20\%$):** Desempenho impecável, baixo desgaste e obtenção de recursos raros.

### 🧪 C. Alquimia Ambiental & Reações em Cadeia
- O cenário é composto por superfícies (*fields*) reativas. Quando dois componentes colidem em um mesmo bloco da grade (ex.: Fogo e Água), uma regra em `environmental_reactions.json` extingue ambos e gera uma nova entidade volátil (Fumaça), que por sua vez bloqueia linhas de visão e causa tosse/asfixia.
- Gases inflamáveis, combustíveis derramados, choque elétrico em poças e propagação térmica operam de forma orgânica turno a turno.

### ⚖️ D. Massa Procedural Automatizada
- O peso dos itens não requer digitação manual de valores arbitrários. O motor calcula:
  $$\text{Peso} = (\text{Largura em Slots} \times \text{Altura em Slots}) \times \text{Densidade do Material}$$
- Mudar o material de ferro para ouro no JSON quintuplica automaticamente o peso e ajusta os impactos de sobrecarga, facilitando enormemente o balanceamento de mods e itens novos.

---

## 🎯 5. Público-Alvo & Plataforma de Destino

- **Dispositivo Alvo Primário:** Smartphones e tablets Android (Touchscreen, execução 100% nativa e offline).
- **Público:** Fãs de roguelikes tradicionais, jogadores de RPG de mesa, entusiastas de simulações de sobrevivência tática e a comunidade ativa de modders de jogos sandbox.
- **Experiência de Sessão:** Sessões rápidas de 5 a 15 minutos (abrir o jogo, explorar uma sala, lutar um turno tático, salvar instantaneamente) que se sustentam em campanhas longas de dezenas de horas com mapa procedural persistente.
