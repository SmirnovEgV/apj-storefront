package edu.byui.apj.storefront.jms;

import edu.byui.apj.storefront.model.Cart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class CartCleanupJob {
    private static final Logger log = LoggerFactory.getLogger(CartCleanupJob.class);
    private final WebClient webClient;

    public CartCleanupJob(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8083").build();
    }

    public List<Cart> getCartsWithoutOrders() {
        try {
            return webClient.get()
                    .uri("/cart/noorder")
                    .retrieve()
                    .bodyToFlux(Cart.class)
                    .collectList()
                    .block(); // Using block() for simplicity, consider reactive approach in production
        } catch (Exception e) {
            log.error("Error retrieving carts without orders", e);
            throw new RuntimeException("Failed to retrieve carts", e);
        }
    }

    public void cleanupCart(String cartId) {
        try {
            webClient.delete()
                    .uri("/cart/{cartId}", cartId)
                    .retrieve()
                    .toBodilessEntity()
                    .block(); // Using block() for simplicity

            log.info("Successfully deleted cart with ID: {}", cartId);
        } catch (WebClientResponseException e) {
            log.error("Failed to delete cart with ID: {}. HTTP Status: {}", cartId, e.getStatusCode());
        } catch (Exception e) {
            log.error("Unexpected error deleting cart with ID: {}", cartId, e);
        }
    }

    @Scheduled(cron = "0 0 0 * * ?") // Run daily at midnight
    public void cleanupCarts() {
        try {
            List<Cart> cartsToCleanup = getCartsWithoutOrders();

            if (cartsToCleanup == null || cartsToCleanup.isEmpty()) {
                log.info("No carts to clean up");
                return;
            }

            ExecutorService executorService = Executors.newFixedThreadPool(2);

            cartsToCleanup.forEach(cart ->
                    executorService.submit(() -> cleanupCart(cart.getId()))
            );

            executorService.shutdown();
            executorService.awaitTermination(5, TimeUnit.MINUTES);

            log.info("Cart cleanup complete");
        } catch (Exception e) {
            log.error("Error during cart cleanup", e);
        }
    }
}
