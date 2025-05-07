package com.example.OrderService.service;

import com.example.OrderService.dto.StartOrderResponseDto;
import com.example.OrderService.entity.ProductOrder;
import com.example.OrderService.enums.OrderStatus;
import com.example.OrderService.feign.CatalogClient;
import com.example.OrderService.feign.DeliveryClient;
import com.example.OrderService.feign.PaymentClient;
import com.example.OrderService.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest
class OrderServiceTest {

    @Autowired
    OrderRepository orderRepository;

    @MockitoBean
    PaymentClient paymentClient;

    @MockitoBean
    DeliveryClient deliveryClient;

    @MockitoBean
    CatalogClient catalogClient;

    @Autowired
    OrderService orderService;

    @Test
    void startOrderTest() {
        // given
        Map<String, Object> paymentMethodRes = new HashMap<>();
        Map<String, Object> userAddressRes = new HashMap<>();
        paymentMethodRes.put("paymentMethodType", "CREDIT_CARD");
        userAddressRes.put("address", "서울시 중구");

        when(catalogClient.getProduct(1L)).thenReturn(Map.of("productId", 1L, "price", 10000));
        when(paymentClient.getProductMethod(1L)).thenReturn(paymentMethodRes);
        when(deliveryClient.getUserAddress(1L)).thenReturn(userAddressRes);

        // when
        StartOrderResponseDto response = orderService.startOrder(1L, 1L, 2L);

        // then
        assertNotNull(response.orderId());
        assertEquals(paymentMethodRes, response.paymentMethod());
        assertEquals(userAddressRes, response.address());

        ProductOrder order = orderRepository.findById(response.orderId()).orElseThrow();
        assertEquals(OrderStatus.INITIATED, order.getOrderStatus());
    }

}
