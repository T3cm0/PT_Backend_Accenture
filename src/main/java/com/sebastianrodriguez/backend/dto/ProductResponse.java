package com.sebastianrodriguez.backend.dto;

/**
 * DTO de respuesta de producto.
 *
 * @param id identificador del producto.
 * @param name nombre del producto.
 * @param stock stock actual.
 */
public record ProductResponse(
        Long id,
        String name,
        int stock
) {
}
