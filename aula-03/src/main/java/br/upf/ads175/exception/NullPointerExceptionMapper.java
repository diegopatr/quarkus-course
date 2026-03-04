package br.upf.ads175.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * EXERCÍCIO 4: ExceptionMapper para NullPointerException.
 *
 * Retorna HTTP 500 Internal Server Error com mensagem amigável,
 * ocultando detalhes internos da exceção por segurança.
 *
 * Em produção, isso evita que stack traces sejam expostos ao cliente,
 * enquanto registra o erro no log do servidor para investigação.
 */
@Provider
public class NullPointerExceptionMapper implements ExceptionMapper<NullPointerException> {

    @Override
    public Response toResponse(NullPointerException e) {
        // Log do erro real no servidor (não exposto ao cliente)
        System.err.println("[ERRO INTERNO] NullPointerException: " + e.getMessage());

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity(new ErroResponse(
                "Erro interno no servidor. Tente novamente mais tarde.",
                "ERRO_INTERNO",
                500
            ))
            .type(MediaType.APPLICATION_JSON)
            .build();
    }
}
