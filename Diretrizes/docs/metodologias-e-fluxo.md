# Metodologia & Fluxo de Trabalho de Desenvolvimento (End-to-End Workflow)

Este documento define o ciclo de vida completo, rigoroso e auditável de desenvolvimento de softwares no nosso ecossistema. Ele orienta tanto os desenvolvedores humanos quanto os assistentes de IA (Antigravity IDE / Gemini) em todas as fases do projeto.

---

## 📌 1. Concepção, Ideação & Especificação (A Tríade de Documentos)

Todo projeto inicia-se com uma fase de alinhamento e alinhamento de visão (humano + IA ou equipe). O resultado dessa concepção é obrigatoriamente registrado na raiz do projeto em 3 arquivos Markdown específicos:

1. **`idealizacao.md` (Conceito Humano / GDD):**
   - Texto fluido e narrativo resumindo a ideia bruta, o propósito do projeto, visão geral das telas e objetivos centrais. Voltado para leitura humana intuitiva.
2. **`planejamento.md` (Faseamento Estruturado):**
   - Estruturação em tópicos e listas divididos por **Fases rígidas e bem definidas**.
   - Ao final da lista de tópicos, contém um parágrafo explicativo detalhado para cada item.
3. **`funcoes.md` (Especificação Técnica Detalhada):**
   - Documento estritamente técnico descrevendo a definição de cada função, bloco de código, arquivos, pastas, tipos de arquivo, APIs consumidas, serviços de hospedagem, dispositivos alvo, público-alvo e interações do sistema.

---

## 🏗️ 2. Arquitetura Física & Regra Viva de Pastas

Após a definição da tríade de especificação, cria-se a estrutura física de diretórios acompanhada do arquivo de governança:

- **`estrutura-pastas.md` (Arquivo de Governança de Arquitetura):**
  - Salvo na raiz do projeto, descreve a finalidade exata de cada pasta e subpasta.
  - **REGRA VIVA & RESTRITIVA:** Este documento é uma diretriz mandatória. A IA e os desenvolvedores DEVEM consultá-lo antes de criar qualquer novo arquivo ou subdiretório. Nenhuma alteração de estrutura é permitida sem estar formalmente alinhada a este documento.

---

## 🌿 3. Estratégia de Git, Branching, Segredos & Ambientes

### 🌿 Estratégia de Branching

- **Branch por Fase:** Cada FASE principal do projeto é desenvolvida em uma branch Git própria (ex: `fase-1`, `fase-2`).
- **Branch por Subfase Complexa:** Subfases densas ou de alta complexidade podem ter branches derivadas da branch da fase atual (ex: `fase-1/subfase-modulo-auth`).

### 📦 Commits & Deploys

- **Commit Obrigatório por Ciclo:** A cada ciclo (subfase) concluído e validado, é obrigatório realizar o commit e push para o GitHub.
- **Deploy Obrigatório por Fase:** Ao concluir uma FASE inteira, é obrigatório realizar um deploy de staging/preview ou gerar um build de teste (ex: `.apk` para mobile) para testes de usuários finais.

### 🔐 Gestão de `.env` e CI/CD

- Variáveis de ambiente sensíveis devem seguir as melhores práticas: cadastradas no **GitHub Secrets** e automatizadas via **GitHub Actions**.
- Schemas e migrações de banco de dados seguem o mesmo versionamento e rigor do código-fonte.

### 🧹 Sanitização Pré-Deploy (Remoção de Mocks)

- **REGRA OBRIGATÓRIA:** Antes de mesclar/commitar código na branch `main` ou realizar um deploy de produção, TODOS os dados mockados e de testes locais devem ser obrigatoriamente removidos, garantindo o ambiente 100% limpo para produção.

---

## 🔄 4. Execução por Ciclos & Desenvolvimento de Módulos Completos

O desenvolvimento prático ocorre em ciclos de "Módulos Completos":

1. **Arquivo de Tarefas (`tarefas.md`):** Criado no início de cada ciclo detalhando todas as funções e componentes que serão construídos naquela subfase.
2. **Desenvolvimento Modular Integro:** A IA desenvolve o módulo completo definido no ciclo.
3. **Imutabilidade de Módulos Concluídos:** Módulos concluídos anteriormente **NÃO devem ser alterados**, a menos que haja um erro comprovado ou falha grave de planejamento identificado formalmente.

---

## 🛡️ 5. Testes e Pentesting em 3 Camadas

Nenhum módulo é dado como concluído sem passar pelo protocolo de testes em 3 camadas:

- **Camada 1 (Testes Locais do Módulo):**
  - Verificação de sintaxe, erros de lógica e sanitização de dados.
  - Segurança de Input/Output: filtragem de fontes e tipos, proteção contra IDOR, XSS, SQL Injection e vazamentos.
- **Camada 2 (Testes de Integração Direta):**
  - Repetição da Camada 1 em todas as funções, arquivos e módulos que possuem conexão direta (entrada/saída de dados de via única ou dupla) com o módulo recém-criado.
- **Camada 3 (Varredura Sistêmica Crítica):**
  - Acionada em caso de detecção de falhas massivas ou riscos de arquitetura/segurança. Realiza varredura e testes em 100% das funções e arquivos do projeto para evitar regressões cascata.

---

## 📜 6. Registro de Histórico de Ciclo (Logs de Subfase)

Após a aprovação nos testes, a IA deve registrar o histórico da subfase em um arquivo Markdown em pasta organizada dedicada (`docs/historico-ciclos/`):

- **Conteúdo do Log do Ciclo:**
  - O que foi implementado, como foi implementado e por que foi implementado dessa forma.
  - Log de erros encontrados e como foram superados.
  - Alertas e pontos de atenção para evitar erros futuros ou monitorar comportamentos de borda.
  - Contexto de encadeamento: Fase/subfase atual, subfase imediatamente anterior e próxima subfase planejada.

---

## 🚨 7. Protocolo de Erros, Refatoração & Alteração Legada

Se for inevitável alterar um módulo anterior (devido a bug ou erro de planejamento):

1. **Destaque Visível no Planejamento:** Marcar visivelmente nos arquivos de planejamento que o módulo foi refatorado (sem apagar o histórico anterior).
2. **Arquivo de Incidente/Refatoração:** Criar um arquivo `.md` em pasta dedicada (`docs/incidencias-e-refatoracoes/`) detalhando o erro minunciosamente, possíveis soluções e o impacto no projeto.
3. **Debate de Custo Operacional:** Discussão estruturada (Humano + IA) para avaliar a melhor solução e seu "custo operacional" (esforço e volume de código afetado).
4. **Atualização da Solução Decidida:** Atualizar o documento do erro com a solução final detalhada.
5. **Execução & Re-teste:** Executar a correção, acionar os testes de Camada 3 e gerar o log de ciclo padrão.

---

## 🚀 8. Validação Exaustiva Final & Documentação de Estado Final

Quando todas as fases do projeto forem concluídas:

1. **Testes Exaustivos com Agentes de IA:**
   - Agentes de IA simulam usuários finais realizando testes de uso exaustivos e pentest completo para identificar quaisquer falhas residuais.
2. **Zero Erros Pendentes:**
   - Com 100% dos testes aprovados, gera-se a **Documentação Final do Projeto** em uma pasta dedicada na raiz (`documentacao-final/`):
     - `README.md` (Consolidado e atualizado)
     - `API_DOCS.md` (Documentação completa de endpoints e contratos)
     - `MANUAL_USUARIO.md` (Guia de uso final)
     - `ARQUITETURA_FINAL.md` (Visão geral da solução em produção)
     - `CHANGELOG.md` (Histórico completo de releases)
