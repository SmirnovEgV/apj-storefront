package edu.byui.apj.storefront.jms.consumer;

import edu.byui.apj.storefront.model.CardOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class OrderConfirmationConsumer {
    private static final Logger log = LoggerFactory.getLogger(OrderConfirmationConsumer.class);
    private final WebClient webClient;

    public OrderConfirmationConsumer(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8083").build();
    }

    @JmsListener(destination = "orderQueue")
    public void receiveOrderConfirmation(String orderId) {
        try {
            CardOrder cardOrder = webClient.get()
                    .uri("/order/{orderId}", orderId)
                    .retrieve()
                    .bodyToMono(CardOrder.class)
                    .block(); // Using block() for simplicity, consider using reactive approach in production

            if (cardOrder != null) {
                log.info("Order Confirmation Received - Order Details: {}", cardOrder);
            } else {
                log.error("No order found for order ID: {}", orderId);
            }
        } catch (Exception e) {
            log.error("Error processing order confirmation for order ID: {}", orderId, e);
        }
    }
}