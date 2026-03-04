package br.upf.ads175;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Aula 02 — Java Moderno para Desenvolvimento Enterprise.
 *
 * Esta classe serve como um guia prático e teórico sobre as evoluções do ecossistema Java,
 * focando em produtividade, segurança de tipos e expressividade.
 *
 * CONCEITOS CHAVE PARA ESTUDANTES DE CIÊNCIA DA COMPUTAÇÃO:
 *
 * 1. IMUTABILIDADE: Não é apenas a impossibilidade de alterar um estado. É um pilar da
 *    programação funcional que garante que uma instância, uma vez validada e criada,
 *    permanecerá consistente durante todo o seu ciclo de vida. Isso elimina efeitos colaterais
 *    (side-effects), facilita a depuração e torna o código thread-safe por natureza.
 *
 * 2. PROGRAMAÇÃO DECLARATIVA vs IMPERATIVA: Com Streams API, mudamos o foco de "como fazer"
 *    (loops, contadores, estados temporários) para "o que fazer" (filtrar, mapear, reduzir).
 *
 * 3. TIPAGEM FORTE E EXPRESSIVA: Recursos como Sealed Classes e Pattern Matching permitem
 *    que o compilador ajude o desenvolvedor a garantir a exatidão lógica (exhaustiveness checking).
 *
 * Execute: javac --release 21 -d out src/main/java/br/upf/ads175/*.java
 *          java -cp out br.upf.ads175.JavaModerno
 */
public class JavaModerno {

    // =====================================================================
    // 1. RECURSOS DE MODELAGEM E POLIMORFISMO
    // =====================================================================

    /**
     * SEALED INTERFACES (Interfaces Seladas - Java 17+)
     *
     * O conceito de "Selamento" permite restringir quais classes ou
     * interfaces podem implementar ou estender uma determinada interface.
     *
     * Vantagens técnicas:
     * - Controle de Hierarquia: Definição explícita dos subtipos permitidos.
     * - Análise de Exaustividade: O compilador valida todas as implementações
     *   possíveis, permitindo que expressões 'switch' dispensem a cláusula 'default'.
     */
    public sealed interface Forma permits Circulo, Retangulo {}

    /**
     * RECORDS (Java 16+)
     *
     * Records são classes de dados transparentes e imutáveis por padrão.
     * A declaração de um record induz a geração automática de:
     * - Atributos privados e finais (private final).
     * - Construtor canônico.
     * - Métodos de acesso (ex: raio()).
     * - Implementações de equals(), hashCode() e toString() baseadas no estado.
     */
    public record Circulo(double raio) implements Forma {}

    public record Retangulo(double largura, double altura) implements Forma {}

    /**
     * Record Produto — Exemplo de DTO (Data Transfer Object).
     *
     * IMUTABILIDADE PROFUNDA: Os atributos (Long, String, BigDecimal, boolean)
     * são tipos imutáveis ou primitivos. Coleções mutáveis, se presentes, devem ser
     * encapsuladas para preservar a integridade do estado interno.
     */
    public record Produto(Long id, String nome, BigDecimal preco,
                          boolean ativo, String categoria) {

        /**
         * COMPACT CONSTRUCTOR
         *
         * Variante do construtor que omite a repetição de parâmetros. É o local
         * preconizado para validações e verificação de INVARIANTES DE CLASSE.
         *
         * Invariante: Condição lógica que deve ser mantida durante todo o ciclo de vida
         * do objeto. Garante-se, por exemplo, que o preço nunca seja negativo na memória.
         */
        public Produto {
            Objects.requireNonNull(nome, "O nome é uma invariante e não pode ser nulo.");
            Objects.requireNonNull(preco, "O preço é uma invariante e não pode ser nulo.");
            if (preco.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Preço negativo viola a regra de negócio.");
            }
        }

        /**
         * Lógica de Negócio encapsulada.
         * Aplicação do princípio "Tell, Don't Ask": o objeto provê a resposta
         * baseada em seu estado interno em vez de expô-lo para processamento externo.
         */
        public boolean isPremium() {
            return preco.compareTo(BigDecimal.valueOf(1000)) > 0;
        }
    }

    /** Record para transportar resultados de agregações estatísticas */
    public record EstatisticaCategoria(String categoria, long quantidade,
                                       BigDecimal precoMedio, BigDecimal precoMaximo) {}

    // =====================================================================
    // 2. DATA SOURCE (Simulado)
    // =====================================================================

