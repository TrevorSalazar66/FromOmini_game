# Catálogo Oficial de Ferramentas Externas & Utilitários

Este documento consolida o ecossistema de softwares, utilitários locais, bibliotecas de assets e serviços aprovados para uso no ciclo de vida de desenvolvimento dos nossos projetos.

---

## 🎯 Princípios de Escolha de Ferramentas

1. **Local-First & Offline:** Preferência absoluta por ferramentas que funcionem localmente na máquina, sem dependência obrigatória de conexão ou nuvem.
2. **SVG-First:** Quase a totalidade dos ativos visuais e ícones deve ser em formato **`.svg`** para garantir peso mínimo, escalabilidade infinita e estilização dinâmica via código/CSS.
3. **Custo Zero & Open Source:** Foco em soluções gratuitas, sem custos ocultos e com licenças permissivas.
4. **Evitar Conversores Online:** Conversões e manipulações de mídias e vetores devem ser feitas via utilitários locais (CLI) para garantir privacidade, rapidez e reprodutibilidade.

---

## 💻 1. Desenvolvimento, IDEs & Motores

| Ferramenta | Categoria | Finalidade no Ecossistema |
| :--- | :--- | :--- |
| **Antigravity IDE** | IDE com IA | Ambiente primário de pair programming com o assistente Gemini. |
| **Android Studio** | IDE / SDK | Compilação, SDK Android e desenvolvimento do ecossistema Flutter. |
| **Godot Engine** | Game Engine | Desenvolvimento de jogos 2D/3D, simulações e interfaces interativas ricas. |
| **Google Chrome** | Navegador | Plataforma de execução, depuração e testes de aplicações Web e PWAs. |
| **GitHub** | Versionamento | Hospedagem de repositórios, controle de versões e rastreamento de branches. |

---

## 🧠 2. Ideação, Estruturação & Inteligência Artificial

- **Obsidian:**
  - **Uso:** Ferramenta central para ideação profunda, anotações de GDD (Game Design Document), estruturação de ideias brutas e base de conhecimento interligada.
- **GIMP:**
  - **Uso:** Elaboração rápida de mapas mentais, diagramação visual base de interfaces frontend e recortes gráficos.
- **ChatGPT & Gemini:**
  - **Uso:** Brainstorming conceitual preliminar, estudo de regras de negócio complexas, estruturação de fluxos de dados e pesquisa exploratória.

---

## 🎨 3. Design, UI/UX & Ativos Visuais (Padrão SVG-First)

Para manter as aplicações ultra-leves e customizáveis, os ativos visuais priorizam vetores e geradores ágeis:

### 🧩 3.1. Ícones e Vetores Padronizados

- **Lucide Icons:** Ícones modernos, consistentes e integráveis diretamente como componentes React/Flutter/SVG.
- **Tabler Icons:** Vasto catálogo de ícones vetoriais personalizáveis para dashboards e interfaces ricas.
- **SVG Repo:** Banco de vetores e ilustrações gratuitas em SVG para busca pontual de elementos específicos.

### 🖼️ 3.2. Assets Complexos & Sprites

- **IAs Generativas de Imagem:** Utilizadas para geração sob demanda de assets artísticos conceituais complexos.
- **Repositórios Gratuitos Confiáveis:** *OpenGameArt.org*, *Kenney.nl* e *Itch.io Assets* para bases gráficas de jogos e RPGs.

### ⚙️ 3.3. Otimização de Vetores

- **SVGOMG (SVGO):** Ferramenta mandatória para limpeza de metadados, remoção de atributos inúteis de softwares de desenho e minificação extrema de arquivos `.svg` antes do commit.

---

## 🎵 4. Áudio, Efeitos Sonoros (SFX) & Músicas

O tratamento sonoro varia conforme a ambientação do projeto, priorizando leveza e execução procedural:

- **Audacity:**
  - **Uso:** Edição, corte, normalização de volume, equalização e exportação de áudios locais.
- **Áudio Procedural & Sintetizadores (Leveza Extrema):**
  - **Uso:** Geração de efeitos sonoros e músicas via código/partitura direta (ex: *Web Audio API, Tone.js, jsfxr*), evitando o tráfego de arquivos pesados pela rede.
- **Repositórios Sonoros Confiáveis:**
  - **Freesound.org:** Busca de efeitos sonoros reais e ambiências gratuitas com licença Creative Commons.

---

## 🧪 5. Testes, Diagnósticos & Emulação

- **Bruno (API Client):**
  - **Uso:** Cliente HTTP/API leve, rápido e 100% offline. Salva as coleções de requisições em arquivos de texto puro diretamente na pasta do projeto, permitindo versionamento no Git sem vazamento de dados na nuvem.
- **Chrome DevTools & Lighthouse:**
  - **Uso:** Depuração de código, inspeção de requisições de rede, auditoria de performance, acessibilidade (A11y), SEO e conformidade de PWA.
- **BlueStacks (Emulação Android no PC):**
  - **Uso:** Testes e homologação ágil de arquivos `.apk` gerados pelo Flutter diretamente no computador, sem necessidade de conectar dispositivos físicos ou abrir emuladores pesados do Android Studio.

---

## 🛠️ 6. Utilitários Locais de Linha de Comando (CLI)

- **FFmpeg:**
  - **Uso:** Conversão rápida de codecs, extração de áudio, compressão e ajuste de taxas de amostragem de áudios e vídeos via terminal local (ex: conversão para `.mp3`/`.ogg`/`.webm` com parâmetros ideais de compressão).
