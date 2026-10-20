# Guia Oficial de Engenharia de Software & Estilo de Código (Code Style)

Este documento estabelece os padrões rigorosos de engenharia de software, anatomia de funções, desacoplamento de módulos e convenções sintáticas para todas as linguagens e projetos do nosso ecossistema. Deve ser seguido estritamente por desenvolvedores e assistentes de IA (Antigravity IDE / Gemini).

---

## 🏛️ 1. Princípios Fundamentais de Engenharia de Software

1. **Responsabilidade Única (SRP):** Cada módulo, arquivo, classe e função deve ter uma única e bem definida razão para mudar.
2. **Inversão de Dependência (DIP):** Módulos de alto nível não devem depender de módulos de baixo nível; ambos devem depender de abstrações/interfaces.
3. **Imutabilidade e Funções Puras:** Sempre que possível, funções não devem mutar os objetos recebidos como argumento. Retorne novas estruturas de dados com as modificações necessárias.
4. **Validação de Fronteira (Fail-Fast):** Valide os dados na borda de entrada de cada módulo (parâmetros, payloads externos, inputs de usuário). Se algo estiver inválido, falhe imediatamente antes de executar qualquer lógica de negócio.
5. **Legibilidade sobre Esperteza:** Escreva código simples, explícito e autodocumentado. Evite atalhos sintáticos obscuros ou "one-liners" complexos que dificultem a manutenção.

---

## 🧩 2. Anatomia e Design de Funções e Métodos

### 🚪 2.1. Guard Clauses (Early Returns)
É proibido aninhar múltiplos blocos de `if/else` profundos. Trate as condições de erro e casos de borda imediatamente no início da função e retorne:

```typescript
// ❌ INCORRETO (Aninhamento profundo)
function processarPedido(pedido: Pedido) {
  if (pedido.itens.length > 0) {
    if (pedido.clienteValido) {
      if (pedido.pagamentoAprovado) {
        // Lógica principal perdida no aninhamento...
      }
    }
  }
}

// ✅ CORRETO (Guard Clauses com Early Return)
function processarPedido(pedido: Pedido): ResultadoProcessamento {
  if (pedido.itens.length === 0) {
    return { sucesso: false, erro: 'Pedido sem itens' };
  }
  if (!pedido.clienteValido) {
    return { sucesso: false, erro: 'Cliente inválido' };
  }
  if (!pedido.pagamentoAprovado) {
    return { sucesso: false, erro: 'Pagamento recusado' };
  }

  // Caminho feliz executado linearmente e com clareza
  return finalizarProcessamento(pedido);
}
```

### 📦 2.2. Assinaturas de Funções e DTOs
- Funções devem receber no **máximo 2 a 3 argumentos posicionais**.
- Caso a função precise de mais dados, agrupe-os em um **Objeto de Parâmetros / DTO tipado**:

```typescript
// ❌ INCORRETO (Muitos parâmetros soltos)
function criarUsuario(nome: string, email: string, idade: number, perfil: string, ativo: boolean) {}

// ✅ CORRETO (DTO / Objeto tipado)
interface CriarUsuarioDTO {
  nome: string;
  email: string;
  idade: number;
  perfil: PerfilUsuario;
  ativo?: boolean;
}

function criarUsuario(dados: CriarUsuarioDTO): Usuario {}
```

---

## 🔗 3. Arquitetura Modular e Comunicação entre Arquivos

1. **Desacoplamento por Contratos (Interfaces):**
   - Módulos se comunicam através de tipos/interfaces públicas. A implementação interna de um serviço nunca deve vazar detalhes para quem o consome.
2. **Proibição de Dependências Circulares:**
   - O arquivo `A` nunca deve importar `B` se `B` já importa `A` (direta ou indiretamente). Se houver necessidade de tipos compartilhados, extraia-os para um terceiro arquivo isolado (ex: `types/`).
3. **Ponto de Entrada Único (Barrel Files):**
   - Diretórios de módulos/componentes devem possuir um arquivo de exportação (ex: `index.ts`) que expõe apenas os símbolos públicos autorizados, ocultando a complexidade interna dos submódulos.
4. **Isolamento Estrito de Camadas:**
   - **Camada de UI:** Nunca faz chamadas de rede ou banco diretamente; consome a camada de serviços/hooks.
   - **Camada de Domínio/Serviços:** Não possui dependência de elementos de UI ou frameworks visuais.

---

## 📜 4. Padrões de Nomenclatura e Sintaxe por Linguagem

