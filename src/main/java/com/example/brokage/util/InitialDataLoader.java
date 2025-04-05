package com.example.brokage.util;

import com.example.brokage.model.Asset;
import com.example.brokage.model.Customer;
import com.example.brokage.repository.AssetRepository;
import com.example.brokage.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class InitialDataLoader implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final AssetRepository assetRepository;
    private final PasswordEncoder passwordEncoder;

    public InitialDataLoader(CustomerRepository customerRepository, AssetRepository assetRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.assetRepository = assetRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        // Create a default admin user if not exists
        Optional<Customer> adminUserOptional = customerRepository.findByUsername("admin");
        Customer adminUser;
        if (adminUserOptional.isEmpty()) {
            adminUser = new Customer();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("adminpassword"));
            adminUser.setRole("ADMIN");
            customerRepository.save(adminUser);
        } else {
            adminUser = adminUserOptional.get();
        }

        // Create initial assets for the admin user if not exists
        createAssetIfNotFound(adminUser.getId(), "TRY", 1000000L, 1000000L);
        createAssetIfNotFound(adminUser.getId(), "ABC", 1000L, 1000L);
        createAssetIfNotFound(adminUser.getId(), "DEF", 500L, 500L);

        // Create a default regular user if not exists
        Optional<Customer> regularUserOptional = customerRepository.findByUsername("user");
        Customer regularUser;
        if (regularUserOptional.isEmpty()) {
            regularUser = new Customer();
            regularUser.setUsername("user");
            regularUser.setPassword(passwordEncoder.encode("userpassword"));
            regularUser.setRole("CUSTOMER");
            customerRepository.save(regularUser);
        } else {
            regularUser = regularUserOptional.get();
        }

        // Create initial assets for the regular user if not exists
        createAssetIfNotFound(regularUser.getId(), "TRY", 500000L, 500000L);
        createAssetIfNotFound(regularUser.getId(), "GHI", 200L, 200L);
    }

    private void createAssetIfNotFound(Long customerId, String assetName, Long size, Long usableSize) {
        Optional<Asset> existingAsset = Optional.ofNullable(assetRepository.findByCustomerIdAndAssetName(customerId, assetName));
        if (existingAsset.isEmpty()) {
            Asset newAsset = new Asset();
            newAsset.setCustomerId(customerId);
            newAsset.setAssetName(assetName);
            newAsset.setSize(size);
            newAsset.setUsableSize(usableSize);
            assetRepository.save(newAsset);
        }
    }
}
