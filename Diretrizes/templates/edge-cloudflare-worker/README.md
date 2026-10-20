# Template Edge API & Serverless Worker (Hono.js & TypeScript)

Blueprint oficial para desenvolvimento de APIs serverless de alta velocidade, ultra-leves e seguras. Construído sobre **Hono.js** e **TypeScript**, garantindo **portabilidade nativa multi-hosting** para rodar sem atrito no **Cloudflare Workers**, **Vercel Edge**, **Koyeb** e **Render** (Node.js/Docker).

---

## 🏛️ 1. Arquitetura Modular em Camadas

A estrutura segue a Clean Architecture com separação estrita de responsabilidades:

```text
src/
├── routes/                        # 1. Rotas e Endpoints HTTP
│   ├── auth_routes.ts             # Endpoints de autenticação
│   └── data_routes.ts             # Endpoints de leitura e gravação
├── middlewares/                   # 2. Segurança, Autenticação e Erros
│   ├── auth_middleware.ts         # Validação de Token / JWT
│   ├── security_headers.ts        # CORS restritivo e proteção de cabeçalhos
│   └── error_handler.ts           # Tratamento centralizado de exceções (sem vazamento de dados)
├── schemas/                       # 3. Schemas de Validação (Fail-Fast com Zod)
│   └── payload_schemas.ts         # Validação estrita de entrada (ex: Zod)
├── domain/                        # 4. Casos de Uso & Lógica de Negócios
│   └── usecases/                  # 1 Caso de Uso por arquivo (ex: sync_data_usecase.ts)
├── data/                          # 5. Adaptadores de Persistência Multi-Hosting
│   ├── interfaces/                # Contratos abstratos (ex: IDataRepository.ts)
│   └── adapters/
│       ├── cloudflare_d1.ts       # Adaptador para Cloudflare D1 (SQL Edge)
│       ├── cloudflare_kv.ts       # Adaptador para Cloudflare KV (Chave-Valor)
│       ├── node_sqlite.ts         # Adaptador para SQLite local (Koyeb / Render)
│       └── supabase_adapter.ts    # Adaptador para PostgreSQL / Supabase
├── index.ts                       # Ponto de entrada Cloudflare Workers / Vercel Edge
└── node-server.ts                 # Ponto de entrada Node.js para Koyeb / Render
```

---

## 👥 2. Modos de Escopo de Usuários

### 👤 2.1. Modo Usuário Único (Single-User / Ferramentas Pessoais)
- **Autenticação:** Validação de Token estático secreto configurado via variável de ambiente (`API_SECRET_TOKEN`).
- **Persistência:** Gravação direta de arquivos `.json` ou chave-valor via **Cloudflare KV**, **R2** ou **SQLite local**, sem tabelas relacionais de usuários.
- **Vantagem:** Latência quase zero, simplicidade absoluta e custo zero.

### 👥 2.2. Modo Múltiplos Usuários (Multi-User)
- **Autenticação:** Validação de JWT / Tokens de sessão com identificação de `user_id`.
- **Isolamento de Dados:** Consultas ao banco filtradas estritamente pelo ID do usuário autenticado para evitar vulnerabilidades IDOR.
- **Persistência:** Banco relacional estruturado em **Cloudflare D1**, **Supabase (PostgreSQL)** ou **SQLite**.

---

## 🛡️ 3. Segurança e Validação Estrita (Zero-Trust)

1. **Validação Fail-Fast com Zod:**
   - Todo payload de entrada é validado no middleware antes de atingir a lógica de negócio. Entradas fora do padrão recebem resposta `400 Bad Request` imediata para economizar processamento.
2. **CORS e Cabeçalhos:**
   - Origens autorizadas estritas, bloqueando acessos indevidos de outros domínios.
3. **Tratamento Seguro de Exceções:**
   - Erros internos retornam mensagens genéricas e amigáveis ao cliente, sem expor *stack traces* ou detalhes de banco de dados.

---

## 🌐 4. Portabilidade Multi-Hosting

O mesmo código de rotas e regras de negócio roda em múltiplos ambientes de hospedagem:

| Provedor | Tipo de Runtime | Ponto de Entrada | Banco / Armazenamento Suportado |
| :--- | :--- | :--- | :--- |
| **Cloudflare Workers** | Edge Serverless | `src/index.ts` | Cloudflare D1, KV, R2 |
| **Vercel Edge** | Edge Runtime | `src/index.ts` | Vercel KV, Supabase |
| **Koyeb** | Container / Node.js | `src/node-server.ts` | SQLite local, PostgreSQL / Supabase |
| **Render** | Web Service Node.js | `src/node-server.ts` | SQLite persistente, Supabase |

---

## 🚀 5. Comandos de Desenvolvimento e Deploy

### 💻 5.1. Execução Local
```bash
# Executar localmente simulando Cloudflare Workers:
npx wrangler dev

# Executar localmente simulando servidor Node.js (Koyeb/Render):
npm run dev:node
```

### 📦 5.2. Deploys Manuais
```bash
# 1. Deploy para Cloudflare Workers:
npx wrangler deploy

# 2. Deploy para Vercel:
vercel --prod

# 3. Deploy para Koyeb / Render:
# O container Docker executa 'node dist/node-server.js'
```

