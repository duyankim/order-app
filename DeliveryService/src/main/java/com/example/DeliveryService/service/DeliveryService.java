package com.example.DeliveryService.service;

import com.example.DeliveryService.dg.DeliveryAdapter;
import com.example.DeliveryService.entity.Delivery;
import com.example.DeliveryService.entity.UserAddress;
import com.example.DeliveryService.enums.DeliveryStatus;
import com.example.DeliveryService.repository.DeliveryRepository;
import com.example.DeliveryService.repository.UserAddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeliveryService {

    @Autowired
    private UserAddressRepository userAddressRepository;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private DeliveryAdapter deliveryAdapter;

    public UserAddress addUserAddress(Long userId, String address, String alias) {
        UserAddress userAddress = UserAddress.builder()
                .userId(userId)
                .address(address)
                .alias(alias)
                .build();
        return userAddressRepository.save(userAddress);
    }

    public Delivery processDelivery(
            Long orderId,
            String productName,
            Long productCount,
            String address
    ) {
        Long referenceCode = deliveryAdapter.processDelivery(
                productName,
                productCount,
                address
        );

        Delivery delivery = Delivery.builder()
                .orderId(orderId)
                .productName(productName)
                .productCount(productCount)
                .address(address)
                .deliveryStatus(DeliveryStatus.REQUESTED)
                .referenceCode(referenceCode)
                .build();

        return deliveryRepository.save(delivery);
    }

    public Delivery getDelivery(Long deliveryId) {
        return deliveryRepository.findAllByOrderId(deliveryId)
                .stream()
                .findFirst()
                .orElseThrow();
    }

    public UserAddress getAddress(Long addressId) {
        return userAddressRepository.findById(addressId)
                .orElseThrow();
    }

    public UserAddress getUserAddress(Long userId) {
        return userAddressRepository.findByUserId(userId)
                .stream()
                .findFirst()
                .orElseThrow();
    }
}
