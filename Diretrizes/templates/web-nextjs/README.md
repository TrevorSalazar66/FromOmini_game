# Template Web & PWA (Next.js / React & TypeScript)

Blueprint oficial para desenvolvimento de aplicações Web modernas, escaláveis, acessíveis e com suporte a PWA (Progressive Web App).

---

## 🏛️ 1. Arquitetura Modular em 4 Camadas

A estrutura segue a Clean Architecture com separação estrita de responsabilidades:

```text
src/
├── app/                           # 1. Rotas e Telas (Next.js App Router)
│   ├── layout.tsx                 # Layout global com providers e temas
│   └── page.tsx                   # Página inicial da aplicação
├── components/                    # 2. Componentes de UI Puros (Dumb Components)
│   └── <nome-componente>/         # Componentes com .tsx, .module.css e index.ts
├── hooks/                         # 3. Controladores e Gestão de Estado
│   └── use_app_state.ts           # Hooks e lógica de estado
├── domain/                        # 4. Casos de Uso & Lógica de Negócio Pura
│   └── usecases/                  # 1 Caso de Uso por arquivo
├── data/                          # 5. Camada de Dados & Repositórios
│   └── repositories/              # Abstração de persistência (LocalStorage, JSON, API)
└── styles/                        # Design Tokens e variáveis globais CSS
```

---

## 📱 2. PWA & Suporte Multi-Dispositivo

- **Instalação Direta:** Suporte a manifesto PWA para instalação como app no celular e desktop.
- **Padrão SVG-First:** Todos os ícones em `.svg` (Lucide Icons).
- **Offline Resilience:** Cache local com fallback para uso offline.
