-- 创建数据库（可选，如果你的 docker-compose 或 MySQL 已自动创建 ecommerce_db）
-- CREATE DATABASE IF NOT EXISTS ecommerce_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
CREATE DATABASE IF NOT EXISTS ecommerce_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE ecommerce_db;

-- 删除已存在的表（避免重复初始化）
DROP TABLE IF EXISTS products;

-- 创建商品表
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    image VARCHAR(500),
    description TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,          -- 存储 BCrypt 加密后的密码
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE if not exists cart(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,  -- 一对一：一个用户一个购物车
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 购物车项表（购物车里的商品）
CREATE TABLE cart_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    cart_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1 CHECK (quantity > 0),
    added_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cart_id) REFERENCES cart(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id),
    UNIQUE KEY unique_cart_product (cart_id, product_id)  -- 防止重复添加同一商品
);

-- 插入示例商品数据
INSERT INTO products (name, price, image, description) VALUES
('iPhone 15 Pro', 8999.00, '/images/iphone15.jpg', 'Apple''s latest flagship smartphone powered by the A17 chip'),
('Samsung Galaxy S24', 7499.00, '/images/s24.jpg', 'Samsung AI smartphone with real-time translation support'),
('MacBook Air M2', 9499.00, '/images/macbook-air.jpg', 'Ultra-thin, portable, and powerful laptop'),
('AirPods Pro', 1899.00, '/images/airpods-pro.jpg', 'Active Noise Cancellation, Spatial Audio, and Transparency mode'),
('Xiaomi Smart Desk Lamp', 199.00, '/images/xiaomi-lamp.jpg', 'Adjustable brightness and color temperature with minimalist design');