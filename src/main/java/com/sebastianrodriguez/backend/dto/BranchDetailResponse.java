package com.sebastianrodriguez.backend.dto;

import java.util.List;

/**
 * DTO de detalle de sucursal con productos.
 *
 * @param id identificador de la sucursal.
 * @param name nombre de la sucursal.
 * @param products listado de productos.
 */
public record BranchDetailResponse(
        Long id,
        String name,
        List<ProductResponse> products
) {
}
