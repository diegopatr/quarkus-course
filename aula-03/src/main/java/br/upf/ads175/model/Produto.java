package br.upf.ads175.model;

import java.math.BigDecimal;

/**
 * Entidade que representa um produto no banco de dados (simulado).
 * 
 * Em uma arquitetura real JPA:
 * - Esta classe seria anotada com @Entity.
 * - @Id, @GeneratedValue, @Column seriam usados para o mapeamento.
 * - Aqui mantemos como uma classe POJO simples para simulação.
 * 
 * Diferença de Entidade vs DTO:
 * 1. Entidade: Reflete a estrutura do Banco de Dados.
 * 2. DTO: Reflete os dados que a API entrega ou recebe do cliente (JSON).
 * 
 * O serviço (Service) é responsável por converter entre um e outro.
 */
public class Produto {
    private Long id;
    private String nome;
    private BigDecimal preco;
    private boolean ativo;
    private Categoria categoria;

    public Produto() {}

    public Produto(Long id, String nome, BigDecimal preco, boolean ativo, Categoria categoria) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.ativo = ativo;
        this.categoria = categoria;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
}
