package com.rce.shop;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** One row per payment attempt. The full card number is never stored. */
@Entity
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String orderNumber;
    public String transactionId;
    public BigDecimal amount;
    public String status;
    public String message;
    public String cardLast4;
    public LocalDateTime createdAt;
}
