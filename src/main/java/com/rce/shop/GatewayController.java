package com.rce.shop;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import org.springframework.web.bind.annotation.*;

/**
 * MOCK payment gateway. No real money is ever processed.
 * Test cards:
 *   4111111111111111 -> SUCCESS
 *   4000000000000002 -> FAILED (card declined)
 *   4000000000009995 -> FAILED (insufficient funds)
 *   4000000000000119 -> slow response (5 s), the shop times out after 3 s -> PENDING
 *   anything else    -> FAILED (invalid card)
 */
@RestController
@RequestMapping("/gateway")
public class GatewayController {
    public record PayRequest(String orderNumber, BigDecimal amount, String cardNumber) {}

    @PostMapping("/pay")
    public Map<String, Object> pay(@RequestBody PayRequest r) throws InterruptedException {
        String card = r.cardNumber() == null ? "" : r.cardNumber().replaceAll("\\s", "");
        String status;
        String message;
        switch (card) {
            case "4111111111111111" -> { status = "SUCCESS"; message = "Payment approved"; }
            case "4000000000000002" -> { status = "FAILED"; message = "Card declined"; }
            case "4000000000009995" -> { status = "FAILED"; message = "Insufficient funds"; }
            case "4000000000000119" -> { Thread.sleep(5000); status = "SUCCESS"; message = "Approved (slow)"; }
            default -> { status = "FAILED"; message = "Invalid card number"; }
        }
        String txn = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return Map.of("status", status, "transactionId", txn, "message", message,
                      "orderNumber", String.valueOf(r.orderNumber()));
    }
}
