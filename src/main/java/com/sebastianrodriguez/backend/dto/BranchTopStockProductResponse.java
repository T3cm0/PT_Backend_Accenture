package com.sebastianrodriguez.backend.dto;

public record BranchTopStockProductResponse(
        Long branchId,
        String branchName,
        Long productId,
        String productName,
        int stock
) {
}
