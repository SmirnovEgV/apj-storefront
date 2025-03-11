package edu.byui.apj.storefront.web.controller;

import edu.byui.apj.storefront.web.model.TradingCard;
import edu.byui.apj.storefront.web.service.TradingCardClientService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class TradingCardController {
    private final TradingCardClientService tradingCardClientService;

    public TradingCardController(TradingCardClientService tradingCardClientService) {
        this.tradingCardClientService = tradingCardClientService;
    }

    // REST API endpoints to match JavaScript requests
    @GetMapping("/api/cards")
    @ResponseBody
    public List<TradingCard> getAllCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {
        return tradingCardClientService.getAllCardsPaginated(page, size);
    }

    @GetMapping("/api/cards/filter")
    @ResponseBody
    public List<TradingCard> filterCards(
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String specialty,
            @RequestParam(required = false, defaultValue = "name") String sort) {
        return tradingCardClientService.filterAndSort(minPrice, maxPrice, specialty, sort);
    }

    @GetMapping("/api/cards/search")
    @ResponseBody
    public List<TradingCard> searchCards(@RequestParam String query) {
        return tradingCardClientService.searchByNameOrContribution(query);
    }

}