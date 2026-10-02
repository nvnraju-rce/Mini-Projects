package com.rce.shop;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Named ShopOrder because ORDER is a reserved SQL word. Status: CREATED, PAYMENT_PENDING, PAID, PAYMENT_FAILED */
@Entity
@Table(name = "orders")
public class ShopOrder {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String orderNumber;
    public String username;
    public BigDecimal total;
    public String status;
    public LocalDateTime createdAt;
}
