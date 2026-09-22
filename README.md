# Gestão Municipal de Servidores

Aplicação web para **gerenciamento de servidores públicos e secretarias**, desenvolvida como solução para um desafio técnico.

O projeto é composto por uma API REST desenvolvida com **Java e Spring Boot** e uma aplicação web desenvolvida com **Angular**, utilizando **PostgreSQL** para persistência dos dados.

---

## 🛠️ Tecnologias

### Backend

* Java 21
* Spring Boot 4
* Spring Web
* Spring Data JPA
* Bean Validation
* PostgreSQL
* Flyway
* Maven
* Docker
* Swagger / OpenAPI
* JUnit
* Mockito

### Frontend

* Angular 18
* TypeScript
* Reactive Forms
* Angular Router
* HttpClient
* HTML5
* SCSS

---

## 📁 Estrutura do projeto

```text
servidores/

├── servidores-api/       # API REST
│   ├── src/
│   ├── pom.xml
│   └── docker-compose.yml
│
├── servidores-web/      # Aplicação Angular
│   ├── src/
│   ├── package.json
│   └── angular.json
│
└── README.md
```

Frontend, backend e banco de dados foram mantidos separados para facilitar a execução e permitir que os serviços sejam futuramente publicados de forma independente.

---

## ✨ Funcionalidades

### 👤 Servidores

* Cadastro de servidores
* Consulta de servidores ativos
* Atualização de dados
* Exclusão lógica
* Validação de nome e e-mail
* Validação da data de nascimento
* Validação de idade entre 18 e 75 anos
* Associação com secretaria
* Transferência entre secretarias
* Desligamento
* Reativação
* Histórico de movimentações

### 🏢 Secretarias

* Cadastro de secretarias
* Consulta de secretarias ativas
* Atualização
* Exclusão lógica
* Validação de sigla única

---

## 🏗️ Arquitetura

O backend segue uma arquitetura simples baseada em camadas:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL
```

A aplicação utiliza:

* DTOs para entrada e saída de dados
* Bean Validation para validação das requisições
* Tratamento global de exceções
* Spring Data JPA para persistência
* Flyway para versionamento do banco de dados
* Transações para operações que envolvem regras de negócio
* CORS configurável por variável de ambiente

---

## 🗄️ Banco de dados

O projeto utiliza **PostgreSQL 16**, executado localmente através do Docker Compose.

### Subindo o banco

Na pasta `servidores-api`, execute:

```bash
docker compose up -d
```

O PostgreSQL será disponibilizado na porta `5432`.

As credenciais do banco são configuradas através de variáveis de ambiente.

---

## ⚙️ Configuração do Backend

O backend utiliza as seguintes variáveis de ambiente:

| Variável               | Descrição                       |
| ---------------------- | ------------------------------- |
| `DATABASE_URL`         | URL de conexão com o PostgreSQL |
| `DATABASE_USERNAME`    | Usuário do banco                |
| `DATABASE_PASSWORD`    | Senha do banco                  |
| `PORT`                 | Porta da API                    |
| `CORS_ALLOWED_ORIGINS` | Origens permitidas pelo CORS    |

### Exemplo para execução local

No PowerShell:

```powershell
$env:DATABASE_URL="jdbc:postgresql://localhost:5432/servidores_db"
$env:DATABASE_USERNAME="servidores"
$env:DATABASE_PASSWORD="servidores"
$env:PORT="8080"
$env:CORS_ALLOWED_ORIGINS="http://localhost:4200"
```

Depois, na pasta `servidores-api`:

```powershell
.\mvnw.cmd spring-boot:run
```

A API estará disponível em:

`http://localhost:8080`

---

## 📚 Swagger

Com o backend em execução, a documentação interativa da API pode ser acessada em:

`http://localhost:8080/swagger-ui/index.html`

---

## 🔌 Principais endpoints

### Servidores

| Método   | Endpoint           | Descrição               |
| -------- | ------------------ | ----------------------- |
| `GET`    | `/servidores`      | Lista servidores ativos |
| `GET`    | `/servidores/{id}` | Consulta um servidor    |
| `POST`   | `/servidores`      | Cadastra um servidor    |
| `PUT`    | `/servidores/{id}` | Atualiza um servidor    |
| `DELETE` | `/servidores/{id}` | Desativa um servidor    |

### Movimentações

| Método | Endpoint                         | Descrição            |
| ------ | -------------------------------- | -------------------- |
| `GET`  | `/servidores/{id}/historico`     | Consulta o histórico |
| `PUT`  | `/servidores/{id}/transferencia` | Transfere o servidor |
| `PUT`  | `/servidores/{id}/desligamento`  | Desliga o servidor   |
| `PUT`  | `/servidores/{id}/reativacao`    | Reativa o servidor   |

### Secretarias

| Método   | Endpoint            | Descrição                |
| -------- | ------------------- | ------------------------ |
| `GET`    | `/secretarias`      | Lista secretarias ativas |
| `POST`   | `/secretarias`      | Cadastra uma secretaria  |
| `PUT`    | `/secretarias/{id}` | Atualiza uma secretaria  |
| `DELETE` | `/secretarias/{id}` | Desativa uma secretaria  |

---

## 🧪 Testes

Os testes do backend utilizam **JUnit e Mockito**.

Para executar:

```powershell
cd servidores-api
.\mvnw.cmd test
```

---

## 💻 Executando o Frontend

Na pasta `servidores-web`, instale as dependências:

```bash
npm install
```

Depois execute:

```bash
npm start
```

A aplicação estará disponível em:

`http://localhost:4200`

---

## 🖥️ Funcionalidades do Frontend

### Servidores

* Formulário de cadastro e edição
* Validação dos campos
* Seleção de secretaria
* Listagem de servidores
* Edição
* Exclusão
* Visualização do histórico
* Mensagens de sucesso e erro

### Secretarias

* Formulário de cadastro e edição
* Validação dos campos
* Listagem de secretarias
* Edição
* Exclusão
* Mensagens de sucesso e erro

---

## 🔐 Regras de negócio

O sistema utiliza **exclusão lógica**, preservando os registros no banco de dados.

As movimentações dos servidores são registradas em histórico, contemplando:

* Admissão
* Transferência
* Desligamento
* Reativação

Também são aplicadas regras para:

* E-mail válido e único
* Sigla de secretaria única
* Servidor vinculado a uma secretaria ativa
* Idade entre 18 e 75 anos
* Data de nascimento não futura

---

## 🌐 CORS

O CORS é configurado globalmente no backend.

A origem permitida pode ser definida através da variável `CORS_ALLOWED_ORIGINS`.

Para desenvolvimento local:

`http://localhost:4200`

---

## 🚀 Execução completa

Para executar o projeto localmente:

### 1. Banco de dados

```bash
cd servidores-api
docker compose up -d
```

### 2. Backend

Configure as variáveis de ambiente e execute:

```powershell
.\mvnw.cmd spring-boot:run
```

### 3. Frontend

Em outro terminal:

```bash
cd servidores-web
npm install
npm start
```

Acesse:

`http://localhost:4200`

---

## 📌 Observações

O projeto foi estruturado para permitir a execução independente do frontend, backend e banco de dados.

Em um ambiente de produção, os serviços podem ser hospedados separadamente, utilizando uma API Spring Boot, um frontend Angular e um banco PostgreSQL gerenciado.

---

## 👨‍💻 Autor

**Avilan Renato Porto**

Desenvolvedor Java / Backend

Tecnologias principais:

**Java · Spring Boot · Angular · PostgreSQL · Docker · AWS**
