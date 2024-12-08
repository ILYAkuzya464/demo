package com.example.demo.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.model.Product;
import com.example.demo.repo.ProductRepository;
import com.example.demo.services.ProductService;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Продукт с ID " + id + " не найден"));
    }

    @Override
    public Product addProduct(Product product) {
        if (productRepository.existsByName(product.getName())) {
            throw new IllegalArgumentException("Продукт с таким именем уже существует");
        }
        return productRepository.save(product);
    }
}
