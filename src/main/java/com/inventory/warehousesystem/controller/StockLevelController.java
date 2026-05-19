package com.inventory.warehousesystem.controller;

import com.inventory.warehousesystem.model.StockLevel;
import com.inventory.warehousesystem.service.StockLevelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stock-levels")
@CrossOrigin(origins = "*")
public class StockLevelController {
    private final StockLevelService stockLevelService;

    public StockLevelController(StockLevelService stockLevelService) {
        this.stockLevelService = stockLevelService;
    }

    @GetMapping
    public ResponseEntity<List<StockLevel>> getAll() {
        return ResponseEntity.ok(stockLevelService.getAll());
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<StockLevel>> getByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(stockLevelService.getByProduct(productId));
    }

    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<List<StockLevel>> getByWarehouse(@PathVariable Long warehouseId) {
        return ResponseEntity.ok(stockLevelService.getByWarehouse(warehouseId));
    }

    @PostMapping
    public ResponseEntity<StockLevel> upsert(@RequestBody StockLevel stockLevel) {
        StockLevel saved = stockLevelService.upsert(stockLevel);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        stockLevelService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

