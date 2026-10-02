package com.rce.shop;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    public record AddRequest(Long productId, int quantity) {}

    private final AuthService auth;
    private final CartService cart;

    public CartController(AuthService auth, CartService cart) {
        this.auth = auth;
        this.cart = cart;
    }

    @GetMapping
    public Map<String, Object> view(@RequestHeader(value = AuthService.HEADER, required = false) String token) {
        List<CartService.Line> lines = cart.lines(auth.userFor(token));
        return Map.of("items", lines, "total", cart.total(lines));
    }

    @PostMapping
    public Map<String, String> add(@RequestHeader(value = AuthService.HEADER, required = false) String token,
                                   @RequestBody AddRequest r) {
        cart.add(auth.userFor(token), r.productId(), r.quantity());
        return Map.of("message", "Added to cart");
    }

    @DeleteMapping
    public Map<String, String> clear(@RequestHeader(value = AuthService.HEADER, required = false) String token) {
        cart.clear(auth.userFor(token));
        return Map.of("message", "Cart cleared");
    }
}
