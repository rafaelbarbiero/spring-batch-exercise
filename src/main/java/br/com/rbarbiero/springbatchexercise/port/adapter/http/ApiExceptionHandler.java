package br.com.rbarbiero.springbatchexercise.port.adapter.http;

import br.com.rbarbiero.springbatchexercise.domain.exception.ProcessedFileNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class ApiExceptionHandler {

    @ExceptionHandler(ProcessedFileNotFoundException.class)
    ResponseEntity<Void> fileNotFoundException() {
        return ResponseEntity.notFound().build();
    }
}
