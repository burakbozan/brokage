package com.example.brokage.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long customerId;
    private String assetName;
    private Long size;
    private Long usableSize;

    public Asset() {
    }

    public Long id() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long customerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String assetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public Long size() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long usableSize() {
        return usableSize;
    }

    public void setUsableSize(Long usableSize) {
        this.usableSize = usableSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Asset asset = (Asset) o;
        return Objects.equals(id, asset.id) && Objects.equals(customerId, asset.customerId) && Objects.equals(assetName, asset.assetName) && Objects.equals(size, asset.size) && Objects.equals(usableSize, asset.usableSize);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customerId, assetName, size, usableSize);
    }

    @Override
    public String toString() {
        return "Asset{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", assetName='" + assetName + '\'' +
                ", size=" + size +
                ", usableSize=" + usableSize +
                '}';
    }
}
