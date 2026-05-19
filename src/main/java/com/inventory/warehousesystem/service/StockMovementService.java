package com.inventory.warehousesystem.service;

import com.inventory.warehousesystem.model.Product;
import com.inventory.warehousesystem.model.StockMovement;
import com.inventory.warehousesystem.model.Warehouse;
import com.inventory.warehousesystem.repository.ProductRepository;
import com.inventory.warehousesystem.repository.StockMovementRepository;
import com.inventory.warehousesystem.repository.WarehouseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StockMovementService {
    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final StockLevelService stockLevelService;

    public StockMovementService(StockMovementRepository stockMovementRepository,
                                ProductRepository productRepository,
                                WarehouseRepository warehouseRepository,
                                StockLevelService stockLevelService) {
        this.stockMovementRepository = stockMovementRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
        this.stockLevelService = stockLevelService;
    }

    public List<StockMovement> getAllMovements() {
        return stockMovementRepository.findAll();
    }

    public List<StockMovement> getMovementsByProduct(Long productId) {
        return stockMovementRepository.findByProductId(productId);
    }

    @Transactional
    public StockMovement addMovement(StockMovement movement) {
        if (movement.getProduct() == null || movement.getProduct().getId() == null) {
            throw new RuntimeException("Product is required");
        }
        if (movement.getWarehouse() == null || movement.getWarehouse().getId() == null) {
            throw new RuntimeException("Warehouse is required");
        }

        Product product = productRepository.findById(movement.getProduct().getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Warehouse warehouse = warehouseRepository.findById(movement.getWarehouse().getId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        String type = movement.getType();
        if (type == null) {
            throw new RuntimeException("Movement type is required");
        }

        int quantity = movement.getQuantity() == null ? 0 : movement.getQuantity();
        String normalizedType = type.trim().toUpperCase();

        int delta;
        if ("IN".equals(normalizedType)) {
            delta = quantity;
        } else if ("OUT".equals(normalizedType)) {
            delta = -quantity;
        } else {
            throw new RuntimeException("Invalid movement type");
        }

        stockLevelService.adjustStock(product, warehouse, delta);

        movement.setProduct(product);
        movement.setWarehouse(warehouse);
        movement.setType(normalizedType);
        movement.setDate(LocalDateTime.now());

        return stockMovementRepository.save(movement);
    }
}
