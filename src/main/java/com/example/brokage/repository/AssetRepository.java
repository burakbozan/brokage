package com.example.brokage.repository;

import com.example.brokage.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {

    Asset findByCustomerIdAndAssetName(Long customerId, String assetName);

    List<Asset> findByCustomerId(Long customerId);
}
