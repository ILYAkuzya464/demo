package com.example.demo.services;

import com.example.demo.model.Product;

public interface ProductService {
    Product getProductById(Long id);
    Product addProduct(Product product);
    void deleteById(Long id);
}
