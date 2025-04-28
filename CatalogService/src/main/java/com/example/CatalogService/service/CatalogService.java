package com.example.CatalogService.service;

import com.example.CatalogService.cassandra.entity.Product;
import com.example.CatalogService.cassandra.repository.ProductRepository;
import com.example.CatalogService.mysql.entity.SellerProduct;
import com.example.CatalogService.mysql.repository.SellerProductRepository;
import ecommerce.protobuf.EdaMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CatalogService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SellerProductRepository sellerProductRepository;

    @Autowired
    private KafkaTemplate<String, byte[]> kafkaTemplate;

    public Product registerProduct(
            Long sellerId,
            String name,
            String description,
            Long price,
            Long stockCount,
            List<String> tags
    ) {
        SellerProduct sellerProduct = new SellerProduct();
        sellerProductRepository.save(sellerProduct);

        Product product = Product.builder()
                .id(sellerProduct.getId())
                .sellerId(sellerId)
                .name(name)
                .description(description)
                .price(price)
                .stockCount(stockCount)
                .tags(tags)
                .build();

        // 상품 추가 이벤트 발행
        var message = EdaMessage.ProductTags.newBuilder()
                .setProductId(product.getId())
                .addAllTags(tags)
                .build();

        kafkaTemplate.send("product_tags_added", message.toByteArray());

        return productRepository.save(product);
    }

    public void deleteProduct(Long productId) {
        productRepository.findById(productId).ifPresent(product -> {
            // 상품 삭제 이벤트 발행
            var message = EdaMessage.ProductTags.newBuilder()
                    .setProductId(product.getId())
                    .addAllTags(product.getTags())
                    .build();

            kafkaTemplate.send("product_tags_removed", message.toByteArray());
        });

        productRepository.deleteById(productId);
        sellerProductRepository.deleteById(productId);
    }

    public List<Product> getProductsBySellerId(Long sellerId) {
        List<SellerProduct> sellerProducts = sellerProductRepository.findBySellerId(sellerId);
        List<Product> products = new ArrayList<>();

        sellerProducts.forEach(sellerProduct -> {
            productRepository.findById(sellerProduct.getId())
                    .ifPresent(products::add);
        });

        return products;
    }

    public Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow();
    }

    public Product decreaseStockCount(Long productId, Long decreaseCount) {
        Product product = productRepository.findById(productId)
                .orElseThrow();
        product.decreaseStockCount(decreaseCount);

        return productRepository.save(product);
    }
}
