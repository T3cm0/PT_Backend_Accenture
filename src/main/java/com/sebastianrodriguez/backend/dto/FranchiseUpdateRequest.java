package com.sebastianrodriguez.backend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para actualizar una franquicia.
 *
 * @param name nuevo nombre de la franquicia.
 */
public record FranchiseUpdateRequest(
        @NotBlank String name
) {
}
