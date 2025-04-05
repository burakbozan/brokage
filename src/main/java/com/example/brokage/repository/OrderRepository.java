package com.example.brokage.repository;

import com.example.brokage.model.Order;
import com.example.brokage.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerIdAndCreateDateBetween(Long customerId, LocalDateTime startDate, LocalDateTime endDate);
    Optional<Order> findByIdAndStatus(Long orderId, Status status);
    Optional<Order> findByIdAndCustomerId(Long orderId, Long customerId);

    List<Order> findByStatus(Status status);
}
