package com.sebastianrodriguez.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepcion de recurso no encontrado con respuesta HTTP 404.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {

    /**
     * Construye la excepcion con un mensaje descriptivo.
     *
     * @param message detalle del error.
     */
    public NotFoundException(String message) {
        super(message);
    }
}
