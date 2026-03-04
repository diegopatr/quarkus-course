# Aula 03: Arquitetura e Padrões de Software com Quarkus

Este projeto integra o conteúdo da disciplina **ADS 175 - Jakarta EE**. O objetivo é demonstrar a implementação de uma API RESTful utilizando o framework **Quarkus**, enfatizando a aplicação de padrões de projeto e a separação de responsabilidades em camadas arquiteturais.

## 🚀 Tecnologias e Especificações

- **Java 21**: Uso de recursos modernos como Records, Streams e Pattern Matching.
- **Quarkus**: Framework Java para ambientes nativos e microserviços.
- **Jakarta REST (JAX-RS)**: Definição de endpoints e recursos web.
- **Jakarta CDI**: Injeção de dependências e gerenciamento do ciclo de vida de objetos.
- **Jackson**: Processamento e serialização de dados em formato JSON.
- **JUnit 5 & RestAssured**: Frameworks para automação de testes de unidade e integração.

## 🏗️ Arquitetura do Sistema

A aplicação adota uma arquitetura em camadas visando escalabilidade e manutenibilidade (*Separation of Concerns*).

### Camadas e Responsabilidades:

1.  **Resource (Interface de Exposição):**
    - Localização: `br.upf.ads175.resource`
    - Atua na camada de borda, expondo endpoints REST.
    - Gerencia o protocolo HTTP (status codes, URI parameters e query parameters).
    - Utiliza exclusivamente **DTOs** para o tráfego de dados.
    - Delega a execução das regras de negócio para a camada de Service.

2.  **Service (Lógica de Negócio):**
    - Localização: `br.upf.ads175.service`
    - Centraliza as regras de negócio e orquestração do sistema.
    - Executa o mapeamento (*translation*) entre DTOs e Entidades.
    - Utiliza **Java Streams** para processamento e agregação de dados.

3.  **Repository (Persistência):**
    - Localização: `br.upf.ads175.repository`
    - Gerencia a persistência e recuperação de dados.
    - Implementa o padrão *Repository* para isolar a tecnologia de armazenamento.
    - Opera estritamente com **Entidades**.
    - Nesta aula, utiliza-se uma implementação em memória (ArrayList) para fins didáticos.

4.  **Model & DTO (Representação de Dados):**
    - **Models (`br.upf.ads175.model`)**: Entidades que representam o domínio interno.
    - **DTOs (`br.upf.ads175.dto`)**: *Data Transfer Objects*, implementados como **Java Records**, que definem o contrato da API, garantindo o desacoplamento do modelo interno.

## 🔄 Fluxo de Processamento (DTO vs Entidade)

O projeto demonstra a distinção fundamental entre objetos de transferência e entidades de domínio:

-   **Fluxo de Entrada:** Cliente (JSON) → JAX-RS (DTO) → Service (Entidade) → Repository.
-   **Fluxo de Saída:** Repository (Entidade) → Service (DTO) → JAX-RS (JSON) → Cliente.

Esta abordagem simula arquiteturas corporativas reais (ex: JPA/Hibernate), onde o modelo de persistência é preservado e os DTOs controlam a exposição da API.

## 🛠️ Endpoints Disponíveis

A API de produtos está acessível em `/produtos`:

-   `GET /produtos`: Listagem de produtos ativos em ordem alfabética.
-   `GET /produtos/{id}`: Recuperação de um produto por identificador único.
-   `GET /produtos/por-categoria`: Agrupamento de produtos por categoria.
-   `GET /produtos/premium`: Filtragem de produtos com valor superior a R$ 1.000,00.
-   `GET /produtos/estatisticas`: Métricas agregadas por categoria.
-   `GET /produtos/baratos?precoMaximo=500`: Filtragem por limite de preço.
-   `POST /produtos`: Cadastro de novo recurso.
-   `DELETE /produtos/{id}`: Remoção de recurso existente.

## 🧪 Procedimentos de Execução

### Modo de Desenvolvimento:
```bash
./mvnw quarkus:dev
```

### Execução de Testes:
```bash
./mvnw test
```

### Exemplo de Payload (POST):
```json
{
  "nome": "Mouse Vertical Ergonômico",
  "preco": 185.00,
  "ativo": true,
  "categoria": { "nome": "Periféricos" }
}
```
