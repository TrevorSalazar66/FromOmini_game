# Stacks & Tecnologias Padrão

Este documento serve como o catálogo oficial de tecnologias e o **Guia de Decisão de Stacks** do nosso ecossistema de desenvolvimento. Ele orienta a escolha técnica ideal para cada tipo de projeto, desde utilitários simples até ecossistemas híbridos complexos.

---

## ⚖️ 1. Princípio de Governança & Decisão

1. **A IA como Consultora:** O assistente de IA deve utilizar este documento para analisar os requisitos do projeto e **sugerir opções fundamentadas de stack**.
2. **Decisão Humana Soberana:** A decisão final de qual stack utilizar é **EXCLUSIVAMENTE do desenvolvedor humano**.

---

## 📐 2. Matriz de Decisão (Critérios para Escolha de Stack)

Antes de definir a stack de um novo projeto, o desenvolvedor e a IA devem avaliar os seguintes 6 critérios:

1. **Complexidade do Projeto:** Projeto simples (HTML/CSS/JS direto) vs Projeto complexo (TypeScript + Node.js + DB).
2. **Concorrência de Usuários:** Mono-usuário (ferramenta local) vs Múltiplos usuários simultâneos (Online).
3. **Modo de Execução:** 100% Local/Offline vs Online Serverless/Cloud Edge.
4. **Acesso Multi-Dispositivo:** Necessidade de sincronização de conta em múltiplos aparelhos vs Dispositivo isolado.
5. **Peso de Mídias e Ativos (`.mp3`, `.png`, `.svg`):** Uso de mídias pesadas puxadas por CDN/link externo vs Ativos embarcados.
6. **Objetivo Final:** MVP/Protótipo rápido vs Sistema definitivo em produção (suporte a PWA).

---

## 🛠️ 3. Catálogo de Stacks Padrão por Categoria

### 💻 3.1. Aplicações Web (Sites Comerciais, Dashboards, Portais e SaaS)
- **Projetos Extremamente Simples:**
  - **Stack:** HTML5 + CSS3 Vanilla + JavaScript Vanilla.
  - **Foco:** Carregamento instantâneo, zero build step, máxima leveza.
- **Projetos Complexos & Escalonáveis:**
  - **Frontend:** TypeScript + HTML5 + TailwindCSS.
  - **Backend:** Node.js (ou Cloudflare Workers).
  - **Banco de Dados:** SQL (SQLite / Cloudflare D1).
  - **Mídias & Assets:** SVG para ícones/gráficos, arquivos `.png`/`.mp3` hospedados externamente via CDN/links.
  - **Suporte a PWA:** Implementação de Progressive Web App (PWA) para permitir instalação como aplicativo direto no celular ou PC.

---

### 📱 3.2. Aplicações Mobile (Android / iOS)
- **Apps de Negócios & Utilitários:**
  - **Stack:** Flutter com linguagem Dart.
  - **Arquitetura:** Arquitetura em camadas (UI, Logic, Data).
- **Apps Interativos & Jogos Mobile:**
  - **Stack:** Godot Engine (conforme a complexidade gráfica e escopo).

---

### 🖥️ 3.3. Aplicações Desktop / PC
- **Utilitários & Ferramentas:**
  - **Stack:** Flutter Desktop ou Electron / Tauri (aproveitando a stack Web TypeScript/HTML).
- **Interfaces Gráficas Ricas & Jogos para PC:**
  - **Stack:** Godot Engine ou ferramentas padrão focadas para PC.

---

### 🐍 3.4. Ferramentas CLI, Terminal, Automações & Bots
- **Linguagem Oficial:** **Python**
- **Casos de Uso:** Ferramentas de linha de comando (CLI), scripts de automação, bots de texto (Discord/WhatsApp), web scrapers e utilitários de sistema.
- **Vantagens:** Desenvolvimento rápido, vasto ecossistema de bibliotecas e excelente manipulação de arquivos/dados.

---

### 🎲 3.5. Jogos Digitais, RPGs & Ferramentas para RPG de Mesa
- **Jogos 2D / 3D & Interfaces Interativas:**
  - **Engine:** Godot Engine (GDScript / C#).
- **Jogos Online / Multiplayer:**
  - **Cliente:** Godot Engine (ou Web PWA).
  - **Backend:** Node.js leve dedicado para validação de regras, proteção contra fraudes e segurança.
  - **Arquitetura:** Delegação de processamento seguro para o cliente para economizar recursos de servidor e reduzir latência.
- **Ferramentas de RPG de Mesa (Fichas, Geradores, Mapas):**
  - **Stack:** Aplicação Web Complexa (TypeScript + TailwindCSS + Node.js) com suporte a PWA para uso híbrido online/offline em qualquer dispositivo.

---

### 🔄 3.6. Integrações Híbridas & Ecossistemas Multissistema
Projetos que conectam Web + Mobile + Desktop + Bots + CLI:
- **Comunicação de Dados:**
  - **APIs REST (JSON):** Para operações padrão de leitura e escrita.
  - **WebSockets / Eventos em Tempo Real:** Para jogos multiplayer e sincronização ao vivo.
- **Diretriz de Escolha:** O protocolo é flexível e deve ser ajustado considerando os limites do plano de hospedagem, prioridade de latência e custo operacional.

---

## 🗄️ 4. Diretrizes de Banco de Dados & Persistência

- **Aplicações Online & Multi-Usuário Simultâneo:**
  - **Tecnologias:** **SQLite** e **Cloudflare D1**.
  - **Acesso a Dados:** SQL direto/puro e drivers nativos simples (sem camadas desnecessárias de complicação).
- **Aplicações 100% Locais & Uso Pessoal:**
  - **Tecnologia:** Arquivos **JSON** locais para leveza e facilidade de manipulação.

---

## 🎨 5. Diretrizes de Mídia & Desempenho (Assets)

- **Imagens e Áudios Pesados (`.mp3`, `.png`):** Devem ser referenciados via links externos / CDNs dedicadas para evitar o inchaço do repositório Git e garantir rápido carregamento.
- **Vetores (`.svg`):** Utilizados para ícones e elementos visuais de UI de alta resolução.
