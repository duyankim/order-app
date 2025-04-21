package com.example.CatalogService.mysql.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class SellerProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 카산드라의 파티션 키

    private Long sellerId;

    @Builder
    public SellerProduct(Long sellerId) {
        this.sellerId = sellerId;
    }
}
