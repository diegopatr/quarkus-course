# Aula 03: Arquitetura de Software e Padrões com Quarkus

Este projeto faz parte do curso de **ADS 175 - Jakarta EE** e tem como objetivo demonstrar a implementação de uma API REST robusta utilizando o framework **Quarkus**, explorando padrões de projeto fundamentais e a separação de responsabilidades em camadas.

## 🚀 Tecnologias Utilizadas

- **Java 21**: Versão moderna do Java com foco em performance e simplicidade (Records, Streams, Pattern Matching).
- **Quarkus**: Framework Java nativo para Kubernetes, otimizado para containers e microserviços.
- **Jakarta REST (JAX-RS)**: Para criação dos endpoints da API.
- **Jakarta CDI**: Para injeção de dependências e gerenciamento de contexto.
- **Jackson**: Para serialização e desserialização de JSON.
- **JUnit 5 & RestAssured**: Para testes automatizados da API.

## 🏗️ Arquitetura do Projeto

A aplicação segue uma arquitetura em camadas bem definida, visando a manutenibilidade, testabilidade e separação de preocupações (*Separation of Concerns*).

### Camadas e Responsabilidades:

1.  **Resource (Camada de API/Web):**
    - Local: `br.upf.ads175.resource`
    - Responsável por expor os endpoints REST.
    - Lida com o protocolo HTTP (status codes, parâmetros de URL, query params).
    - Utiliza exclusivamente **DTOs** para entrada e saída de dados.
    - Delega toda a lógica de negócio para a camada de Service.

2.  **Service (Camada de Negócio):**
    - Local: `br.upf.ads175.service`
    - Contém as regras de negócio da aplicação.
    - Atua como um **mapeador (translator)**: recebe DTOs, converte para Entidades para persistência e vice-versa.
    - Utiliza **Java Streams** para processamento, filtragem e agregação de dados.
    - Orquestra as chamadas ao Repositório.

3.  **Repository (Camada de Dados):**
    - Local: `br.upf.ads175.repository`
    - Responsável pela persistência e recuperação de dados.
    - Nesta aula, utilizamos um **Repositório em Memória** (usando `ArrayList`), simulando o comportamento de um banco de dados real.
    - Trabalha exclusivamente com **Entidades**.
    - Padrão *Repository* isola a tecnologia de persistência do restante do sistema.

4.  **Model & DTO (Modelo de Dados):**
    - **Models (`br.upf.ads175.model`)**: Entidades de domínio que representam os dados internos do sistema (simulando tabelas de um banco de dados).
    - **DTOs (`br.upf.ads175.dto`)**: *Data Transfer Objects*, implementados como **Java Records**, usados para definir o contrato da API com o mundo externo. Garante que mudanças internas no modelo não quebrem a API.

## 🔄 Fluxo de Dados (DTO vs Entidade)

Um dos conceitos centrais desta aula é a distinção entre DTOs e Entidades:

-   **Requisição:** O cliente envia um JSON → O JAX-RS converte para `ProdutoDTO` → O `ProdutoService` converte `ProdutoDTO` para a entidade `Produto` → O `ProdutoRepository` salva a entidade `Produto`.
-   **Resposta:** O `ProdutoRepository` retorna a entidade `Produto` → O `ProdutoService` converte a entidade para `ProdutoDTO` → O JAX-RS serializa o `ProdutoDTO` para JSON e envia ao cliente.

Essa separação simula arquiteturas corporativas reais que utilizam JPA/Hibernate, onde as entidades são protegidas e os DTOs controlam o que é exposto na API.

## 🛠️ Endpoints da API

A API de produtos está disponível em `/produtos` e oferece as seguintes funcionalidades:

-   `GET /produtos`: Lista todos os produtos ativos ordenados por nome.
-   `GET /produtos/{id}`: Busca um produto específico por ID.
-   `GET /produtos/por-categoria`: Agrupa nomes de produtos por categoria.
-   `GET /produtos/premium`: Filtra produtos com preço superior a R$ 1.000,00.
-   `GET /produtos/estatisticas`: Retorna estatísticas (média, máximo, quantidade) por categoria.
-   `GET /produtos/baratos?precoMaximo=500`: Filtra produtos por preço máximo.
-   `POST /produtos`: Cadastra um novo produto.
-   `DELETE /produtos/{id}`: Remove um produto do sistema.

## 🧪 Como Executar e Testar

### Executando em modo de desenvolvimento:
```bash
./mvnw quarkus:dev
```

### Executando os testes:
```bash
./mvnw test
```

### Exemplo de criação de produto (POST):
```json
{
  "nome": "Mouse Vertical Ergonômico",
  "preco": 185.00,
  "ativo": true,
  "categoria": { "nome": "Periféricos" }
}
```
