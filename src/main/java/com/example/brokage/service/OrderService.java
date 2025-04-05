package com.example.brokage.service;

import com.example.brokage.model.*;
import com.example.brokage.repository.AssetRepository;
import com.example.brokage.repository.CustomerRepository;
import com.example.brokage.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final AssetRepository assetRepository;
    private final CustomerRepository customerRepository;

    public OrderService(OrderRepository orderRepository, AssetRepository assetRepository, CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.assetRepository = assetRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public Order createOrder(Long customerId, String assetName, Side side, Long size, BigDecimal price) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found."));

        Asset tryAsset = assetRepository.findByCustomerIdAndAssetName(customerId, "TRY");
        Asset orderAsset = assetRepository.findByCustomerIdAndAssetName(customerId, assetName);

        if (side == Side.BUY) {
            if (tryAsset == null || tryAsset.getUsableSize() < size.longValue() * price.longValue()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient TRY balance.");
            }
            tryAsset.setUsableSize(tryAsset.getUsableSize() - size.longValue() * price.longValue());
            assetRepository.save(tryAsset);
        } else if (side == Side.SELL) {
            if (orderAsset == null || orderAsset.getUsableSize() < size) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient " + assetName + " balance.");
            }
            orderAsset.setUsableSize(orderAsset.getUsableSize() - size);
            assetRepository.save(orderAsset);
        }

        Order order = new Order();
        order.setCustomerId(customerId);
        order.setAssetName(assetName);
        order.setOrderSide(side);
        order.setSize(size);
        order.setPrice(price);
        order.setStatus(Status.PENDING);
        order.setCreateDate(LocalDateTime.now());
        return orderRepository.save(order);
    }

    public List<Order> listOrders(Long customerId, LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByCustomerIdAndCreateDateBetween(customerId, startDate, endDate);
    }

    @Transactional
    public void deleteOrder(Long customerId, Long orderId) {
        Order order = orderRepository.findByIdAndCustomerId(orderId, customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pending order not found for this customer."));

        if (order.getStatus() != Status.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only pending orders can be canceled.");
        }

        Asset tryAsset = assetRepository.findByCustomerIdAndAssetName(customerId, "TRY");
        Asset orderAsset = assetRepository.findByCustomerIdAndAssetName(customerId, order.getAssetName());

        if (order.getOrderSide() == Side.BUY) {
            if (tryAsset != null) {
                tryAsset.setUsableSize(tryAsset.getUsableSize() + order.getSize() * order.getPrice().longValue());
                assetRepository.save(tryAsset);
            }
        } else if (order.getOrderSide() == Side.SELL) {
            if (orderAsset != null) {
                orderAsset.setUsableSize(orderAsset.getUsableSize() + order.getSize());
                assetRepository.save(orderAsset);
            }
        }

        order.setStatus(Status.CANCELED);
        orderRepository.save(order);
    }

    public List<Asset> listAssets(Long customerId) {
        return assetRepository.findByCustomerId(customerId);
    }

    @Transactional
    public void matchOrder(Long orderId) {
        Order order = orderRepository.findByIdAndStatus(orderId, Status.PENDING)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pending order not found."));

        Long customerId = order.getCustomerId();
        String assetName = order.getAssetName();
        Side side = order.getOrderSide();
        Long size = order.getSize();
        BigDecimal price = order.getPrice();

        Asset tryAsset = assetRepository.findByCustomerIdAndAssetName(customerId, "TRY");
        Asset orderAsset = assetRepository.findByCustomerIdAndAssetName(customerId, assetName);

        if (side == Side.BUY) {
            if (tryAsset == null || tryAsset.getSize() < size.longValue() * price.longValue()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient TRY balance for matching.");
            }
            if (orderAsset == null) {
                orderAsset = new Asset();
                orderAsset.setCustomerId(customerId);
                orderAsset.setAssetName(assetName);
                orderAsset.setSize(0L);
                orderAsset.setUsableSize(0L);
            }
            tryAsset.setSize(tryAsset.getSize() - size.longValue() * price.longValue());
            orderAsset.setSize(orderAsset.getSize() + size);
            orderAsset.setUsableSize(orderAsset.getUsableSize() + size);
            assetRepository.save(tryAsset);
            assetRepository.save(orderAsset);
        } else if (side == Side.SELL) {
            if (orderAsset == null || orderAsset.getSize() < size) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient " + assetName + " balance for matching.");
            }
            if (tryAsset == null) {
                tryAsset = new Asset();
                tryAsset.setCustomerId(customerId);
                tryAsset.setAssetName("TRY");
                tryAsset.setSize(0L);
                tryAsset.setUsableSize(0L);
            }
            orderAsset.setSize(orderAsset.getSize() - size);
            tryAsset.setSize(tryAsset.getSize() + size.longValue() * price.longValue());
            tryAsset.setUsableSize(tryAsset.getUsableSize() + size.longValue() * price.longValue());
            assetRepository.save(tryAsset);
            assetRepository.save(orderAsset);
        }

        order.setStatus(Status.MATCHED);
        orderRepository.save(order);
    }

    public List<Order> listPendingOrders() {
        return orderRepository.findByStatus(Status.PENDING);
    }

    public List<Order> listMatchedOrders() {
        return orderRepository.findByStatus(Status.MATCHED);
    }

}