    /**
     * List.of() instancia uma lista IMUTÁVEL. Operações de modificação resultam em
     * UnsupportedOperationException, garantindo segurança em tempo de execução.
     */
    static List<Produto> criarProdutos() {
        return List.of(
                new Produto(1L, "Notebook Gamer",       new BigDecimal("8500.00"), true,  "Eletrônicos"),
                new Produto(2L, "Cadeira de Escritório", new BigDecimal("1200.50"), true,  "Móveis"),
                new Produto(3L, "Monitor 4K",            new BigDecimal("2300.00"), false, "Eletrônicos"),
                new Produto(4L, "Mesa de Escritório",    new BigDecimal("850.00"),  true,  "Móveis"),
                new Produto(5L, "Teclado Mecânico",      new BigDecimal("450.00"),  true,  "Eletrônicos"),
                new Produto(6L, "Smartphone Premium",    new BigDecimal("3500.00"), true,  "Eletrônicos"),
                new Produto(7L, "Luminária LED",         new BigDecimal("150.00"),  false, "Iluminação"),
                new Produto(8L, "Cabo USB-C",            new BigDecimal("45.00"),   true,  "Eletrônicos")
        );
    }

    public static void main(String[] args) {
        var produtos = criarProdutos();

        System.out.println("=== AULA 02 — JAVA MODERNO PARA CIÊNCIA DA COMPUTAÇÃO ===\n");

        demoCollections(produtos);
        demoLambdaStreams(produtos);
        demoRecords();
        demoTextBlocks();
        demoPatternMatching();
        demoBigDecimal();
    }

    // =====================================================================
    // 3. DEMONSTRAÇÕES TÉCNICAS
    // =====================================================================

    /**
     * COLLECTIONS FRAMEWORK
     * Estudo das estruturas de dados fundamentais do Java.
     */
    static void demoCollections(List<Produto> produtos) {
        System.out.println("--- 1. Collections Framework ---");

        // LIST: Estrutura linear que mantém a ordem de inserção e permite duplicatas.
        // ArrayList é baseada em arrays redimensionáveis (Acesso O(1), Inserção O(n) no pior caso).
        List<String> nomes = new ArrayList<>(produtos.stream().map(Produto::nome).toList());
        Collections.sort(nomes); // Algoritmo Timsort (O(n log n))
        System.out.println("List (Ordenada): " + nomes);

        // SET: Estrutura que não permite elementos duplicados.
        // HashSet utiliza uma Tabela Hash (Acesso O(1) em média).
        // Útil para extrair valores únicos (como categorias).
        Set<String> categorias = new HashSet<>(produtos.stream().map(Produto::categoria).toList());
        System.out.println("Set (Únicos): " + categorias);

        // MAP: Estrutura de Chave-Valor (Dicionário).
        // Essencial para buscas rápidas por um identificador.
        Map<Long, String> idParaNome = new HashMap<>();
        produtos.forEach(p -> idParaNome.put(p.id(), p.nome()));
        System.out.println("Map (Busca por ID): " + idParaNome);

        System.out.println();
    }

    /**
     * LAMBDA & STREAMS API
     * Transição do paradigma imperativo para o declarativo.
     */
    static void demoLambdaStreams(List<Produto> produtos) {
        System.out.println("--- 2. Lambda & Streams API ---");

        /**
         * STREAMS representam sequências de elementos que suportam operações agregadas.
         * Elas não armazenam dados, mas processam fluxos a partir de uma fonte (como uma List).
         *
         * OPERAÇÕES INTERMEDIÁRIAS (Lazy): filter, map, sorted.
         * OPERAÇÕES TERMINAIS (Eager): toList, collect, reduce, count.
         */

        // Pipeline 1: Filtro e Ordenação
        List<Produto> ativos = produtos.stream()
                .filter(Produto::ativo)                            // Predicate: Produto -> boolean
                .sorted(Comparator.comparing(Produto::nome))       // Function para extrair chave de ordenação
                .toList();
        System.out.println("Ativos ordenados: " + ativos.stream().map(Produto::nome).toList());

        // Pipeline 2: Transformação (Map) e Redução
        // Aqui estamos encadeando múltiplos filtros antes de transformar em String.
        List<String> premium = produtos.stream()
                .filter(Produto::ativo)
                .filter(Produto::isPremium)
                .sorted(Comparator.comparing(Produto::preco).reversed())
                .map(Produto::nome) // Transforma Stream<Produto> em Stream<String>
                .toList();
        System.out.println("Nomes Premium (Desc): " + premium);

        // Pipeline 3: Agrupamento (GroupingBy)
        // Similar ao "GROUP BY" do SQL, mas em memória.
        Map<String, List<String>> porCategoria = produtos.stream()
                .collect(Collectors.groupingBy(
                        Produto::categoria,
                        Collectors.mapping(Produto::nome, Collectors.toList())
                ));
        System.out.println("Agrupado por Categoria: " + porCategoria);

        // Pipeline 4: Agregações Complexas (Collectors.collectingAndThen)
        // Demonstra como calcular estatísticas customizadas por grupo.
        System.out.println("Estatísticas Avançadas por Categoria:");
        Map<String, EstatisticaCategoria> estatisticas = produtos.stream()
                .filter(Produto::ativo)
                .collect(Collectors.groupingBy(
                        Produto::categoria,
                        Collectors.collectingAndThen(Collectors.toList(), lista -> {
                            // Redução manual para soma usando BigDecimal
                            BigDecimal soma = lista.stream()
                                    .map(Produto::preco)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                            BigDecimal media = soma.divide(BigDecimal.valueOf(lista.size()), 2, RoundingMode.HALF_UP);

                            BigDecimal max = lista.stream()
                                    .map(Produto::preco)
                                    .max(BigDecimal::compareTo)
                                    .orElse(BigDecimal.ZERO);

                            return new EstatisticaCategoria(
                                    lista.getFirst().categoria(),
                                    lista.size(), media, max
                            );
                        })
                ));

        estatisticas.forEach((cat, est) ->
                System.out.printf("  > %s: %d itens, Média: R$%s, Máx: R$%s%n",
                        cat, est.quantidade(), est.precoMedio(), est.precoMaximo()));
        System.out.println();
    }

