package com.rce.shop;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    public record PayRequest(String cardNumber) {}

    private final AuthService auth;
    private final OrderService service;

    public OrderController(AuthService auth, OrderService service) {
        this.auth = auth;
        this.service = service;
    }

    @PostMapping("/checkout")
    public Map<String, Object> checkout(@RequestHeader(value = AuthService.HEADER, required = false) String token,
                                        @RequestBody PayRequest r) {
        String user = auth.userFor(token);
        ShopOrder o = service.checkout(user, r.cardNumber());
        return service.receipt(user, o.orderNumber);
    }

    @GetMapping
    public List<ShopOrder> list(@RequestHeader(value = AuthService.HEADER, required = false) String token) {
        return service.list(auth.userFor(token));
    }

    @GetMapping("/{orderNumber}/receipt")
    public Map<String, Object> receipt(@RequestHeader(value = AuthService.HEADER, required = false) String token,
                                       @PathVariable String orderNumber) {
        return service.receipt(auth.userFor(token), orderNumber);
    }

    @PostMapping("/{orderNumber}/pay")
    public Map<String, Object> retry(@RequestHeader(value = AuthService.HEADER, required = false) String token,
                                     @PathVariable String orderNumber, @RequestBody PayRequest r) {
        String user = auth.userFor(token);
        service.retry(user, orderNumber, r.cardNumber());
        return service.receipt(user, orderNumber);
    }
}
