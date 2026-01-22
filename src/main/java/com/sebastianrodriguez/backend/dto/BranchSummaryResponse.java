package com.sebastianrodriguez.backend.dto;

/**
 * DTO de respuesta resumida de sucursal.
 *
 * @param id identificador de la sucursal.
 * @param name nombre de la sucursal.
 */
public record BranchSummaryResponse(
        Long id,
        String name
) {
}
