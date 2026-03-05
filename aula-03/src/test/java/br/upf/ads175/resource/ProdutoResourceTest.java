package br.upf.ads175.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Testes de integração para a API REST de produtos.
 *
 * @QuarkusTest inicia toda a aplicação Quarkus antes de executar os testes,
 * permitindo testar endpoints HTTP reais com REST Assured.
 *
 * REST Assured usa uma sintaxe BDD (Given-When-Then):
 * - given() — pré-condições (headers, parâmetros, corpo)
 * - when()  — ação (verbo HTTP + URL)
 * - then()  — verificações (status code, corpo, headers)
 */
@QuarkusTest
class ProdutoResourceTest {

    // =========================================================================
    // GET /produtos — Lista de produtos ativos
    // =========================================================================

    @Test
    void deveRetornarListaDeProdutosAtivos() {
        given()
            .when().get("/produtos")
            .then()
                .statusCode(200)
                // Verifica que retorna uma lista não vazia
                .body("size()", greaterThan(0))
                // O produto inativo (Monitor 4K) não deve aparecer
                .body("nome", not(hasItem("Monitor 4K")))
                // Os ativos devem estar presentes
                .body("nome", hasItem("Notebook Gamer"))
                .body("nome", hasItem("Teclado Mecânico"));
    }

    // =========================================================================
    // GET /produtos/{id} — Busca por ID
    // =========================================================================

    @Test
    void deveRetornarProdutoQuandoIdExiste() {
        given()
            .when().get("/produtos/1")
            .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("nome", equalTo("Notebook Gamer"))
                .body("preco", equalTo(8500.00f))
                .body("ativo", equalTo(true))
                .body("categoria.nome", equalTo("Eletrônicos"));
    }

    @Test
    void deveRetornar404QuandoIdNaoExiste() {
        given()
            .when().get("/produtos/999")
            .then()
                .statusCode(404)
                .body("erro", containsString("não encontrado"));
    }

    // =========================================================================
    // GET /produtos/premium — Produtos com preço > R$ 1000
    // =========================================================================

    @Test
    void deveRetornarApenasProdutosPremium() {
        given()
            .when().get("/produtos/premium")
            .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                // Verifica que todos os preços são > 1000
                .body("preco", everyItem(greaterThan(1000.0f)))
                // Teclado Mecânico (R$ 450) não deve aparecer
                .body("nome", not(hasItem("Teclado Mecânico")));
    }

    // =========================================================================
    // GET /produtos/por-categoria — Agrupamento
    // =========================================================================

    @Test
    void deveAgruparProdutosPorCategoria() {
        given()
            .when().get("/produtos/por-categoria")
            .then()
                .statusCode(200)
                // Verifica que as categorias existem
                .body("Eletrônicos", notNullValue())
                .body("Móveis", notNullValue())
                .body("Eletrônicos.size()", greaterThan(0));
    }

    // =========================================================================
    // GET /produtos/estatisticas — Estatísticas por categoria
    // =========================================================================

    @Test
    void deveRetornarEstatisticasPorCategoria() {
        given()
            .when().get("/produtos/estatisticas")
            .then()
                .statusCode(200)
                .body("Eletrônicos.quantidade", greaterThan(0))
                .body("Eletrônicos.precoMedio", notNullValue())
                .body("Eletrônicos.precoMaximo", notNullValue());
    }

    // =========================================================================
    // GET /produtos/baratos — Exercício 2
    // =========================================================================

    @Test
    void deveRetornarProdutosBaratosComPrecoMaximoPadrao() {
        given()
            .when().get("/produtos/baratos")
            .then()
                .statusCode(200)
                // Com padrão de R$ 500, apenas Teclado Mecânico (R$ 450) deve retornar
                .body("size()", greaterThan(0))
                .body("preco", everyItem(lessThanOrEqualTo(500.0f)));
    }

    @Test
    void deveRetornarProdutosBaratosComPrecoMaximoCustomizado() {
        given()
            .queryParam("precoMaximo", 1000)
            .when().get("/produtos/baratos")
            .then()
                .statusCode(200)
                .body("preco", everyItem(lessThanOrEqualTo(1000.0f)));
    }

    // =========================================================================
    // POST /produtos — Exercício 3
    // =========================================================================

    @Test
    void deveCriarNovoProdutoComStatus201() {
        String novoProduto = """
            {
                "nome": "Webcam HD",
                "preco": 299.90,
                "ativo": true,
                "categoria": { "nome": "Eletrônicos" }
            }
            """;

        given()
            .contentType("application/json")
            .body(novoProduto)
            .when().post("/produtos")
            .then()
                .statusCode(201)
                // Verifica que o ID foi gerado
                .body("id", notNullValue())
                .body("nome", equalTo("Webcam HD"))
                .body("preco", equalTo(299.90f))
                // Verifica header Location
                .header("Location", containsString("/produtos/"));
    }

    // =========================================================================
    // DELETE /produtos/{id}
    // =========================================================================

    @Test
    void deveRemoverProdutoComStatus204() {
        // Primeiro cria um produto para depois remover
        String novoProduto = """
            {
                "nome": "Produto Temporário",
                "preco": 100.00,
                "ativo": true,
                "categoria": { "nome": "Teste" }
            }
            """;

        // Cria o produto
        int id = given()
            .contentType("application/json")
            .body(novoProduto)
            .when().post("/produtos")
            .then().statusCode(201)
            .extract().path("id");

        // Remove o produto criado
        given()
            .when().delete("/produtos/" + id)
            .then()
                .statusCode(204);
    }

    @Test
    void deveRetornar404AoRemoverProdutoInexistente() {
        given()
            .when().delete("/produtos/99999")
            .then()
                .statusCode(404);
    }
}
