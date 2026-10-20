# Convenções e Workflow Git Oficial

Este documento estabelece as regras mandatórias de branching, padronização de commits, versionamento e governança de repositório que devem ser seguidas rigorosamente por desenvolvedores e assistentes de IA (Antigravity IDE / Gemini).

---

## 🌿 1. Estrutura e Nomenclatura de Branches

O repositório opera com fluxo estruturado por fases alinhado ao `metodologias-e-fluxo.md`:

| Tipo de Branch | Padrão de Nomenclatura | Finalidade |
| :--- | :--- | :--- |
| **Branch Principal** | **`main`** | Código oficial, estável e homologado em produção. |
| **Branch por Fase** | **`fase-<N>`** (ex: `fase-1`, `fase-2`) | Desenvolvimento da fase atual do planejamento. |
| **Branch por Subfase** | **`fase-<N>/subfase-<nome>`** | Módulos complexos dentro de uma fase. |
| **Correções Urgentes** | **`hotfix/<descricao>`** | Correções pontuais e críticas de bugs. |
| **Manutenção / Chores** | **`chore/<descricao>`** | Atualização de dependências, builds ou linter. |
| **Documentação Isolada**| **`docs/<descricao>`** | Ajustes avulsos em manuais e documentações. |

---

## 🔀 2. Fluxo de Unificação e Merge na Branch `main`

A branch de uma fase só deve ser unificada (mergeada) com a branch `main` após o cumprimento rigoroso dos seguintes critérios:

1. **Conclusão de 100% dos Módulos da Fase:** Todas as subfases daquele ciclo finalizadas e com logs registrados.
2. **Testes em 3 Camadas Aprovados:** Validação local e integração de todos os componentes do módulo.
3. **Validação em Link Provisório:** Teste real em ambiente de preview/staging fornecido pela plataforma de hospedagem.
4. **Sanitização Pré-Merge Executada:** Remoção completa de dados mockados, stubs temporários e prints/logs de depuração.
5. **Criação de Tag SemVer:** Criação da tag de versão correspondente (ex: `git tag v0.1.0-fase-1`).
6. **Atualização do `CHANGELOG.md`:** Registro formal de todas as novidades, correções e melhorias introduzidas na versão.

---

## 📝 3. Padronização de Commits (Conventional Commits em PT-BR)

Todas as mensagens de commit **DEVEM OBRIGATORIAMENTE ser escritas em Português do Brasil**, no formato:

```text
<tipo>(<escopo>): <descrição sucinta e clara>
```

### 🏷️ 3.1. Tipos Permitidos:
- **`feat`**: Nova funcionalidade entregue ao sistema ou usuário.
- **`fix`**: Correção de bug ou falha de comportamento.
- **`docs`**: Adição ou alteração exclusiva de documentações.
- **`style`**: Ajustes visuais, formatação ou indentação sem alteração de lógica.
- **`refactor`**: Refatoração de código sem alteração de regra de negócio ou funcionalidade.
- **`perf`**: Alteração focada em ganho de performance e otimização.
- **`test`**: Criação ou atualização de testes automatizados.
- **`sec`**: Melhorias de segurança, sanitização de inputs ou proteção contra vulnerabilidades.
- **`chore`**: Tarefas de infraestrutura interna, dependências, scripts de build ou configs.

### 🎯 3.2. Escopos Recomendados:
- `(ui)`: Componentes visuais, layouts, CSS e telas.
- `(auth)`: Autenticação, autorização, tokens e sessões.
- `(api)`: Rotas, endpoints, clientes HTTP e integrações.
- `(db)`: Banco de dados, JSON schemas, D1, Supabase ou persistência.
- `(audio)`: Efeitos sonoros, sintetizadores procedurais e músicas.
- `(game)`: Mecânicas de jogo, regras, Godot ou física.
- `(pwa)`: Service workers, manifest, offline cache e instalação.
- `(deps)`: Instalação ou atualização de pacotes e dependências.
- `(config)`: Configurações de ambiente, tsconfig, wrangler, etc.
- `(core)`: Lógica central, state management e utilitários base.

### 📌 Exemplos de Commits Válidos:
```bash
git commit -m "feat(auth): implementa fluxo de login com persistência de sessão"
git commit -m "fix(audio): corrige delay na reprodução do sintetizador procedural"
git commit -m "docs(infra): atualiza matriz de decisão de hospedagem e banco de dados"
git commit -m "refactor(ui): simplifica renderização de componentes com ícones SVG"
```

---

## ⏰ 4. Frequência de Commits

- **Commit Obrigatório por Subfase/Ciclo:** A cada ciclo modular concluído, testado e validado, realiza-se o commit e push para o GitHub.
- **Micro-commits:** Permitidos durante o ciclo para salvar etapas lógicas intermediárias estáveis.

---

## 🛡️ 5. Checklist de Pré-Commit e Auditoria de Segurança

Antes de executar qualquer `git commit` ou `git push`, a IA e o desenvolvedor devem auditar as mudanças:

```bash
# Comandos obrigatórios de auditoria prévia:
git status
git diff --staged
```

### 🚫 Itens Estritamente Proibidos no Commit:
1. **Segredos e Credenciais:** Nunca commitar arquivos `.env`, chaves de API, senhas ou tokens privados.
2. **Logs Temporários:** Nunca commitar `console.log`, `print` ou instruções temporárias de depuração.
3. **Mocks Residuais:** Garantir que nenhum dado fictício de teste vá para a branch `main`.
4. **Binários Pesados Inadequados:** Nunca versionar arquivos `.apk`, caches locais (`node_modules/`, `.dart_tool/`, `.build/`) ou mídias não otimizadas.

