package com.example.OrderService.controller;

import com.example.OrderService.dto.FinishOrderDto;
import com.example.OrderService.dto.ProductOrderDetailDto;
import com.example.OrderService.dto.StartOrderDto;
import com.example.OrderService.dto.StartOrderResponseDto;
import com.example.OrderService.entity.ProductOrder;
import com.example.OrderService.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/order/start-order")
    public StartOrderResponseDto startOrder(@RequestBody StartOrderDto startOrderDto) {
        // 주문 시작 로직
        return orderService.startOrder(
                startOrderDto.userId(),
                startOrderDto.productId(),
                startOrderDto.count()
        );
    }

    @PostMapping("/order/finish-order")
    public void finishOrder(@RequestBody FinishOrderDto finishOrderDto) {
        // 주문 완료 로직
        orderService.finishOrder(
                finishOrderDto.orderId(),
                finishOrderDto.paymentMethodId(),
                finishOrderDto.addressId()
        );
    }

    @GetMapping("/order/users/{userId}/orders")
    public List<ProductOrder> getUserOrders(@PathVariable Long userId) {
        // 사용자 ID로 주문 조회 로직
        return orderService.getUserOrders(userId);
    }

    @GetMapping("/order/orders/{orderId}")
    public ProductOrderDetailDto getOrder(@PathVariable Long orderId) {
        // 주문 ID로 주문 조회 로직
        return orderService.getOrderDetail(orderId);
    }
}
