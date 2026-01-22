package com.sebastianrodriguez.backend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para crear una franquicia.
 *
 * @param name nombre de la franquicia.
 */
public record FranchiseCreateRequest(
        @NotBlank String name
) {
}