    /**
     * RECORDS E IMUTABILIDADE
     * Vantagens da imutabilidade na consistência de dados.
     */
    static void demoRecords() {
        System.out.println("--- 3. Records e Imutabilidade ---");
        var p1 = new Produto(1L, "Notebook", new BigDecimal("2500"), true, "Eletrônicos");
        var p2 = new Produto(1L, "Notebook", new BigDecimal("2500"), true, "Eletrônicos");

        // VALOR vs REFERÊNCIA: Records implementam equals() baseado no estado interno,
        // aproximando a semântica de objetos do conceito matemático de igualdade.
        System.out.println("Igualdade por valor: " + p1.equals(p2)); // true

        // A representação textual automática (toString) otimiza logging e depuração.
        System.out.println("Representação textual: " + p1);

        // ACESSORES: O uso de p1.nome() em vez de prefixos "get" reduz o ruído sintático.
        System.out.println("Acesso direto: " + p1.nome());
        System.out.println();
    }

    /**
     * TEXT BLOCKS
     * Melhora a legibilidade de códigos externos dentro do Java (SQL, JSON, HTML).
     */
    static void demoTextBlocks() {
        System.out.println("--- 4. Text Blocks ---");

        // Preserva a formatação e as aspas sem a necessidade de escapes (\") ou concatenações (+).
        String json = """
                {
                    "curso": "Ciência da Computação",
                    "modulo": "Java Moderno",
                    "status": "Ativo"
                }
                """;
        System.out.println("JSON Formatado:\n" + json);
    }

    /**
     * PATTERN MATCHING
     * Redução de "Cerimônia" (Boilerplate) e aumento de segurança de tipos.
     */
    static void demoPatternMatching() {
        System.out.println("--- 5. Pattern Matching & Switch ---");

        // Pattern Matching para instanceof: O Java já faz o cast automático para a variável 's'.
        Object objeto = "Estudante de Computação";
        if (objeto instanceof String s) {
            System.out.println("Tamanho do texto: " + s.length());
        }

        // RECORD PATTERNS (Java 21): Decomposição de objetos diretamente no switch.
        record Ponto(int x, int y) {}
        Object forma = new Ponto(10, 20);

        String descricao = switch (forma) {
            case Integer i -> "É um número: " + i;
            case Ponto(var x, var y) -> "Coordenadas extraídas: x=" + x + ", y=" + y;
            default -> "Outro tipo";
        };
        System.out.println(descricao);

        // EXAUSTIVIDADE com Sealed Classes e Switch
        Forma f = new Circulo(5.0);
        double area = switch (f) {
            case Circulo c -> Math.PI * Math.pow(c.raio(), 2);
            case Retangulo r -> r.largura() * r.altura();
            // Note: Não há 'default'! O compilador garante que tratamos todos os casos de 'Forma'.
        };
        System.out.printf("Área calculada: %.2f%n%n", area);
    }

    /**
     * BIGDECIMAL E PRECISÃO FINANCEIRA
     * Análise da imprecisão do ponto flutuante em domínios críticos.
     */
    static void demoBigDecimal() {
        System.out.println("--- 6. Precisão Numérica com BigDecimal ---");

        // O erro do ponto flutuante (IEEE 754):
        // Representações binárias de tipos primitivos (double/float) podem gerar
        // imprecisões em frações decimais.
        double d1 = 0.1, d2 = 0.2;
        System.out.println("Double (Impreciso): 0.1 + 0.2 = " + (d1 + d2));

        // BigDecimal provê aritmética exata em base 10.
        BigDecimal b1 = new BigDecimal("0.1");
        BigDecimal b2 = new BigDecimal("0.2");
        System.out.println("BigDecimal (Exato): 0.1 + 0.2 = " + b1.add(b2));

        // ARREDONDAMENTO: HALF_UP é a estratégia padrão para cálculos comerciais.
        BigDecimal valor = new BigDecimal("10.555");
        System.out.println("Arredondado (2 casas): " + valor.setScale(2, RoundingMode.HALF_UP));
        System.out.println();
    }
}
