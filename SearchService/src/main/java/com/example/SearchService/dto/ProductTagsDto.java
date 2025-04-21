package com.example.SearchService.dto;

import java.util.List;

public record ProductTagsDto (
        Long productId, List<String> tags
){ }
