package br.upf.ads175.dto;

import java.math.BigDecimal;

/**
 * Record que representa um produto com categoria associada.
 *
 * Este DTO demonstra:
 * - Composição de Records: ProdutoDTO contém CategoriaDTO
 * - Métodos customizados em Records: isPremium() encapsula lógica de negócio
 * - BigDecimal para valores monetários (evita imprecisão de double/float)
 *
 * JSON resultante:
 * {
 *   "id": 1,
 *   "nome": "Notebook Gamer",
 *   "preco": 8500.00,
 *   "ativo": true,
 *   "categoria": { "nome": "Eletrônicos" },
 *   "premium": true        // derivado de isPremium()
 * }
 *
 * @param id        identificador único do produto
 * @param nome      nome descritivo do produto
 * @param preco     valor monetário usando BigDecimal para precisão
 * @param ativo     indica se o produto está disponível para venda
 * @param categoria categoria à qual o produto pertence
 */
public record ProdutoDTO(
    Long id,
    String nome,
    BigDecimal preco,
    boolean ativo,
    CategoriaDTO categoria
) {
    /**
     * Produto é considerado premium se seu preço é superior a R$ 1.000,00.
     * Jackson serializa este método como propriedade "premium" no JSON
     * (por seguir o padrão isXxx() para boolean).
     */
    public boolean isPremium() {
        return preco.compareTo(BigDecimal.valueOf(1000)) > 0;
    }
}
