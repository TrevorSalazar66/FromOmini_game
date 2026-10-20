---
name: create-component
description: Skill oficial para criação de componentes UI de alta performance, minimalistas, acessíveis e intuitivos em múltiplas plataformas (Web React/Next.js, Flutter/Dart, Vanilla JS e Godot).
---

# Procedimento Oficial para Criação de Componentes UI

Quando o assistente de IA for instruído a criar ou refatorar qualquer componente de interface de usuário, deve seguir estritamente as diretrizes estruturais, visuais e de usabilidade descritas abaixo.

---

## 🎨 1. Diretrizes de Design: Minimalismo, Alto Desempenho e Usabilidade Extrema

Todo componente deve ser projetado pensando no usuário final — inclusive usuários leigos sem familiaridade com tecnologias digitais:

1. **Intuitividade Absoluta (Zero Curva de Aprendizado):**
   - A função do componente deve ser autoexplicativa à primeira vista.
   - Textos de botões e rótulos claros e diretos (ex: "Salvar Dados", "Voltar", "Confirmar").
   - Feedbacks visuais imediatos ao clicar, passar o mouse ou focar (micro-interações sutis).
2. **Minimalismo e Alto Desempenho:**
   - Evitar poluição visual ou elementos decorativos desnecessários que atrasem a renderização.
   - Utilização de tokens de design via variáveis CSS / temas globais (`--bg-surface`, `--color-primary`, `--radius-md`).
   - Animações leves via CSS transitions nativas (150ms a 250ms), sem sobrecarregar a CPU/GPU do dispositivo.
3. **Padrão SVG-First:**
   - Ícones obrigatoriamente vetoriais em **`.svg`** (Lucide Icons ou Tabler Icons), com suporte a redimensionamento e `currentColor`.
4. **Acessibilidade Universal (A11y):**
   - Relação de contraste de cores legível.
   - Estados de foco visíveis (`:focus-visible`) para navegação por teclado ou leitores de tela.
   - Atributos semânticos e descritivos (`aria-label`, `role`, `tabindex`).

---

## 🧱 2. Estrutura de Código por Plataforma

### 💻 2.1. Web (React / Next.js / TypeScript)

- **Localização:** `src/components/<nome-componente>/`
- **Arquitetura de Arquivos:**
  ```text
  src/components/<nome-componente>/
  ├── <NomeDoComponente>.tsx        # Componente puro com props tipadas
  ├── <NomeDoComponente>.module.css # Estilos dedicados (ou Tailwind estruturado)
  └── index.ts                      # Re-exportação limpa
  ```
- **Padrão de Código:**
  - Componente puramente funcional (*Dumb Component*), sem chamadas diretas a APIs.
  - Interface de props explícita: `export interface <NomeDoComponente>Props { ... }`.
  - Uso de early return para estados de carregamento, erro ou vazio.

---

### 📱 2.2. Mobile (Flutter / Dart)

- **Localização:** `lib/presentation/widgets/<nome_componente>/`
- **Arquitetura de Arquivos:**
  ```text
  lib/presentation/widgets/<nome_componente>/
  └── <nome_componente>_widget.dart
  ```
- **Padrão de Código:**
  - Preferência por `StatelessWidget` com construtor `const`.
  - Parâmetros imutáveis e tipados (ex: `final VoidCallback? onPressed;`).
  - Uso do tema global (`Theme.of(context)`) para cores e tipografia.

---

### 🌐 2.3. Vanilla Web (HTML5 / CSS3 / JavaScript)

- **Localização:** `src/components/<nome-componente>/`
- **Padrão de Código:**
  - Módulos encapsulados ou Web Components nativos (`customElements.define`).
  - CSS com variáveis de escopo local e exportação limpa de funções de inicialização/evento.

---

### 🎮 2.4. Jogos & Interfaces Interativas (Godot Engine)

- **Localização:** `scenes/ui/<nome_componente>/`
- **Arquitetura de Arquivos:**
  ```text
  scenes/ui/<nome_componente>/
  ├── <nome_componente>.tscn        # Cena modular com nós de UI (Control, Container)
  └── <nome_componente>.gd          # Script associado com tipagem estática e sinais
  ```
- **Padrão de Código:**
  - Comunicação para fora da cena exclusivamente via **sinais tipados** (`signal button_clicked(id)`).
  - Nós devidamente ancorados com layout responsivo.

---

## ✅ 3. Checklist Obrigatório de Validação do Componente

Antes de dar o componente como concluído:
- [ ] O componente tem uma única responsabilidade visual?
- [ ] O componente é 100% responsivo (mobile e desktop)?
- [ ] Os ícones utilizados são em formato `.svg` leve?
- [ ] O componente possui estados de hover, active, focus e disabled claros?
- [ ] Nenhum dado hardcoded sensível está embutido no componente?
- [ ] A interface é compreensível até mesmo para um usuário leigo?

