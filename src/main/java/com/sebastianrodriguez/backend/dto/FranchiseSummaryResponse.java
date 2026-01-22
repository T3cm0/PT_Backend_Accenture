package com.sebastianrodriguez.backend.dto;

/**
 * DTO de respuesta resumida de franquicia.
 *
 * @param id identificador de la franquicia.
 * @param name nombre de la franquicia.
 */
public record FranchiseSummaryResponse(
        Long id,
        String name
) {
}
