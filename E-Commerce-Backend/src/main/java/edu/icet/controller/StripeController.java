package edu.icet.controller;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import edu.icet.dto.CheckoutItem;
import edu.icet.dto.CheckoutRequest;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class StripeController {

    @Value("${stripe.secret-key}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    @PostMapping("/create-checkout-session")
    public Map<String, String> createCheckoutSession(@RequestBody CheckoutRequest checkoutRequest) {
        try {
            SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                    .setSuccessUrl(checkoutRequest.getSuccessUrl())
                    .setCancelUrl(checkoutRequest.getCancelUrl());

            for (CheckoutItem item : checkoutRequest.getItems()) {
                paramsBuilder.addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setUnitAmount(item.getPrice())
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()

                                                                .setName("Product " + item.getProductId())
                                                                .build()
                                                )
                                                .build()
                                )
                                .setQuantity((long) item.getQuantity())
                                .build()
                );
            }

            Session session = Session.create(paramsBuilder.build());
            Map<String, String> response = new HashMap<>();
            response.put("sessionId", session.getId());
            return response;
        } catch (StripeException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Stripe error: " + e.getMessage()
            );
        }
    }
}