# Football Predict API

<div align="center">
  <img src="logo-java.png" alt="Java10x Logo" width="300"/>
  
  <p>
    <strong>Desenvolvido como parte do curso Java10x</strong><br>
    <a href="https://java10x.dev">java10x.dev</a>
  </p>
</div>

---

## 📋 Sobre o Projeto

Aplicação REST desenvolvida em **Java 17** com **Spring Boot 3.x** que consome a API do [Football Data](https://www.football-data.org/) para fornecer informações sobre partidas de futebol.

## 🚀 Tecnologias

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring WebFlux** (WebClient)
- **Maven**
- **dotenv-java** (gerenciamento de variáveis de ambiente)

## 📦 Estrutura do Projeto

```
FootballPredict/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── dev/java10x/FootballPredict/
│   │   │       ├── config/
│   │   │       │   └── WebClientConfig.java
│   │   │       ├── client/
│   │   │       │   └── FootballDataClient.java
│   │   │       ├── service/
│   │   │       │   └── MatchService.java
│   │   │       ├── controller/
│   │   │       │   └── MatchController.java
│   │   │       └── FootballPredictApplication.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
├── pom.xml
├── .env
├── .env.example
└── README.md
```

## 🏗️ Arquitetura

A aplicação segue uma arquitetura em camadas:

- **Controller**: Recebe requisições HTTP e retorna respostas
- **Service**: Contém a lógica de negócio
- **Client**: Responsável pela comunicação com a API externa
- **Config**: Configurações da aplicação (WebClient, beans)

## ⚙️ Configuração

### Pré-requisitos

- Java 17 ou superior
- Maven 3.6+ (ou use o wrapper `mvnw`)

### Variáveis de Ambiente

1. Copie o arquivo `.env.example` para `.env`:
   ```bash
   cp .env.example .env
   ```

2. Edite o arquivo `.env` e adicione sua chave da API:
   ```
   FOOTBALL_DATA_API_KEY=sua_chave_aqui
   ```

   > **Nota**: Para obter uma chave da API, registre-se em [football-data.org](https://www.football-data.org/)

## 🏃 Como Executar

### Opção 1: Maven Wrapper (Recomendado)
```bash
./mvnw spring-boot:run
```

### Opção 2: Maven
```bash
mvn spring-boot:run
```

### Opção 3: JAR Executável
```bash
mvn clean package
java -jar target/FootballPredict-0.0.1-SNAPSHOT.jar
```

A aplicação estará disponível em: `http://localhost:8080`

## 📡 Endpoints

### GET /matches

Retorna as partidas de futebol da API externa.

**Exemplo de requisição:**
```bash
curl http://localhost:8080/matches
```

**Resposta:**
```json
{
  "filters": {
    "dateFrom": "2025-12-14",
    "dateTo": "2025-12-15",
    "permission": "TIER_ONE"
  },
  "resultSet": {
    "count": 30,
    "competitions": "DED,PD,SA,PL,FL1,BL1,PPL",
    "first": "2025-12-14",
    "last": "2025-12-14",
    "played": 12
  },
  "matches": [...]
}
```

## 🔒 Segurança

- A chave da API é lida de variáveis de ambiente (arquivo `.env`)
- O arquivo `.env` está no `.gitignore` para não ser versionado
- Nunca commite credenciais no repositório

## 📝 Licença

Este projeto foi desenvolvido como parte do curso **Java10x**.

---

<div align="center">
  <p>
    <strong>Java10x - Seu último curso de programação</strong><br>
    <a href="https://java10x.dev">java10x.dev</a>
  </p>
</div>

