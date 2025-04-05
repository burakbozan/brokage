package com.example.brokage;

import com.example.brokage.model.*;
import com.example.brokage.repository.AssetRepository;
import com.example.brokage.repository.CustomerRepository;
import com.example.brokage.repository.OrderRepository;
import com.example.brokage.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private OrderService orderService;

    private Customer testCustomer;
    private Asset tryAsset;
    private Asset stockAsset;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setUsername("testuser");
        tryAsset = new Asset();
        tryAsset.setCustomerId(1L);
        tryAsset.setAssetName("TRY");
        tryAsset.setSize(10000L);
        tryAsset.setUsableSize(10000L);
        stockAsset = new Asset();
        stockAsset.setCustomerId(1L);
        stockAsset.setAssetName("ABC");
        stockAsset.setSize(100L);
        stockAsset.setUsableSize(100L);
    }

    @Test
    void createBuyOrder_sufficientBalance_orderCreatedAndBalancesUpdated() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(assetRepository.findByCustomerIdAndAssetName(1L, "TRY")).thenReturn(tryAsset);
        when(assetRepository.findByCustomerIdAndAssetName(1L, "XYZ")).thenReturn(null);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(assetRepository.save(any(Asset.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BigDecimal price = BigDecimal.TEN;
        Long size = 100L;
        Order createdOrder = orderService.createOrder(1L, "XYZ", Side.BUY, size, price);

        assertNotNull(createdOrder);
        assertEquals(1L, createdOrder.getCustomerId());
        assertEquals("XYZ", createdOrder.getAssetName());
        assertEquals(Side.BUY, createdOrder.getOrderSide());
        assertEquals(size, createdOrder.getSize());
        assertEquals(price, createdOrder.getPrice());
        assertEquals(Status.PENDING, createdOrder.getStatus());
        assertNotNull(createdOrder.getCreateDate());
        assertEquals(10000L - (size * price.longValue()), tryAsset.getUsableSize());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(assetRepository, times(1)).save(tryAsset);
    }

    @Test
    void createBuyOrder_insufficientBalance_throwsException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(assetRepository.findByCustomerIdAndAssetName(1L, "TRY")).thenReturn(tryAsset);

        BigDecimal price = BigDecimal.valueOf(100);
        Long size = 101L; // Will require 10100 TRY

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                orderService.createOrder(1L, "XYZ", Side.BUY, size, price));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Insufficient TRY balance.", exception.getReason());
        verify(orderRepository, never()).save(any(Order.class));
        verify(assetRepository, never()).save(any(Asset.class));
    }
}
