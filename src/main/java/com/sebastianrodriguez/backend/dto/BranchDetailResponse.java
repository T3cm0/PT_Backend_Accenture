package com.sebastianrodriguez.backend.dto;

import java.util.List;

public record BranchDetailResponse(
        Long id,
        String name,
        List<ProductResponse> products
) {
}
