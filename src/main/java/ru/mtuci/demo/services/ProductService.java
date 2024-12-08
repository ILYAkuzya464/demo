package ru.mtuci.demo.services;

import ru.mtuci.demo.model.Product;

public interface ProductService {
    Product getProductById(Long id);
    Product addProduct(Product product);
    void deleteById(Long id);
}
