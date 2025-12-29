package com.example.ecommerce_demo.service;

import com.example.ecommerce_demo.entity.ShoppingCart;

public interface ShoppingCartService {

    ShoppingCart getOrCreate(Long userId);

    void addToCart(Long userId,Long productId, Integer quantity);

    int getCartItemCount(Long userId);

    ShoppingCart getCartWithItems(Long userId);

    void removeCartItem(Long userId, Long cartItemId);

    void clearCart(Long userId);
}
