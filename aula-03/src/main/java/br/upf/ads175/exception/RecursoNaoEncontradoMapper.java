package br.upf.ads175.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Mapper que intercepta RecursoNaoEncontradoException e converte em HTTP 404.
 *
 * @Provider — Registra automaticamente este mapper no container JAX-RS.
 * Não é necessário nenhuma configuração adicional; basta anotar com @Provider.
 *
 * implements ExceptionMapper<T> — O tipo genérico T define qual exceção
 * este mapper intercepta. Cada exceção pode ter seu próprio mapper.
 *
 * Fluxo:
 * 1. Um endpoint lança RecursoNaoEncontradoException
 * 2. JAX-RS detecta que existe um ExceptionMapper para essa exceção
 * 3. Chama toResponse() para converter em resposta HTTP
 * 4. Cliente recebe 404 com corpo JSON estruturado
 */
@Provider
public class RecursoNaoEncontradoMapper
    implements ExceptionMapper<RecursoNaoEncontradoException> {

    @Override
    public Response toResponse(RecursoNaoEncontradoException e) {
        return Response.status(Response.Status.NOT_FOUND)
            .entity(new ErroResponse(
                e.getMessage(),
                "RECURSO_NAO_ENCONTRADO",
                404
            ))
            .type(MediaType.APPLICATION_JSON)
            .build();
    }
}
