package br.upf.ads175.exception;

import java.time.LocalDateTime;

/**
 * Record que padroniza a estrutura de respostas de erro da API.
 *
 * Ao usar um record dedicado para erros, garantimos:
 * - Consistência: todas as respostas de erro seguem o mesmo formato
 * - Documentação: clientes da API sabem exatamente o que esperar
 * - Serialização: Jackson converte automaticamente para JSON
 *
 * JSON resultante:
 * {
 *   "erro": "Produto não encontrado com ID: 999",
 *   "tipo": "RECURSO_NAO_ENCONTRADO",
 *   "status": 404,
 *   "timestamp": "2025-01-15T10:30:00"
 * }
 *
 * @param erro      mensagem descritiva do erro
 * @param tipo      código/tipo do erro para tratamento programático
 * @param status    código HTTP correspondente
 * @param timestamp momento em que o erro ocorreu
 */
public record ErroResponse(
    String erro,
    String tipo,
    int status,
    LocalDateTime timestamp
) {
    /**
     * Construtor compacto que preenche o timestamp automaticamente.
     * Útil para criar respostas de erro sem informar a data manualmente.
     */
    public ErroResponse(String erro, String tipo, int status) {
        this(erro, tipo, status, LocalDateTime.now());
    }
}
