package com.rce.shop;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CartService {
    public record Line(Long productId, String name, BigDecimal price, int quantity, BigDecimal subtotal) {}

    private final CartItemRepository cartRepo;
    private final ProductRepository productRepo;

    public CartService(CartItemRepository cartRepo, ProductRepository productRepo) {
        this.cartRepo = cartRepo;
        this.productRepo = productRepo;
    }

    public List<Line> lines(String user) {
        List<Line> out = new ArrayList<>();
        for (CartItem c : cartRepo.findByUsername(user)) {
            Product p = productRepo.findById(c.productId)
                    .orElseThrow(() -> new ApiException(404, "Product not found"));
            out.add(new Line(p.id, p.name, p.price, c.quantity, p.price.multiply(BigDecimal.valueOf(c.quantity))));
        }
        return out;
    }

    public BigDecimal total(List<Line> lines) {
        return lines.stream().map(Line::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void add(String user, Long productId, int qty) {
        if (productId == null || qty < 1) throw new ApiException(400, "Valid product and quantity required");
        Product p = productRepo.findById(productId)
                .orElseThrow(() -> new ApiException(404, "Product not found"));
        CartItem c = cartRepo.findByUsernameAndProductId(user, productId).orElseGet(() -> {
            CartItem n = new CartItem();
            n.username = user;
            n.productId = productId;
            n.quantity = 0;
            return n;
        });
        if (c.quantity + qty > p.stock) throw new ApiException(400, "Only " + p.stock + " in stock for " + p.name);
        c.quantity += qty;
        cartRepo.save(c);
    }

    public void clear(String user) {
        cartRepo.deleteAll(cartRepo.findByUsername(user));
    }
}
