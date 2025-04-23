package com.example.CatalogService.dto;

import java.util.List;

public record ProductTagsDto(
        Long productId,
        List<String> tags
) { }
