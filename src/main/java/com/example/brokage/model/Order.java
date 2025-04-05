package com.example.brokage.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "stock_order") // Avoid reserved keyword 'order'
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long customerId;
    private String assetName;
    @Enumerated(EnumType.STRING)
    private Side orderSide;
    private Long size;
    private BigDecimal price;
    @Enumerated(EnumType.STRING)
    private Status status;
    private LocalDateTime createDate;

    public Order() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public Side getOrderSide() {
        return orderSide;
    }

    public void setOrderSide(Side orderSide) {
        this.orderSide = orderSide;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id) && Objects.equals(customerId, order.customerId) && Objects.equals(assetName, order.assetName) && orderSide == order.orderSide && Objects.equals(size, order.size) && Objects.equals(price, order.price) && status == order.status && Objects.equals(createDate, order.createDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customerId, assetName, orderSide, size, price, status, createDate);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", assetName='" + assetName + '\'' +
                ", orderSide=" + orderSide +
                ", size=" + size +
                ", price=" + price +
                ", status=" + status +
                ", createDate=" + createDate +
                '}';
    }
}
