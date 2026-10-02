package com.rce.shop;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopOrderRepository extends JpaRepository<ShopOrder, Long> {
    Optional<ShopOrder> findByOrderNumber(String orderNumber);
    List<ShopOrder> findByUsernameOrderByCreatedAtDesc(String username);
}
