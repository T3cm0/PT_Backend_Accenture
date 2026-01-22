package com.sebastianrodriguez.backend.dto;

/**
 * DTO de reporte de producto con mayor stock por sucursal.
 *
 * @param branchId identificador de la sucursal.
 * @param branchName nombre de la sucursal.
 * @param productId identificador del producto.
 * @param productName nombre del producto.
 * @param stock stock del producto.
 */
public record BranchTopStockProductResponse(
        Long branchId,
        String branchName,
        Long productId,
        String productName,
        int stock
) {
}
