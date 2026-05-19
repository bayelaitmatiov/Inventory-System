package com.inventory.warehousesystem.repository;

import com.inventory.warehousesystem.model.StockLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockLevelRepository extends JpaRepository<StockLevel, Long> {
    Optional<StockLevel> findByProductIdAndWarehouseId(Long productId, Long warehouseId);

    List<StockLevel> findByProductId(Long productId);

    List<StockLevel> findByWarehouseId(Long warehouseId);
}

