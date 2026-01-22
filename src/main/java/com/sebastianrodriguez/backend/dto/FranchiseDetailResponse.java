package com.sebastianrodriguez.backend.dto;

import java.util.List;

/**
 * DTO de detalle de franquicia con sus sucursales.
 *
 * @param id identificador de la franquicia.
 * @param name nombre de la franquicia.
 * @param branches listado de sucursales.
 */
public record FranchiseDetailResponse(
        Long id,
        String name,
        List<BranchDetailResponse> branches
) {
}
