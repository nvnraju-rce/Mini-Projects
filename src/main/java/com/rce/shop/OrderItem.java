package com.rce.shop;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String orderNumber;
    public Long productId;
    public String productName;
    public BigDecimal price;
    public int quantity;
}
