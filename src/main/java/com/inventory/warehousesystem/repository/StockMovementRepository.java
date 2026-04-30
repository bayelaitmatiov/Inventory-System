package com.inventory.warehousesystem.repository;

import com.inventory.warehousesystem.model.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    List<StockMovement> findByProductId(Long productId);

    List<StockMovement> findByType(String type);
}

