package br.upf.ads175.dto;

/**
 * Record que representa uma categoria de produto.
 *
 * Records são ideais para DTOs (Data Transfer Objects) pois são:
 * - Imutáveis: valores definidos no construtor não podem ser alterados
 * - Concisos: getters, equals(), hashCode() e toString() gerados automaticamente
 * - Serializáveis: Jackson converte automaticamente para/de JSON
 *
 * JSON resultante:
 * { "nome": "Eletrônicos" }
 */
public record CategoriaDTO(String nome) {}
