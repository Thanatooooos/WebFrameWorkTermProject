package com.example.ecommerce_demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Data
@Entity
@Table(name = "cart")
public class ShoppingCart {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

   LocalDateTime createdAt;

   LocalDateTime updatedAt;
}
