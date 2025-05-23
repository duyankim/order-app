package com.example.CatalogService.controller;

import com.example.CatalogService.cassandra.entity.Product;
import com.example.CatalogService.dto.DecreaseStockCountDto;
import com.example.CatalogService.dto.RegisterRequestDto;
import com.example.CatalogService.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CatalogController {

    @Autowired
    private CatalogService catalogService;

    @PostMapping("/catalog/products")
    public Product registerProduct(@RequestBody RegisterRequestDto dto) {
        return catalogService.registerProduct(
                dto.sellerId(),
                dto.name(),
                dto.description(),
                dto.price(),
                dto.stockCount(),
                dto.tags()
        );
    }

    @DeleteMapping("/catalog/products/{productId}")
    public void deleteProduct(@PathVariable Long productId) {
        catalogService.deleteProduct(productId);
    }

    @GetMapping("/catalog/products/{productId}")
    public Product getProductById(@PathVariable Long productId) {
        return catalogService.getProductById(productId);
    }

    @GetMapping("/catalog/sellers/{sellerId}/products")
    public List<Product> getProductsBySellerId(@PathVariable Long sellerId) {
        return catalogService.getProductsBySellerId(sellerId);
    }

    @GetMapping("/catalog/products/{productId}/decreaseStockCount")
    public Product decreaseStockCount(@PathVariable Long productId, @RequestBody DecreaseStockCountDto dto) {
        return catalogService.decreaseStockCount(productId, dto.decreaseCount());
    }
}
