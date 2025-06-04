# 📁 Simulador de Sistema de Arquivos com Journaling – Java

## 🧪 Metodologia

Este simulador foi desenvolvido em linguagem Java, utilizando classes e métodos que representam comandos típicos de um sistema de arquivos. Cada funcionalidade é implementada de forma modular, refletindo o comportamento de sistemas reais como EXT4 ou NTFS. As ações são processadas por métodos específicos e os resultados exibidos diretamente no console do usuário.

---

## 📘 Parte 1: Introdução ao Sistema de Arquivos com Journaling

### 🔹 O que é um Sistema de Arquivos?

Um sistema de arquivos é o componente do sistema operacional responsável por organizar, armazenar, recuperar e manipular dados em dispositivos de armazenamento como HDs, SSDs e pendrives. Ele organiza os dados em estruturas hierárquicas (pastas e arquivos) e fornece acesso eficiente e seguro a eles.

### 🔹 O que é Journaling?

**Journaling** é uma técnica utilizada por sistemas de arquivos para garantir a integridade dos dados em caso de falhas, como queda de energia ou travamento. Antes de uma operação ser realmente executada, ela é registrada em um log. Caso o sistema falhe, é possível **reproduzir** ou **reverter** ações com base nesse histórico.

#### Principais tipos de journaling:

- **Write-ahead logging (WAL):** registra a operação no log antes de executá-la de fato.
- **Metadata journaling:** registra apenas alterações de metadados (como nomes e permissões).
- **Log-structured file systems:** organizam todos os dados em formato de log sequencial.

---

## ⚙️ Parte 2: Arquitetura do Simulador

### 🗂️ Estrutura de Dados

O simulador utiliza as seguintes estruturas:

- `DirectoryEntry`: representa um diretório, contendo subdiretórios e arquivos.
- `FileEntry`: representa um arquivo com nome e conteúdo.
- `FileSystemSimulator`: coordena todas as operações (mkdir, touch, ls, rm, etc.).
- `JournalManager`: gerencia o log `journal.log` com as ações realizadas.

Todas as estruturas são mantidas em memória através de uma árvore com raiz `baseRoot`, que é serializada em `base.dat`.

### 🧾 Journaling

- Cada operação (`mkdir`, `touch`, `rm`, etc.) é registrada no arquivo `journal.log` com **data e hora**.
- O log é mantido em modo **append contínuo**, como especificado.
- Mesmo com o uso do `base.dat` para persistência principal, o `journal.log` serve como histórico seguro e rastreável.

---

## 💻 Parte 3: Implementação em Java

### 🔹 `FileSystemSimulator.java`

Contém todos os métodos de comando:
- `mkdir(path)`
- `touch(path, conteúdo)`
- `ls(path)`
- `rm(path)` (com suporte a remoção recursiva)
- `rename(path, novoNome)`
- `copy(origem, destino)`
- `tree()` — mostra a árvore de diretórios
- `showlog()` — exibe o conteúdo do log

### 🔹 `DirectoryEntry.java` e `FileEntry.java`

São as estruturas de dados principais:
- `DirectoryEntry`: possui mapas para subdiretórios e arquivos.
- `FileEntry`: possui nome e conteúdo textual.

Ambas implementam `Serializable`, permitindo serem salvas no `base.dat`.

### 🔹 `JournalManager.java`

- Registra logs com timestamp
- Realiza leitura e exibição do `journal.log`
- Função `close()` fecha o writer com segurança

---

## 📦 Parte 4: Instalação e Funcionamento

### 🔧 Pré-requisitos

- Java JDK 11 ou superior
- Terminal (cmd, bash, etc.)

### ▶️ Como executar o simulador

1. Clique no botão `Run Java` localizado no canto superior esquerdo da tela.

ou

1. Compile todos os arquivos:
```bash
javac *.java
```

2. Execute o programa:
```bash
java Main
```

### ✅ Comandos disponíveis

| Comando                         | Descrição                                                 |
|--------------------------------|------------------------------------------------------------|
| `mkdir <caminho>`              | Cria diretórios em qualquer profundidade                  |
| `touch <caminho> [texto]`      | Cria arquivos com ou sem conteúdo                         |
| `ls <caminho>`                 | Lista diretórios e arquivos                               |
| `rm <caminho>`                 | Remove arquivos ou diretórios (mesmo não vazios)          |
| `rename <caminho> <novo>`      | Renomeia arquivos ou diretórios                           |
| `copy <origem> <destino>`      | Copia arquivos                                             |
| `tree`                         | Exibe a estrutura hierárquica do sistema                  |
| `showlog`                      | Exibe as entradas do `journal.log`                        |
| `exit`                         | Salva e encerra o programa                                |

---

### 💾 Persistência

- O sistema persiste seu estado no arquivo `base.dat` automaticamente após cada operação.
- O arquivo `journal.log` é contínuo, com timestamps, e **não é limpo**.
- Ao reiniciar o programa, o estado anterior é carregado automaticamente a partir do `base.dat`.

---

### 🔗 Link do projeto no GitHub
https://github.com/ricardotemporal/sistema-de-arquivos
