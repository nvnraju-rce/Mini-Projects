package com.rce.shop;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/** Loads sample products at startup (the H2 database is in memory). */
@Component
public class DataLoader implements CommandLineRunner {
    private final ProductRepository repo;
    public DataLoader(ProductRepository repo) { this.repo = repo; }

    @Override
    public void run(String... args) {
        if (repo.count() > 0) return;
        repo.saveAll(List.of(
            new Product("Laptop", "14 inch, 16 GB RAM, 512 GB SSD", new BigDecimal("55000.00"), 10),
            new Product("Smartphone", "6.5 inch display, 128 GB storage", new BigDecimal("18999.00"), 25),
            new Product("Headphones", "Wireless, noise cancelling", new BigDecimal("3499.00"), 40),
            new Product("Keyboard", "Mechanical, backlit", new BigDecimal("2499.00"), 30),
            new Product("Mouse", "Wireless ergonomic mouse", new BigDecimal("799.00"), 60),
            new Product("Backpack", "Water resistant, laptop sleeve", new BigDecimal("1599.00"), 35),
            new Product("Smartwatch", "Heart-rate and sleep tracking", new BigDecimal("4999.00"), 20),
            new Product("Power Bank", "20000 mAh fast charging", new BigDecimal("1899.00"), 50)
        ));
    }
}
