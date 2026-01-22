package com.sebastianrodriguez.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record FranchiseCreateRequest(
        @NotBlank String name
) {
}
