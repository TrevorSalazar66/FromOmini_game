# Template Mobile Oficial (Flutter & Dart)

Blueprint base para desenvolvimento de aplicações móveis de alta performance, minimalistas e acessíveis em Flutter. Projetado com **Clean Architecture em 4 Camadas** e suporte flexível para três modos de operação: **100% Offline**, **Híbrido** e **100% Online**.

---

## 🏛️ 1. Arquitetura em 4 Camadas Limpas

A estrutura de código em `lib/` segue rigorosamente o padrão de separação de responsabilidades:

```text
lib/
├── presentation/                  # 1. Camada de Apresentação (UI)
│   ├── screens/                   # Telas da aplicação (ex: home_screen.dart)
│   └── widgets/                   # Componentes visuais puros e reutilizáveis
├── controllers/                   # 2. Camada de Controle & Estado
│   └── home_controller.dart       # Gerenciamento de estado (ValueNotifier / ChangeNotifier)
├── domain/                        # 3. Camada de Domínio & Regras de Negócio
│   ├── entities/                  # Entidades puras do sistema (ex: user.dart)
│   ├── repositories/              # Interfaces abstratas de repositório (ex: user_repository.dart)
│   └── usecases/                  # 1 Caso de Uso por arquivo (ex: get_user_profile.dart)
├── data/                          # 4. Camada de Dados & Persistência
│   ├── datasources/
│   │   ├── local/                 # Acesso a arquivos .json locais (JsonFileDataSource)
│   │   └── remote/                # Chamadas HTTP/API para backend externo
│   ├── models/                    # Modelos com serialização JSON (fromJson / toJson)
│   └── repositories/              # Implementações concretas dos repositórios
├── core/                          # Utilitários compartilhados, temas e constantes
│   ├── theme/                     # Definições de cores, tipografia e temas globais
│   └── constants/                 # Constantes e rotas do app
└── main.dart                      # Ponto de entrada e injeção de dependências
```

---

## 🔄 2. Modos de Operação Suportados

### 📴 Modo 1: 100% Offline (Local-First com `.json`)
- **Funcionamento:** O app não realiza chamadas de rede. Todos os dados são serializados e salvos diretamente no armazenamento do dispositivo em arquivos `.json` via `path_provider`.
- **Ideal para:** Utilitários pessoais, calculadoras, geradores de fichas e ferramentas isoladas.

### 🔄 Modo 2: Híbrido (Local-First + Sincronização em Nuvem)
- **Funcionamento:** Todas as leituras e gravações ocorrem instantaneamente no arquivo `.json` local. Uma camada de sincronização em segundo plano envia as alterações para o backend (Cloudflare D1, Supabase ou Firebase) quando a conexão com a internet estiver ativa.
- **Ideal para:** Aplicativos com uso contínuo que precisam de backup na nuvem sem travar a interface do usuário.

### 🌐 Modo 3: 100% Online (Servidor Autoritativo)
- **Funcionamento:** O app atua como cliente de APIs REST ou WebSockets conectadas a um backend hospedado (Cloudflare Workers, Koyeb ou Supabase).
- **Ideal para:** Ambientes multi-usuário, chat em tempo real e jogos online com validação anti-fraude no servidor.

---

## 🎨 3. Diretrizes Visuais & Padrão SVG-First

1. **Ícones em Vetor (`.svg`):**
   - Utilização do pacote `flutter_svg` para renderização de ícones de `assets/icons/`.
   - Cores vinculadas dinamicamente ao tema (`Theme.of(context).colorScheme.primary`).
2. **Minimalismo e Acessibilidade:**
   - Widgets com construtores `const` para eliminar reconstruções desnecessárias da árvore de renderização.
   - Textos claros com tamanho legível e suporte a contraste visual.

---

## 📦 4. Dependências Base Recomendadas (`pubspec.yaml`)

```yaml
dependencies:
  flutter:
    sdk: flutter
  flutter_svg: ^2.0.10+1      # Renderização de vetores SVG
  path_provider: ^2.1.2       # Localização de diretórios para arquivos .json locais
  http: ^1.2.0                # Cliente HTTP leve para modos Híbrido e Online

flutter:
  uses-material-design: true
  assets:
    - assets/icons/
    - assets/audio/
```

---

## 🧪 5. Workflow de Homologação no PC (BlueStacks)

Para validar o comportamento do app sem necessidade de celular físico conectado:

```bash
# 1. Executar análise estática
flutter analyze

# 2. Gerar o pacote APK de release otimizado
flutter build apk --release

# 3. Localizar o executável gerado:
# build/app/outputs/flutter-apk/app-release.apk

# 4. Homologação no BlueStacks:
# Arraste o arquivo .apk para a janela do BlueStacks no PC para instalar e testar.
```

