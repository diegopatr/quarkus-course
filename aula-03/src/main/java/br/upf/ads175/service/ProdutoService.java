package br.upf.ads175.service;

import br.upf.ads175.dto.CategoriaDTO;
import br.upf.ads175.dto.ProdutoDTO;
import br.upf.ads175.model.Categoria;
import br.upf.ads175.model.Produto;
import br.upf.ads175.repository.ProdutoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Serviço CDI responsável pela lógica de negócio de produtos.
 *
 * @ApplicationScoped — Uma única instância é criada para toda a aplicação.
 * Ideal para serviços stateless (sem estado de sessão).
 *
 * A separação Resource → Service → Repository é um padrão arquitetural que:
 * - Facilita testes unitários (o serviço pode ser testado sem HTTP)
 * - Permite reutilização da lógica em diferentes contextos
 * - Respeita o princípio de responsabilidade única (SRP)
 * 
 * Nesta versão simulamos uma arquitetura JPA:
 * - O Service recebe e retorna DTOs para o Resource (camada externa).
 * - O Service utiliza ENTIDADES para conversar com o Repository (camada de persistência).
 * - O mapeamento (conversão) é feito aqui no Service.
 */
@ApplicationScoped
public class ProdutoService {

    @Inject
    ProdutoRepository repository;

    /**
     * Retorna todos os produtos ativos, ordenados alfabeticamente pelo nome.
     */
    public List<ProdutoDTO> buscarProdutosAtivosOrdenadosPorNome() {
        return repository.listAll().stream()
            .filter(Produto::isAtivo)           // apenas ativos
            .sorted(Comparator.comparing(Produto::getNome)) // ordem alfabética
            .map(this::toDTO)                   // Converte Entidade -> DTO
            .toList();
    }

    /**
     * Busca produto por ID.
     */
    public Optional<ProdutoDTO> buscarPorId(Long id) {
        return repository.findById(id)
            .map(this::toDTO); // Converte Entidade -> DTO se existir
    }

    /**
     * Agrupa nomes de produtos por nome da categoria.
     */
    public Map<String, List<String>> buscarNomesProdutosAgrupadosPorCategoria() {
        return repository.listAll().stream()
            .collect(Collectors.groupingBy(
                produto -> produto.getCategoria().getNome(),
                Collectors.mapping(Produto::getNome, Collectors.toList())
            ));
    }

    /**
     * Retorna produtos premium (preço > R$ 1000) ativos.
     */
    public List<ProdutoDTO> buscarProdutosPremium() {
        return repository.listAll().stream()
            .filter(Produto::isAtivo)
            .map(this::toDTO) // Converte para DTO para usar o método isPremium() do Record
            .filter(ProdutoDTO::isPremium)
            .sorted(Comparator.comparing(ProdutoDTO::preco).reversed())
            .toList();
    }

    /**
     * Calcula estatísticas por categoria.
     */
    public Map<String, Map<String, Object>> obterEstatisticasPorCategoria() {
        return repository.listAll().stream()
            .filter(Produto::isAtivo)
            .collect(Collectors.groupingBy(
                produto -> produto.getCategoria().getNome(),
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    produtosCategoria -> {
                        BigDecimal soma = produtosCategoria.stream()
                            .map(Produto::getPreco)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                        
                        BigDecimal media = BigDecimal.ZERO;
                        if (!produtosCategoria.isEmpty()) {
                            media = soma.divide(
                                BigDecimal.valueOf(produtosCategoria.size()),
                                2, RoundingMode.HALF_UP
                            );
                        }
                        
                        BigDecimal maximo = produtosCategoria.stream()
                            .map(Produto::getPreco)
                            .max(BigDecimal::compareTo)
                            .orElse(BigDecimal.ZERO);

                        return Map.<String, Object>of(
                            "quantidade", produtosCategoria.size(),
                            "precoMedio", media,
                            "precoMaximo", maximo
                        );
                    }
                )
            ));
    }

    /**
     * Retorna produtos com preço inferior ou igual ao valor máximo informado.
     */
    public List<ProdutoDTO> buscarProdutosBaratos(BigDecimal precoMaximo) {
        return repository.listAll().stream()
            .filter(Produto::isAtivo)
            .filter(p -> p.getPreco().compareTo(precoMaximo) <= 0)
            .map(this::toDTO)
            .sorted(Comparator.comparing(ProdutoDTO::preco))
            .toList();
    }

    /**
     * Cria um novo produto. Recebe DTO e persiste Entidade.
     */
    public ProdutoDTO criar(ProdutoDTO dto) {
        Produto entidade = toEntity(dto);
        Produto salva = repository.save(entidade);
        return toDTO(salva);
    }

    /**
     * Remove um produto por ID.
     */
    public boolean remover(Long id) {
        return repository.delete(id);
    }

    // --- Métodos de Mapeamento (Simulando o que um MapStruct ou ModelMapper faria) ---

    private ProdutoDTO toDTO(Produto p) {
        return new ProdutoDTO(
            p.getId(),
            p.getNome(),
            p.getPreco(),
            p.isAtivo(),
            new CategoriaDTO(p.getCategoria().getNome())
        );
    }

    private Produto toEntity(ProdutoDTO dto) {
        return new Produto(
            dto.id(),
            dto.nome(),
            dto.preco(),
            dto.ativo(),
            new Categoria(dto.categoria().nome())
        );
    }
}
