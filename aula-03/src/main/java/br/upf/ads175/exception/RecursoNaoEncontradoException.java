package br.upf.ads175.exception;

/**
 * Exceção de domínio que indica que um recurso não foi encontrado.
 *
 * Estender RuntimeException (unchecked) permite lançar a exceção sem
 * obrigar o chamador a usar try/catch — o ExceptionMapper intercepta
 * automaticamente e converte em resposta HTTP 404.
 */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
