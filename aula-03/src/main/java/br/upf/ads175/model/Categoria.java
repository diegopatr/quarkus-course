package br.upf.ads175.model;

/**
 * Entidade que representa uma categoria de produto no banco de dados (simulado).
 * 
 * Diferente do DTO, uma entidade:
 * - Representa o estado persistente do sistema.
 * - Geralmente possui campos extras que não interessam ao cliente da API (ex: data_criacao, versao).
 * - Em JPA, teria anotações como @Entity e @Id.
 */
public class Categoria {
    private String nome;

    public Categoria() {}

    public Categoria(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
