package com.inventory.warehousesystem.service;

import com.inventory.warehousesystem.model.Product;
import com.inventory.warehousesystem.model.StockMovement;
import com.inventory.warehousesystem.repository.ProductRepository;
import com.inventory.warehousesystem.repository.StockMovementRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StockMovementService {
    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;

    public StockMovementService(StockMovementRepository stockMovementRepository, ProductRepository productRepository) {
        this.stockMovementRepository = stockMovementRepository;
        this.productRepository = productRepository;
    }

    public List<StockMovement> getAllMovements() {
        return stockMovementRepository.findAll();
    }

    public List<StockMovement> getMovementsByProduct(Long productId) {
        return stockMovementRepository.findByProductId(productId);
    }

    public StockMovement addMovement(StockMovement movement) {
        if (movement.getProduct() == null || movement.getProduct().getId() == null) {
            throw new RuntimeException("Product is required");
        }

        Product product = productRepository.findById(movement.getProduct().getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        String type = movement.getType();
        if (type == null) {
            throw new RuntimeException("Movement type is required");
        }

        int quantity = movement.getQuantity() == null ? 0 : movement.getQuantity();
        String normalizedType = type.trim().toUpperCase();

        if ("IN".equals(normalizedType)) {
            product.setQuantity(product.getQuantity() + quantity);
        } else if ("OUT".equals(normalizedType)) {
            int updatedQuantity = product.getQuantity() - quantity;
            if (updatedQuantity < 0) {
                throw new RuntimeException("Insufficient stock");
            }
            product.setQuantity(updatedQuantity);
        } else {
            throw new RuntimeException("Invalid movement type");
        }

        Product savedProduct = productRepository.save(product);

        movement.setProduct(savedProduct);
        movement.setType(normalizedType);
        movement.setDate(LocalDateTime.now());

        return stockMovementRepository.save(movement);
    }
}

