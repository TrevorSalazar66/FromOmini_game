# Blueprint Base de Desenvolvimento (Starter Blueprint)

Este repositório é a **matriz oficial de governança, diretrizes e arquitetura** para todos os novos projetos de software (Web, Mobile Flutter, Desktop, Cloudflare Workers/Edge, Godot e Python CLI).

Ele define o ciclo de vida rigoroso de desenvolvimento, regras de Engenharia de Software, convenções de código, matrizes de decisão de stack e procedimentos para assistentes de IA (Antigravity IDE / Gemini) e desenvolvedores humanos.

---

## 📂 Organização Oficial de Espaço de Trabalho

Ao criar um novo projeto a partir deste blueprint, o espaço de trabalho é organizado em duas pastas na raiz:

```text
meu-novo-projeto/
├── Diretrizes/                      # REPOSITÓRIO BASE DE DIRETRIZES (Guia Consultivo)
│   ├── .agents/                    # Regras automáticas e skills operacionais para IA
│   │   ├── rules/                  # architecture.md, code-style.md, git-workflow.md
│   │   └── skills/                 # setup-project, create-component, deploy-guide
│   ├── docs/                       # Documentação e matrizes de decisão
│   │   ├── metodologias-e-fluxo.md # Workflow End-to-End, Tríade e testes em 3 camadas
│   │   ├── stacks-e-tecnologias.md # Matriz de decisão técnica
│   │   ├── hospedagem-e-infra.md   # Matriz de hospedagem e persistência (JSON / D1)
│   │   └── ferramentas-externas.md # Catálogo oficial de ferramentas locais
│   ├── templates/                  # Boilerplates de referência
│   │   ├── mobile-flutter/         # Base Flutter (Offline, Híbrido, Online)
│   │   ├── edge-cloudflare-worker/ # Base Hono.js / Worker Multi-Hosting
│   │   └── web-nextjs/             # Base Web / PWA
│   ├── GEMINI.md                   # Princípios cardeais para a IA
│   └── README.md                   # Apresentação das diretrizes
│
└── Desenvolvimento/                 # ARQUIVOS OFICIAIS DO PROJETO EM DESENVOLVIMENTO
    ├── idealizacao.md              # Tríade 1: Conceito & GDD
    ├── planejamento.md             # Tríade 2: Faseamento estruturado
    ├── funcoes.md                  # Tríade 3: Especificação técnica detalhada
    ├── estrutura-pastas.md         # Governança de Arquitetura Viva
    ├── tarefas.md                  # Tarefas do ciclo/subfase ativo
    └── (código-fonte do app)       # Código de produção, componentes e testes
```

---

## 🚀 Como Inicializar um Novo Projeto

1. **Clonar este repositório:**
   ```bash
   git clone https://github.com/TrevorSalazar66/template-base-allProjects.git
   ```
2. **Organizar o Workspace:**
   - Coloque os arquivos deste template na pasta `Diretrizes/`.
   - Crie a pasta `Desenvolvimento/` para conter o novo projeto.
3. **Desvincular o Git do Template:**
   ```bash
   # Remover o remote original do template
   git remote remove origin

   # Conectar ao novo repositório criado no GitHub:
   git remote add origin https://github.com/usuario/novo-repositorio.git
   ```
4. **Executar a Tríade de Especificação:**
   - Crie `idealizacao.md`, `planejamento.md` e `funcoes.md` dentro de `Desenvolvimento/`.
   - Crie `Desenvolvimento/estrutura-pastas.md`.
5. **Desenvolver por Ciclos Modulares:**
   - Inicie a branch `fase-1`, crie `Desenvolvimento/tarefas.md` e execute com foco em **Qualidade sobre Velocidade**.

