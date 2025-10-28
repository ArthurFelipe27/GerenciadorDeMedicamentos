package br.com.sistema.controle_medicamentos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SUGESTÃO 4: Tratamento Global de Exceções
 * Captura exceções de todos os controllers e formata uma
 * resposta JSON padronizada, em vez de deixar o Spring
 * retornar o stack trace inteiro.
 */
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    // Handler genérico para qualquer exceção não tratada
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex) {
        
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Erro Interno no Servidor");
        body.put("message", ex.getMessage() != null ? ex.getMessage() : "Ocorreu um erro inesperado.");
        // Não inclua ex.getStackTrace() em produção!

        // Loga o erro no console do servidor para debug
        logger.error("Erro não tratado capturado pelo RestExceptionHandler: ", ex);

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Você pode adicionar handlers mais específicos aqui se precisar
    // Ex: @ExceptionHandler(MinhaExceptionCustomizada.class)
    // public ResponseEntity<Object> handleMinhaException(MinhaExceptionCustomizada ex) { ... }
}
