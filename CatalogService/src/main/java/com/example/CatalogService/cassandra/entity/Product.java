package com.example.CatalogService.cassandra.entity;


import lombok.Builder;
import lombok.Getter;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.List;

@Table
@Getter
public class Product {

    @PrimaryKey
    private Long id;

    @Column
    private Long sellerId;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private Long price;

    @Column
    private Long stockCount;

    @Column
    private List<String> tags;

    @Builder
    public Product(Long id, Long sellerId, String name, String description, Long price, Long stockCount, List<String> tags) {
        this.id = id;
        this.sellerId = sellerId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockCount = stockCount;
        this.tags = tags;
    }

    public void decreaseStockCount(Long count) {
        if (stockCount - count < 0) {
            throw new IllegalArgumentException("Stock count cannot be negative");
        }
        this.stockCount -= count;
    }
}
