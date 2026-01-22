package com.sebastianrodriguez.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO para crear un producto.
 *
 * @param name nombre del producto.
 * @param stock stock inicial (>= 0).
 */
public record ProductCreateRequest(
        @NotBlank String name,
        @Min(0) int stock
) {
}
