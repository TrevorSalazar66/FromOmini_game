# Diretrizes Globais do Projeto (GEMINI.md)

Este documento define os princípios mandatórios e diretrizes que os assistentes de IA (Antigravity IDE / Gemini) e o desenvolvedor humano devem seguir rigorosamente em todos os projetos.

---

## 🏛️ 1. Estrutura do Workspace: Diretrizes vs Desenvolvimento

- **`Diretrizes/` (Guia Consultivo & Repositório Base):**
  - Contém as regras de IA (`.agents/`), documentações (`docs/`), blueprints (`templates/`) e este `GEMINI.md`.
  - **Função:** Serve exclusivamente como material de consulta, referência e padronização. Nenhum código de produção do app final deve ser escrito aqui.
- **`Desenvolvimento/` (Área Oficial do Projeto Ativo):**
  - Onde todo o projeto é construído: Tríade de Especificação (`idealizacao.md`, `planejamento.md`, `funcoes.md`), Governança (`estrutura-pastas.md`), Tarefas (`tarefas.md`), código-fonte, componentes, assets e testes.

---

## 🎯 2. Princípios Cardeais de Engenharia & IA

1. **Sempre em Português do Brasil:**
   - Todas as respostas, mensagens de commit, documentações, especificações e comentários de código devem ser estritamente em **Português do Brasil (PT-BR)**.
2. **Qualidade sobre Velocidade:**
   - É terminantemente proibido pular etapas de planejamento, apressar decisões arquiteturais ou gerar código sem passar pela Tríade de Especificação e governança de pastas.
3. **Clean Architecture & Responsabilidade Única:**
   - Separação estrita em 4 camadas (`UI`, `Controllers/State`, `Domain/UseCases`, `Data/Repositories`).
   - Proibido arquivos e funções "faz-tudo". Cada arquivo tem uma única responsabilidade.
4. **Segurança Zero-Trust & Fail-Fast:**
   - Validação estrita de entradas em todas as fronteiras (Zod / Schemas). O servidor nunca confia cegamente no cliente.
5. **Mitigação Inteligente de Servidor (Client-Side First & Custo Zero):**
   - Executar cálculos visuais, estado pessoal offline e renderizações no aparelho do usuário.
   - Reservar servidores externos apenas para autenticação, validação crítica autoritativa e sincronização.
6. **Design Minimalista, Intuitivo e SVG-First:**
   - Todos os ícones e vetores em formato `.svg` leve (Lucide/Tabler Icons).
   - Interfaces intuitivas até mesmo para usuários leigos, com micro-interações ágeis e alto desempenho.

---

## 📚 3. Roteiro de Consulta Obrigatória para a IA

Antes de propor soluções ou executar tarefas, consulte:
- **`Diretrizes/docs/metodologias-e-fluxo.md`:** Ciclo de desenvolvimento, Tríade, testes em 3 camadas e logs de ciclo.
- **`Diretrizes/docs/stacks-e-tecnologias.md`:** Matriz de decisão de tecnologias.
- **`Diretrizes/docs/hospedagem-e-infra.md`:** Matriz de hospedagem e estratégia de persistência (`.json` local / nuvem).
- **`Diretrizes/docs/ferramentas-externas.md`:** Catálogo de ferramentas locais (Bruno, SVGOMG, FFmpeg, BlueStacks).
- **`Diretrizes/.agents/rules/`:** Regras de arquitetura, estilo de código e convenções de Git.
- **`Diretrizes/.agents/skills/`:** Procedimentos operacionais (`setup-project`, `create-component`, `deploy-guide`).

