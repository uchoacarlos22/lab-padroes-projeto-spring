package one.digitalinnovation.gof.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import one.digitalinnovation.gof.service.ClienteNaoEncontradoException;

@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(ClienteNaoEncontradoException.class)
	public ResponseEntity<Map<String, String>> tratarClienteNaoEncontrado(ClienteNaoEncontradoException exception) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", exception.getMessage()));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Map<String, String>> tratarRequisicaoInvalida(IllegalArgumentException exception) {
		return ResponseEntity.badRequest().body(Map.of("erro", exception.getMessage()));
	}
}
