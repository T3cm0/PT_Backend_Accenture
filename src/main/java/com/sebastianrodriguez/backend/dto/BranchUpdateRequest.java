package com.sebastianrodriguez.backend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para actualizar una sucursal.
 *
 * @param name nuevo nombre de la sucursal.
 */
public record BranchUpdateRequest(
        @NotBlank String name
) {
}
