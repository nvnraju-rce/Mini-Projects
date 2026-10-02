package com.rce.shop;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String name;
    public String description;
    public BigDecimal price;
    public int stock;

    public Product() {}
    public Product(String name, String description, BigDecimal price, int stock) {
        this.name = name; this.description = description; this.price = price; this.stock = stock;
    }
}
