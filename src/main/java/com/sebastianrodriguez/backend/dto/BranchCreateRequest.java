package com.sebastianrodriguez.backend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para crear una sucursal.
 *
 * @param name nombre de la sucursal.
 */
public record BranchCreateRequest(
        @NotBlank String name
) {
}
