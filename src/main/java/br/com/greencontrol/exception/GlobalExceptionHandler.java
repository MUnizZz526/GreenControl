package br.com.greencontrol.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroRespostaDTO> handleRecursoNaoEncontrado(RecursoNaoEncontradoException ex) {
        ErroRespostaDTO erro = new ErroRespostaDTO(
                HttpStatus.NOT_FOUND.value(),
                "Recurso Não Encontrado",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<ErroRespostaDTO> handleRegraDeNegocio(RegraDeNegocioException ex) {
        ErroRespostaDTO erro = new ErroRespostaDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Regra de Negócio Violada",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroRespostaDTO> handleValidacao(MethodArgumentNotValidException ex) {
        List<String> detalhes = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(e -> {
                    if (e instanceof FieldError fe) {
                        return fe.getField() + ": " + fe.getDefaultMessage();
                    }
                    return e.getDefaultMessage();
                })
                .toList();

        ErroRespostaDTO erro = new ErroRespostaDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de Validação de Dados",
                "Um ou mais campos contêm valores inválidos.",
                detalhes
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<ErroRespostaDTO> handleIntegridade(Exception ex) {
        return ResponseEntity.status(409).body(new ErroRespostaDTO(409, "Conflito de integridade", "O recurso possui vínculos ou viola uma restrição de integridade."));
    }

    @ExceptionHandler({org.springframework.http.converter.HttpMessageNotReadableException.class,
            org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class,
            org.springframework.web.bind.MissingServletRequestParameterException.class,
            org.springframework.web.method.annotation.HandlerMethodValidationException.class,
            jakarta.validation.ConstraintViolationException.class})
    public ResponseEntity<ErroRespostaDTO> handleEntrada(Exception ex) {
        return ResponseEntity.badRequest().body(new ErroRespostaDTO(400, "Entrada inválida", "Verifique o JSON, os parâmetros e os formatos informados."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroRespostaDTO> handleGenerico(Exception ex) {
        ErroRespostaDTO erro = new ErroRespostaDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro Interno do Servidor",
                "Ocorreu um erro inesperado no servidor."
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }
}
