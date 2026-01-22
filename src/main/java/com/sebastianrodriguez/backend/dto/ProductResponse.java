package com.sebastianrodriguez.backend.dto;

public record ProductResponse(
        Long id,
        String name,
        int stock
) {
}
