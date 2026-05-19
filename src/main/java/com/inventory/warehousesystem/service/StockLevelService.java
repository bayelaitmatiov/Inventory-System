package com.inventory.warehousesystem.service;

import com.inventory.warehousesystem.model.Product;
import com.inventory.warehousesystem.model.StockLevel;
import com.inventory.warehousesystem.model.Warehouse;
import com.inventory.warehousesystem.repository.ProductRepository;
import com.inventory.warehousesystem.repository.StockLevelRepository;
import com.inventory.warehousesystem.repository.WarehouseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StockLevelService {
    private final StockLevelRepository stockLevelRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    public StockLevelService(StockLevelRepository stockLevelRepository,
                             ProductRepository productRepository,
                             WarehouseRepository warehouseRepository) {
        this.stockLevelRepository = stockLevelRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
    }

    public List<StockLevel> getAll() {
        return stockLevelRepository.findAll();
    }

    public List<StockLevel> getByProduct(Long productId) {
        return stockLevelRepository.findByProductId(productId);
    }

    public List<StockLevel> getByWarehouse(Long warehouseId) {
        return stockLevelRepository.findByWarehouseId(warehouseId);
    }

    @Transactional
    public StockLevel upsert(StockLevel request) {
        if (request.getProduct() == null || request.getProduct().getId() == null) {
            throw new RuntimeException("Product is required");
        }
        if (request.getWarehouse() == null || request.getWarehouse().getId() == null) {
            throw new RuntimeException("Warehouse is required");
        }

        Product product = productRepository.findById(request.getProduct().getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouse().getId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        int quantity = request.getQuantity() == null ? 0 : request.getQuantity();

        StockLevel stockLevel = stockLevelRepository.findByProductIdAndWarehouseId(product.getId(), warehouse.getId())
                .orElseGet(StockLevel::new);

        stockLevel.setProduct(product);
        stockLevel.setWarehouse(warehouse);
        stockLevel.setQuantity(quantity);

        StockLevel saved = stockLevelRepository.save(stockLevel);
        syncProductQuantity(product.getId());
        return saved;
    }

    @Transactional
    public StockLevel adjustStock(Product product, Warehouse warehouse, int delta) {
        StockLevel stockLevel = stockLevelRepository.findByProductIdAndWarehouseId(product.getId(), warehouse.getId())
                .orElseGet(() -> {
                    StockLevel created = new StockLevel();
                    created.setProduct(product);
                    created.setWarehouse(warehouse);
                    created.setQuantity(0);
                    return created;
                });

        int current = stockLevel.getQuantity() == null ? 0 : stockLevel.getQuantity();
        int updated = current + delta;
        if (updated < 0) {
            throw new RuntimeException("Insufficient stock in warehouse");
        }

        Integer capacity = warehouse.getCapacity();
        if (capacity != null) {
            int warehouseTotal = stockLevelRepository.sumQuantityByWarehouseId(warehouse.getId());
            int projectedTotal = warehouseTotal - current + updated;
            if (projectedTotal > capacity) {
                throw new RuntimeException("Warehouse capacity exceeded");
            }
        }

        stockLevel.setQuantity(updated);
        StockLevel saved = stockLevelRepository.save(stockLevel);
        syncProductQuantity(product.getId());
        return saved;
    }

    @Transactional
    public void syncProductQuantity(Long productId) {
        int total = stockLevelRepository.sumQuantityByProductId(productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setQuantity(total);
        productRepository.save(product);
    }

    public void delete(Long id) {
        if (!stockLevelRepository.existsById(id)) {
            throw new RuntimeException("Stock level not found");
        }
        stockLevelRepository.deleteById(id);
    }
}
