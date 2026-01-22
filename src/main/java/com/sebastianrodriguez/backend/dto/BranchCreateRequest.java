package com.sebastianrodriguez.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record BranchCreateRequest(
        @NotBlank String name
) {
}
