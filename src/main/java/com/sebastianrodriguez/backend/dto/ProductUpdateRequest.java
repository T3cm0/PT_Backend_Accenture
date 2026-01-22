package com.sebastianrodriguez.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO para actualizar un producto.
 *
 * @param name nombre del producto.
 * @param stock nuevo stock (>= 0).
 */
public record ProductUpdateRequest(
        @NotBlank String name,
        @Min(0) int stock
) {
}
