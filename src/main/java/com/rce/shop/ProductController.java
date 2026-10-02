package com.rce.shop;

import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductRepository repo;
    public ProductController(ProductRepository repo) { this.repo = repo; }

    @GetMapping
    public List<Product> all() { return repo.findAll(); }
}
