# 🎵 Vinyl Collection App
Aplicativo Android desenvolvido em **Java** para gerenciamento de uma coleção pessoal de discos de vinil.  
O app permite cadastrar, editar, listar e organizar discos e artistas, armazenando informações detalhadas como ano de lançamento, gênero, condição do disco, velocidade, posse do vinil e data de aquisição.

---

## Funcionalidades

### 🎧 Gerenciamento de Discos
- Cadastro de novos discos
- Edição de discos existentes
- Exclusão de discos
- Listagem de todos os discos cadastrados
- Ordenação crescente ou decrescente (configurável)
- Validação completa dos dados inseridos

### Gerenciamento de Artistas
- Cadastro de artistas
- Listagem de artistas
- Associação de cada disco a um artista existente
- Exibição do nome do artista na listagem de discos

### Data de Aquisição
- Campo opcional, habilitado apenas se o usuário marcar que já possui o vinil
- Seleção da data via **DatePickerDialog**
- Data limitada até o dia atual
- Exibição formatada usando `LocalDate`
- Campo logicamente ignorado quando não aplicável

### Preferências do Usuário
- Ordenação da lista (crescente ou decrescente)
- Sugestão automática da última condição usada
- Restauração das configurações padrão
- Preferências persistidas com `SharedPreferences`

### Navegação
- Menu de opções com acesso rápido para:
  - Adicionar disco
  - Listar artistas
  - Cadastrar artista
  - Tela "Sobre"
- Navegação clara entre telas usando `Intent`

---

## 🗂️ Estrutura do Projeto

### Activities principais
- `ListDiscsActivity`  
  Tela principal de listagem dos discos.
- `NewVinylActivity`  
  Cadastro e edição de discos.
- `ListArtistsActivity`  
  Listagem de artistas cadastrados.
- `NewArtistActivity`  
  Cadastro de artistas.
- `AboutActivity`  
  Informações sobre o aplicativo.

### Adapters
- `DiscAdapter`  
  Responsável por renderizar cada item da lista de discos.
- Adapter de artistas utilizando `ArrayAdapter`

### Banco de Dados
- Utiliza **Room (SQLite)** para persistência local
- DAOs separados para `Disc` e `Artist`
- Relacionamento entre disco e artista via ID

---

## Validações Implementadas

- Nome do disco obrigatório
- Ano de lançamento:
  - Obrigatório
  - Numérico
  - Máximo de 4 dígitos
  - Intervalo válido (1800 até ano atual)
- Gênero obrigatório
- Velocidade do disco obrigatória
- Data de aquisição:
  - Obrigatória apenas se o usuário marcar que já possui o vinil
  - Não pode ser futura

---

## Tecnologias Utilizadas

- **Java**
- **Android SDK**
- **Room (SQLite)**
- **LocalDate (`java.time`)**
- **SharedPreferences**
- **Material Components**

---

## Interface
- Layouts em XML
- Uso de `ListView`, `Spinner`, `RadioGroup`, `CheckBox`
- Feedback visual com Snackbar
- Suporte a modo de edição e criação
- Interface responsiva com Edge-to-Edge

---

## 🚀 Possíveis Evoluções Futuras
- Filtro por artista ou gênero
- Pesquisa por nome do disco
- Exportação da coleção
- Inclusão de imagens dos discos
- Sincronização em nuvem (Firebase)
