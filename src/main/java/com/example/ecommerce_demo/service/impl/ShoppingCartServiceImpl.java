package com.example.ecommerce_demo.service.impl;

import com.example.ecommerce_demo.entity.CartItem;
import com.example.ecommerce_demo.entity.Product;
import com.example.ecommerce_demo.entity.ShoppingCart;
import com.example.ecommerce_demo.entity.User;
import com.example.ecommerce_demo.repository.CartItemRepository;
import com.example.ecommerce_demo.repository.CartRepository;
import com.example.ecommerce_demo.repository.UserRepository;
import com.example.ecommerce_demo.service.ShoppingCartService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@Transactional
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    // 获取或创建用户的购物车
    public ShoppingCart getOrCreate(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    ShoppingCart newCart = new ShoppingCart();
                    User user =  userRepository.findById(userId).get();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
    }

    // 加入购物车
    public void addToCart(Long userId, Long productId, Integer quantity) {
        ShoppingCart cart = getOrCreate(userId);

        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);
        if (existingItem.isPresent()) {
            // 更新数量
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            // 新增
            Product product = new Product();
            product.setId(productId);
            CartItem newItem = new CartItem(cart, product, quantity);
            cartItemRepository.save(newItem);
        }
    }

    // 获取购物车总件数（用于首页显示）
    public int getCartItemCount(Long userId) {
        ShoppingCart cart = cartRepository.findByUserId(userId).orElse(null);
        if (cart == null) return 0;
        return cart.getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    // 获取完整购物车（用于 /cart 页面）
    public ShoppingCart getCartWithItems(Long userId) {
        return cartRepository.findByUserId(userId)
                .map(cart -> {
                    cart.getItems().size();
                    return cart;
                })
                .orElse(null);
    }

    public void removeCartItem(Long userId, Long cartItemId) {
        ShoppingCart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("购物车不存在"));

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("购物车项不存在"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("无权删除该商品");
        }

        cartItemRepository.delete(item);
    }

    public void clearCart(Long userId) {
        ShoppingCart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("购物车不存在"));

        cart.getItems().clear(); // 因为配置了 orphanRemoval = true，会自动删除
        cartRepository.save(cart);
    }
}
