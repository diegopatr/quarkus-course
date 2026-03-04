package br.upf.ads175.resource;

import br.upf.ads175.dto.ProdutoDTO;
import br.upf.ads175.service.ProdutoService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Resource JAX-RS que expõe a API REST de produtos.
 *
 * Anotações principais:
 * - @Path("/produtos") — Define a URI base; todos os endpoints partem de /produtos
 * - @Produces(APPLICATION_JSON) — Todas as respostas são serializadas em JSON
 * - @Consumes(APPLICATION_JSON) — Aceita JSON no corpo de requisições POST/PUT
 *
 * O Resource é responsável APENAS por:
 * - Receber a requisição HTTP e extrair parâmetros
 * - Delegar para o Service (lógica de negócio)
 * - Construir a resposta HTTP adequada (status code, headers, corpo)
 */
@Path("/produtos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProdutoResource {

    // CDI injeta automaticamente a instância de ProdutoService
    @Inject
    ProdutoService service;

    /**
     * GET /produtos — Retorna lista de produtos ativos ordenados por nome.
     *
     * Retorno automático: 200 OK com array JSON no corpo.
     * Quando o método retorna uma coleção diretamente, JAX-RS usa 200 como padrão.
     */
    @GET
    public List<ProdutoDTO> obterProdutosAtivos() {
        return service.buscarProdutosAtivosOrdenadosPorNome();
    }

    /**
     * GET /produtos/por-categoria — Agrupa nomes de produtos por categoria.
     *
     * Demonstra que JAX-RS serializa Map<String, List<String>> automaticamente.
     */
    @GET
    @Path("/por-categoria")
    public Map<String, List<String>> obterProdutosAgrupados() {
        return service.buscarNomesProdutosAgrupadosPorCategoria();
    }

    /**
     * GET /produtos/{id} — Busca um produto específico por ID.
     *
     * @PathParam("id") extrai o valor do segmento {id} da URI.
     * Ex: GET /produtos/42 → id = 42
     *
     * Usa Response para retornar:
     * - 200 OK com o produto, se encontrado
     * - 404 Not Found com mensagem de erro, se não encontrado
     */
    @GET
    @Path("/{id}")
    public Response obterProdutoPorId(@PathParam("id") Long id) {
        return service.buscarPorId(id)
            // Se encontrou, retorna 200 OK com o produto no corpo
            .map(produto -> Response.ok(produto).build())
            // Se não encontrou, retorna 404 Not Found com mensagem estruturada
            .orElse(Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of(
                    "erro", "Produto não encontrado",
                    "id", id
                ))
                .build());
    }

    /**
     * GET /produtos/premium — Retorna produtos com preço > R$ 1000.
     */
    @GET
    @Path("/premium")
    public List<ProdutoDTO> obterProdutosPremium() {
        return service.buscarProdutosPremium();
    }

    /**
     * GET /produtos/estatisticas — Estatísticas agregadas por categoria.
     *
     * Retorna mapa com quantidade, preço médio e preço máximo por categoria.
     */
    @GET
    @Path("/estatisticas")
    public Map<String, Map<String, Object>> obterEstatisticas() {
        return service.obterEstatisticasPorCategoria();
    }

    // =====================================================================
    // Exercícios resolvidos
    // =====================================================================

    /**
     * EXERCÍCIO 2: GET /produtos/baratos?precoMaximo=500
     *
     * @QueryParam extrai valor da query string.
     * @DefaultValue define valor padrão se o parâmetro não for informado.
     *
     * Exemplos:
     *   GET /produtos/baratos              → precoMaximo = 500 (padrão)
     *   GET /produtos/baratos?precoMaximo=300 → precoMaximo = 300
     */
    @GET
    @Path("/baratos")
    public List<ProdutoDTO> obterProdutosBaratos(
        @QueryParam("precoMaximo") @DefaultValue("500") BigDecimal precoMaximo
    ) {
        return service.buscarProdutosBaratos(precoMaximo);
    }

    /**
     * EXERCÍCIO 3: POST /produtos — Cria um novo produto.
     *
     * Retorna 201 Created com:
     * - Header Location apontando para o recurso criado (padrão REST)
     * - Corpo contendo o produto criado com o ID gerado
     *
     * Exemplo de requisição:
     * POST /produtos
     * Content-Type: application/json
     * {
     *   "nome": "Webcam HD",
     *   "preco": 299.90,
     *   "ativo": true,
     *   "categoria": { "nome": "Eletrônicos" }
     * }
     */
    @POST
    public Response criar(ProdutoDTO produto) {
        ProdutoDTO criado = service.criar(produto);
        // Constrói a URI do recurso criado: /produtos/{id}
        URI location = URI.create("/produtos/" + criado.id());
        // 201 Created com Location header e corpo
        return Response.created(location).entity(criado).build();
    }

    /**
     * DELETE /produtos/{id} — Remove um produto.
     *
     * Retorna:
     * - 204 No Content se removido com sucesso (sem corpo)
     * - 404 Not Found se o produto não existe
     */
    @DELETE
    @Path("/{id}")
    public Response remover(@PathParam("id") Long id) {
        boolean removido = service.remover(id);
        if (removido) {
            // 204 No Content — operação bem-sucedida, sem corpo de resposta
            return Response.noContent().build();
        }
        // 404 Not Found — recurso não existe
        return Response.status(Response.Status.NOT_FOUND)
            .entity(Map.of("erro", "Produto não encontrado", "id", id))
            .build();
    }
}
