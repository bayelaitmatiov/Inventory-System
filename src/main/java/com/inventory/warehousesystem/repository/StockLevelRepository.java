package com.inventory.warehousesystem.repository;

import com.inventory.warehousesystem.model.StockLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockLevelRepository extends JpaRepository<StockLevel, Long> {
    Optional<StockLevel> findByProductIdAndWarehouseId(Long productId, Long warehouseId);

    List<StockLevel> findByProductId(Long productId);

    List<StockLevel> findByWarehouseId(Long warehouseId);

    @Query("select coalesce(sum(s.quantity), 0) from StockLevel s where s.product.id = :productId")
    int sumQuantityByProductId(@Param("productId") Long productId);

    @Query("select coalesce(sum(s.quantity), 0) from StockLevel s where s.warehouse.id = :warehouseId")
    int sumQuantityByWarehouseId(@Param("warehouseId") Long warehouseId);

    long countByProductId(Long productId);
}
