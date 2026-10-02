package com.rce.shop;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByOrderNumberOrderByCreatedAtAsc(String orderNumber);
}
