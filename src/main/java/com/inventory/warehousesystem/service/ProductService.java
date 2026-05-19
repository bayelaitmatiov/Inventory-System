package com.inventory.warehousesystem.service;

import com.inventory.warehousesystem.model.Product;
import com.inventory.warehousesystem.repository.ProductRepository;
import com.inventory.warehousesystem.repository.StockLevelRepository;
import com.inventory.warehousesystem.repository.StockMovementRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final StockLevelRepository stockLevelRepository;
    private final StockMovementRepository stockMovementRepository;

    public ProductService(ProductRepository productRepository,
                          StockLevelRepository stockLevelRepository,
                          StockMovementRepository stockMovementRepository) {
        this.productRepository = productRepository;
        this.stockLevelRepository = stockLevelRepository;
        this.stockMovementRepository = stockMovementRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product product) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        existing.setName(product.getName());
        existing.setDescription(product.getDescription());
        existing.setPrice(product.getPrice());
        existing.setQuantity(product.getQuantity());
        existing.setCategory(product.getCategory());

        return productRepository.save(existing);
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found");
        }
        if (stockLevelRepository.countByProductId(id) > 0) {
            throw new RuntimeException("Cannot delete product: stock levels still exist");
        }
        if (stockMovementRepository.countByProductId(id) > 0) {
            throw new RuntimeException("Cannot delete product: stock movements still exist");
        }
        productRepository.deleteById(id);
    }

    public List<Product> getLowStockProducts(int threshold) {
        return productRepository.findByQuantityLessThan(threshold);
    }
}
