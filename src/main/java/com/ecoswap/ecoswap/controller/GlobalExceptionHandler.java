package com.ecoswap.ecoswap.controller;

import com.ecoswap.ecoswap.exception.ResourceNotFoundException;
import com.ecoswap.ecoswap.exception.InvalidCredentialsException;
import com.ecoswap.ecoswap.exception.InvalidOperationException;
import com.ecoswap.ecoswap.exception.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase centralizada para manejar excepciones a nivel global en todos los controladores.
 * Utiliza @ControllerAdvice para aplicar la lógica de manejo de excepciones a toda la API.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    // Estructura de la respuesta de error para mantener la consistencia
    private ResponseEntity<Object> buildErrorResponse(HttpStatus status, Exception ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", new Date());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(body, status);
    }

    /**
     * Maneja la excepción ResourceNotFoundException (404 NOT FOUND).
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        // Esta excepción ya está marcada como 404 (@ResponseStatus en la clase),
        // pero la manejamos aquí para estandarizar el cuerpo de la respuesta.
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex, request);
    }

    /**
     * Maneja la excepción UserAlreadyExistsException (409 CONFLICT).
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Object> handleUserAlreadyExistsException(UserAlreadyExistsException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex, request);
    }

    /**
     * Maneja la excepción InvalidCredentialsException (401 UNAUTHORIZED).
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Object> handleInvalidCredentialsException(InvalidCredentialsException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex, request);
    }
    
    /**
     * Maneja la excepción InvalidOperationException (400 BAD REQUEST).
     */
    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<Object> handleInvalidOperationException(InvalidOperationException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex, request);
    }


    /**
     * Manejador de excepciones genérico para cualquier otra excepción no cubierta (500 INTERNAL SERVER ERROR).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex, WebRequest request) {
        // Registra el error interno en el log para su posterior análisis.
        System.err.println("Error interno del servidor: " + ex.getMessage());
        ex.printStackTrace();
        
        // Devuelve un mensaje genérico al cliente por seguridad.
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, new Exception("Ocurrió un error interno del servidor."), request);
    }
}