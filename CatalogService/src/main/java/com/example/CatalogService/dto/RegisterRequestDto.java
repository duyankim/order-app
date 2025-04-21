package com.example.CatalogService.dto;

import java.util.List;

public record RegisterRequestDto(
        Long sellerId,
        String name,
        String description,
        Long price,
        Long stockCount,
        List<String> tags
) { }
