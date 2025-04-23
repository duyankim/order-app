package com.example.OrderService.service;

import com.example.OrderService.dto.*;
import com.example.OrderService.entity.ProductOrder;
import com.example.OrderService.enums.OrderStatus;
import com.example.OrderService.feign.CatalogClient;
import com.example.OrderService.feign.DeliveryClient;
import com.example.OrderService.feign.PaymentClient;
import com.example.OrderService.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentClient paymentClient;

    @Autowired
    private DeliveryClient deliveryClient;

    @Autowired
    private CatalogClient catalogClient;

    public StartOrderResponseDto startOrder(Long userId, Long productId, Long count) {
        // 1. 상품 정보 조회
        Map<String, Object> product = catalogClient.getProduct(productId);

        // 2. 결제 수단 조회
        Map<String, Object> paymentMethod = paymentClient.getProductMethod(userId);

        // 3. 배송지 조회
        Map<String, Object> address = deliveryClient.getUserAddress(userId);

        ProductOrder order = ProductOrder.builder()
                .userId(userId)
                .productId(productId)
                .count(count)
                .orderStatus(OrderStatus.INITIATED)
                .paymentId(null)
                .deliveryId(null)
                .build();

        // 4. 주문 생성
        orderRepository.save(order);

        StartOrderResponseDto startOrderDto = new StartOrderResponseDto(
                order.getId(),
                paymentMethod,
                address
        );

        return startOrderDto;
    }

    public ProductOrder finishOrder(Long orderId, Long paymentMethodId, Long addressId) {
        // 1. 상품 주문 정보 조회
        ProductOrder order = orderRepository.findById(orderId)
                .orElseThrow();

        // 2. 결제 처리
        Map<String, Object> product = catalogClient.getProduct(order.getProductId());

        Map<String, Object> payment = paymentClient.processPayment(
                new ProcessPaymentDto(
                        order.getId(),
                        order.getUserId(),
                        Long.parseLong(product.get("price").toString()) * order.getCount(),
                        paymentMethodId
                ));

        // 3. 배송 처리
        Map<String, Object> address = deliveryClient.getAddress(addressId);

        Map<String, Object> delivery = deliveryClient.processDelivery(
                new ProcessDeliveryDto(
                        order.getId(),
                        product.get("name").toString(),
                        order.getCount(),
                        address.get("address").toString()
                )
        );

        // 4. 상품 재고 감소
        catalogClient.decreaseStockCount(
                order.getProductId(),
                new DecreaseStockCountDto(
                        order.getCount()
                )
        );

        // 5. 주문 정보 업데이트
        order = order.builder()
                .orderStatus(OrderStatus.DELIVERY_REQUESTED)
                .paymentId(Long.parseLong(payment.get("id").toString()))
                .deliveryId(Long.parseLong(delivery.get("id").toString()))
                .build();

        return orderRepository.save(order);
    }

    public List<ProductOrder> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public ProductOrderDetailDto getOrderDetail(Long orderId) {
        ProductOrder order = orderRepository.findById(orderId)
                .orElseThrow();

        Map<String, Object> payment = paymentClient.getPayment(order.getPaymentId());
        Map<String, Object> delivery = deliveryClient.getDelivery(order.getDeliveryId());

        return new ProductOrderDetailDto(
                order.getId(),
                order.getUserId(),
                order.getProductId(),
                order.getCount(),
                order.getOrderStatus().toString(),
                order.getPaymentId(),
                order.getDeliveryId(),
                payment.get("paymentStatus").toString(),
                delivery.get("status").toString()
        );
    }
}