### 🔷 4.1. TypeScript / JavaScript
- **Arquivos e Pastas:** `kebab-case.ts` (ex: `auth-service.ts`, `user-card.tsx`).
- **Classes, Interfaces, Types e Componentes:** `PascalCase` (ex: `UserProfile`, `PaymentGateway`).
- **Funções, Métodos e Variáveis:** `camelCase` (ex: `calcularTotal()`, `taxaJuros`).
- **Constantes Globais:** `UPPER_SNAKE_CASE` (ex: `MAX_RETRY_ATTEMPTS`).
- **Tipagem:** `strict: true`. **Proibido o uso de `any`** (use `unknown` com narrowing ou interfaces específicas).

### 🎯 4.2. Dart / Flutter
- **Arquivos e Pastas:** `snake_case.dart` (ex: `home_screen.dart`, `auth_repository.dart`).
- **Classes, Widgets, Mixins e Enums:** `PascalCase` (ex: `CustomButton`, `AppState`).
- **Métodos, Variáveis e Parâmetros:** `camelCase` (ex: `fetchUserData()`, `userEmail`).
- **Privados:** Prefixo sublinhado `_` (ex: `_privateMethod()`, `_internalState`).
- **Construtores `const`:** Obrigatório o uso de `const` em todos os Widgets e instâncias imutáveis.

### 🐍 4.3. Python (PEP 8)
- **Arquivos, Módulos, Funções e Variáveis:** `snake_case.py` (ex: `data_cleaner.py`, `generate_report()`).
- **Classes:** `PascalCase` (ex: `PipelineExecutor`).
- **Constantes:** `UPPER_SNAKE_CASE` (ex: `DATABASE_URL`).
- **Tipagem:** Uso obrigatório de *Type Hints* em funções públicas: `def calcular(valor: float) -> dict:`.

### 🐹 4.4. Go (Golang)
- **Arquivos:** `snake_case.go` (ex: `server_handler.go`).
- **Exportados (Públicos):** `PascalCase` (ex: `NewServer()`, `UserResponse`).
- **Não-Exportados (Privados):** `camelCase` (ex: `validateToken()`, `dbConnection`).
- **Tratamento de Erros:** Erros tratados explicitamente no fluxo imediato (`if err != nil { return nil, err }`).

### ☕ 4.5. Java & C# (.NET)
- **Arquivos e Classes:** `PascalCase` (ex: `OrderProcessor.java`, `InvoiceService.cs`).
- **Interfaces:** `PascalCase` (em C#, prefixo `I` obrigatório: `IOrderService`).
- **Métodos:** `camelCase` em Java (`processOrder()`); `PascalCase` em C# (`ProcessOrder()`).
- **Variáveis e Parâmetros:** `camelCase`.
- **Campos Privados (C#):** `_camelCase` (ex: `_logger`, `_userRepository`).

### ⚙️ 4.6. C & C++
- **Arquivos:** `snake_case.c` / `snake_case.cpp` / `.h` / `.hpp`.
- **Funções e Variáveis (C):** `snake_case` (ex: `init_buffer()`).
- **Classes e Structs (C++):** `PascalCase` (ex: `NetworkSocket`).
- **Gestão de Memória (C++):** Obrigatório uso de RAII e smart pointers (`std::unique_ptr`, `std::shared_ptr`) em vez de alocações manuais com `new/delete`.

### 🎮 4.7. GDScript (Godot Engine)
- **Arquivos e Cenas:** `snake_case.gd`, `snake_case.tscn`.
- **Classes Nomeadas (`class_name`):** `PascalCase` (ex: `class_name PlayerCharacter`).
- **Funções, Variáveis e Sinais:** `snake_case` (ex: `signal health_changed(new_health)`).
- **Tipagem Estática:** Tipagem explícita em variáveis e retornos (`var speed: float = 200.0`, `func get_target() -> Node2D:`).

### 📋 4.8. JSON / YAML / Arquivos de Configuração
- **Chaves:** `camelCase` ou `snake_case` uniforme em todo o projeto.
- **Formatação:** Indentação estrita de **2 espaços**, sem tabulações, com estrutura declarativa clara.

---

## 💬 5. Diretrizes de Documentação no Código

1. **Idioma Oficial:** Qualquer comentário ou docstring no código deve ser escrito em **Português do Brasil**.
2. **Minimização de Comentários:** O código deve ser tão limpo que dispense comentários óbvios. Comentários redundantes que apenas repetem o que o código faz são proibidos.
3. **Quando Comentar:**
   - Para explicar **"POR QUE"** uma decisão não-óbvia ou contorno de regra foi tomado;
   - Para documentar regras matemáticas ou lógicas de negócio críticas;
   - Em contratos de bibliotecas ou APIs públicas (JSDoc, DartDoc, Docstrings).

