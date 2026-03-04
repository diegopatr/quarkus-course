package br.upf.ads175.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Mapper para IllegalArgumentException → HTTP 400 Bad Request.
 *
 * Usado quando parâmetros de entrada são inválidos (valores negativos,
 * formatos incorretos, etc.). Centraliza o tratamento para que os
 * endpoints não precisem ter blocos try/catch individuais.
 */
@Provider
public class GenericExceptionMapper implements ExceptionMapper<IllegalArgumentException> {

    @Override
    public Response toResponse(IllegalArgumentException e) {
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(new ErroResponse(
                e.getMessage(),
                "VALIDACAO",
                400
            ))
            .type(MediaType.APPLICATION_JSON)
            .build();
    }
}
