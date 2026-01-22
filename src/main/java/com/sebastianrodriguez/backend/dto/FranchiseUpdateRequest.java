package com.sebastianrodriguez.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record FranchiseUpdateRequest(
        @NotBlank String name
) {
}
