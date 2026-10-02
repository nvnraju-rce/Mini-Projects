package com.rce.shop;

import jakarta.persistence.*;

@Entity
public class CartItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String username;
    public Long productId;
    public int quantity;
}
