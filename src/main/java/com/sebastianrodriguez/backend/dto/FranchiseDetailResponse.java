package com.sebastianrodriguez.backend.dto;

import java.util.List;

public record FranchiseDetailResponse(
        Long id,
        String name,
        List<BranchDetailResponse> branches
) {
}
