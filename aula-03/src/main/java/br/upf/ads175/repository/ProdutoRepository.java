package br.upf.ads175.repository;

import br.upf.ads175.model.Categoria;
import br.upf.ads175.model.Produto;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repositório em memória para a entidade Produto.
 * 
 * @ApplicationScoped — Garante que exista apenas uma instância desta classe na aplicação,
 * o que é fundamental para um repositório em memória, pois caso contrário os dados 
 * seriam perdidos se uma nova instância fosse criada.
 * 
 * Esta classe segue o Padrão Repository, cuja responsabilidade é isolar a lógica
 * de acesso a dados (persistência) do restante da aplicação (regras de negócio).
 * 
 * Diferença fundamental nesta versão:
 * - Agora o repositório trabalha com ENTIDADES (br.upf.ads175.model.Produto).
 * - O repositório NÃO conhece DTOs, seguindo o padrão JPA onde ele lida com a persistência direta.
 */
@ApplicationScoped
public class ProdutoRepository {

    /**
     * Lista mutável que armazena os produtos em memória.
     * Simulamos aqui a persistência que ocorreria em um banco de dados.
     */
    private final List<Produto> produtos = new ArrayList<>(List.of(
        new Produto(1L, "Notebook Gamer", new BigDecimal("8500.00"), true,
            new Categoria("Eletrônicos")),
        new Produto(2L, "Cadeira de Escritório", new BigDecimal("1200.50"), true,
            new Categoria("Móveis")),
        new Produto(3L, "Monitor 4K", new BigDecimal("2300.00"), false,
            new Categoria("Eletrônicos")),
        new Produto(4L, "Mesa de Escritório", new BigDecimal("850.00"), true,
            new Categoria("Móveis")),
        new Produto(5L, "Teclado Mecânico", new BigDecimal("450.00"), true,
            new Categoria("Eletrônicos")),
        new Produto(6L, "Smartphone Premium", new BigDecimal("3500.00"), true,
            new Categoria("Eletrônicos"))
    ));

    /**
     * Contador para gerar IDs sequenciais para novos produtos.
     */
    private long proximoId = 7L;

    /**
     * Retorna todos os produtos cadastrados.
     * 
     * @return Lista contendo todos os produtos.
     */
    public List<Produto> listAll() {
        return new ArrayList<>(produtos);
    }

    /**
     * Busca um produto pelo seu identificador único.
     * 
     * @param id Identificador do produto.
     * @return Um Optional contendo o produto se encontrado, ou vazio caso contrário.
     */
    public Optional<Produto> findById(Long id) {
        return produtos.stream()
            .filter(p -> p.getId().equals(id))
            .findFirst();
    }

    /**
     * Persiste um novo produto ou atualiza um existente.
     * No caso desta implementação em memória, sempre geramos um novo ID se for criação.
     * 
     * @param produto Dados do produto a ser salvo.
     * @return O produto salvo com seu ID preenchido.
     */
    public Produto save(Produto produto) {
        // Se o produto não tem ID, geramos um (Simulação de auto-incremento)
        if (produto.getId() == null) {
            produto.setId(proximoId++);
        }
        produtos.add(produto);
        return produto;
    }

    /**
     * Remove um produto da lista pelo ID.
     * 
     * @param id Identificador do produto a ser removido.
     * @return true se o produto existia e foi removido, false caso contrário.
     */
    public boolean delete(Long id) {
        return produtos.removeIf(p -> p.getId().equals(id));
    }
}
