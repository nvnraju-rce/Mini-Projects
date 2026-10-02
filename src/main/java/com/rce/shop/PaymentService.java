package com.rce.shop;

import java.math.BigDecimal;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

/** Calls the mock gateway over HTTP, validates the response, and handles failures and timeouts. */
@Service
public class PaymentService {
    public record PayResult(String status, String transactionId, String message) {}

    private final RestTemplate rest;
    private final String gatewayUrl;

    public PaymentService(@Value("${gateway.url}") String gatewayUrl) {
        this.gatewayUrl = gatewayUrl;
        SimpleClientHttpRequestFactory f = new SimpleClientHttpRequestFactory();
        f.setConnectTimeout(2000);
        f.setReadTimeout(3000);
        this.rest = new RestTemplate(f);
    }

    public PayResult charge(String orderNumber, BigDecimal amount, String cardNumber) {
        try {
            Map<String, Object> body = Map.of("orderNumber", orderNumber, "amount", amount,
                                              "cardNumber", cardNumber == null ? "" : cardNumber);
            Map<?, ?> res = rest.postForObject(gatewayUrl, body, Map.class);
            // Validate the response before trusting it
            if (res == null || res.get("status") == null) {
                return new PayResult("FAILED", null, "Invalid response from gateway");
            }
            return new PayResult(String.valueOf(res.get("status")),
                                 String.valueOf(res.get("transactionId")),
                                 String.valueOf(res.get("message")));
        } catch (ResourceAccessException e) {
            return new PayResult("PENDING", null, "Gateway timeout - payment status unknown");
        } catch (Exception e) {
            return new PayResult("FAILED", null, "Gateway error: " + e.getMessage());
        }
    }
}
