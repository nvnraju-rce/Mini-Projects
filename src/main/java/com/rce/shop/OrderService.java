package com.rce.shop;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private final ShopOrderRepository orders;
    private final OrderItemRepository items;
    private final PaymentRepository payments;
    private final ProductRepository products;
    private final CartService cart;
    private final PaymentService gateway;

    public OrderService(ShopOrderRepository orders, OrderItemRepository items, PaymentRepository payments,
                        ProductRepository products, CartService cart, PaymentService gateway) {
        this.orders = orders; this.items = items; this.payments = payments;
        this.products = products; this.cart = cart; this.gateway = gateway;
    }

    /** Steps 3-12: checkout, total, create order, order id, call gateway, validate, store payment, update status. */
    @Transactional
    public ShopOrder checkout(String user, String card) {
        List<CartService.Line> lines = cart.lines(user);
        if (lines.isEmpty()) throw new ApiException(400, "Cart is empty");
        for (CartService.Line l : lines) {
            Product p = products.findById(l.productId()).orElseThrow(() -> new ApiException(404, "Product not found"));
            if (p.stock < l.quantity()) throw new ApiException(400, "Not enough stock for " + p.name);
        }
        ShopOrder o = new ShopOrder();
        o.orderNumber = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        o.username = user;
        o.total = cart.total(lines);
        o.status = "CREATED";
        o.createdAt = LocalDateTime.now();
        orders.save(o);
        for (CartService.Line l : lines) {
            OrderItem i = new OrderItem();
            i.orderNumber = o.orderNumber;
            i.productId = l.productId();
            i.productName = l.name();
            i.price = l.price();
            i.quantity = l.quantity();
            items.save(i);
        }
        return charge(o, card);
    }

    @Transactional
    public ShopOrder retry(String user, String orderNumber, String card) {
        return charge(find(user, orderNumber), card);
    }

    private ShopOrder charge(ShopOrder o, String card) {
        if ("PAID".equals(o.status)) return o;   // idempotent: never charge a paid order twice
        PaymentService.PayResult r = gateway.charge(o.orderNumber, o.total, card);

        Payment p = new Payment();
        p.orderNumber = o.orderNumber;
        p.transactionId = r.transactionId();
        p.amount = o.total;
        p.status = r.status();
        p.message = r.message();
        p.cardLast4 = last4(card);
        p.createdAt = LocalDateTime.now();
        payments.save(p);

        switch (r.status()) {
            case "SUCCESS" -> {
                o.status = "PAID";
                for (OrderItem i : items.findByOrderNumber(o.orderNumber)) {
                    Product pr = products.findById(i.productId).orElseThrow();
                    pr.stock = Math.max(0, pr.stock - i.quantity);
                    products.save(pr);
                }
                cart.clear(o.username);
            }
            case "PENDING" -> o.status = "PAYMENT_PENDING";
            default -> o.status = "PAYMENT_FAILED";
        }
        return orders.save(o);
    }

    private String last4(String card) {
        String d = card == null ? "" : card.replaceAll("\\D", "");
        return d.length() >= 4 ? d.substring(d.length() - 4) : "****";
    }

    private ShopOrder find(String user, String orderNumber) {
        return orders.findByOrderNumber(orderNumber)
                .filter(o -> o.username.equals(user))
                .orElseThrow(() -> new ApiException(404, "Order not found"));
    }

    public List<ShopOrder> list(String user) {
        return orders.findByUsernameOrderByCreatedAtDesc(user);
    }

    /** Step 13: receipt = order + items + all payment attempts. */
    public Map<String, Object> receipt(String user, String orderNumber) {
        ShopOrder o = find(user, orderNumber);
        return Map.of("order", o,
                      "items", items.findByOrderNumber(orderNumber),
                      "payments", payments.findByOrderNumberOrderByCreatedAtAsc(orderNumber));
    }
}
