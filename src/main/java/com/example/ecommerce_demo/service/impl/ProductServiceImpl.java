package com.example.ecommerce_demo.service.impl;

import com.example.ecommerce_demo.dto.Result;
import com.example.ecommerce_demo.entity.Product;
import com.example.ecommerce_demo.repository.ProductRepository;
import com.example.ecommerce_demo.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Result getProducts(){
        return Result.ok(productRepository.findAll());
    }

    @Override
    public List<Product> findAll(){
        return productRepository.findAll();
    }

    @Override
    public Product findById(Long id){
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product with ID " + id + " not found"));
    }
}
