package edu.byui.apj.storefront.web.service;

import edu.byui.apj.storefront.web.model.TradingCard;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class TradingCardClientService {
    private final WebClient webClient;

    public TradingCardClientService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8081").build();
    }

    public List<TradingCard> getAllCardsPaginated(int page, int size) {
        return webClient.get()
                .uri("/api/cards?page={page}&size={size}", page, size)
                .retrieve()
                .bodyToFlux(TradingCard.class)
                .collectList()
                .block();
    }

    public List<TradingCard> filterAndSort(BigDecimal minPrice, BigDecimal maxPrice, String specialty, String sort) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/cards/filter")
                        .queryParamIfPresent("minPrice", Optional.ofNullable(minPrice))
                        .queryParamIfPresent("maxPrice", Optional.ofNullable(maxPrice))
                        .queryParamIfPresent("specialty", Optional.ofNullable(specialty))
                        .queryParamIfPresent("sort", Optional.ofNullable(sort))
                        .build())
                .retrieve()
                .bodyToFlux(TradingCard.class)
                .collectList()
                .block();
    }

    public List<TradingCard> searchByNameOrContribution(String query) {
        return webClient.get()
                .uri("/api/cards/search?query={query}", query)
                .retrieve()
                .bodyToFlux(TradingCard.class)
                .collectList()
                .block();
    }
}