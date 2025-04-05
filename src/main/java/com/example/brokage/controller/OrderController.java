package com.example.brokage.controller;

import com.example.brokage.model.Asset;
import com.example.brokage.model.Customer;
import com.example.brokage.model.Order;
import com.example.brokage.model.Side;
import com.example.brokage.repository.CustomerRepository;
import com.example.brokage.service.OrderService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
@PreAuthorize("isAuthenticated()")
public class OrderController {

    private final OrderService orderService;
    private final CustomerRepository customerRepository;

    public OrderController(OrderService orderService, CustomerRepository customerRepository) {
        this.orderService = orderService;
        this.customerRepository = customerRepository;
    }

    private Long getCurrentCustomerId(UserDetails userDetails) {
        if (userDetails != null) {
            return customerRepository.findByUsername(userDetails.getUsername())
                    .map(Customer::getId) // If Customer is present, get its ID
                    .orElse(null);        // If Customer is not present (Optional is empty), return null
        }
        return null;
    }

    @PostMapping("/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Order> createOrder(
            @RequestParam Long customerId,
            @RequestParam String assetName,
            @RequestParam Side side,
            @RequestParam Long size,
            @RequestParam BigDecimal price) {
        return ResponseEntity.ok(orderService.createOrder(customerId, assetName, side, size, price));
    }

    @GetMapping("/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Order>> listOrders(
            @RequestParam Long customerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(orderService.listOrders(customerId, startDate, endDate));
    }

    @DeleteMapping("/orders/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId, @RequestParam Long customerId) {
        orderService.deleteOrder(customerId, orderId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/assets")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Asset>> listAssets(@RequestParam Long customerId) {
        return ResponseEntity.ok(orderService.listAssets(customerId));
    }

    @PostMapping("/admin/match/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> matchOrder(@PathVariable Long orderId) {
        orderService.matchOrder(orderId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/orders/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Order>> listPendingOrders() {
        List<Order> pendingOrders = orderService.listPendingOrders();
        return ResponseEntity.ok(pendingOrders);
    }

    @GetMapping("/admin/orders/matched")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Order>> listMatchedOrders() {
        List<Order> matchedOrders = orderService.listMatchedOrders();
        return ResponseEntity.ok(matchedOrders);
    }

    // Bonus 1: Customer specific endpoints
    @PostMapping("/customer/orders")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Order> createCustomerOrder(
            @RequestParam String assetName,
            @RequestParam Side side,
            @RequestParam Long size,
            @RequestParam BigDecimal price,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long customerId = getCurrentCustomerId(userDetails);
        if (customerId == null) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        return ResponseEntity.ok(orderService.createOrder(customerId, assetName, side, size, price));
    }

    @GetMapping("/customer/orders")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<Order>> listCustomerOrders(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long customerId = getCurrentCustomerId(userDetails);
        if (customerId == null) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        return ResponseEntity.ok(orderService.listOrders(customerId, startDate, endDate));
    }

    @DeleteMapping("/customer/orders/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> deleteCustomerOrder(@PathVariable Long orderId, @AuthenticationPrincipal UserDetails userDetails) {
        Long customerId = getCurrentCustomerId(userDetails);
        if (customerId == null) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        orderService.deleteOrder(customerId, orderId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/customer/assets")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<Asset>> listCustomerAssets(@AuthenticationPrincipal UserDetails userDetails) {
        Long customerId = getCurrentCustomerId(userDetails);
        if (customerId == null) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        return ResponseEntity.ok(orderService.listAssets(customerId));
    }

}
