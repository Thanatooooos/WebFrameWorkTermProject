package com.example.ecommerce_demo.service;

import com.example.ecommerce_demo.dto.Result;
import com.example.ecommerce_demo.entity.Product;

import java.util.List;

public interface ProductService {
    Result getProducts();

    List<Product> findAll();

    Product findById(Long i);
}
