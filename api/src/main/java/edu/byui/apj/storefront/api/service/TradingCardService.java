package edu.byui.apj.storefront.api.service;

import edu.byui.apj.storefront.api.model.TradingCard;
import jakarta.annotation.PostConstruct;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TradingCardService {

    private final List<TradingCard> tradingCards = new ArrayList<>();

    @PostConstruct
    public void init() {
        loadCardsFromCSV();
    }

    private void loadCardsFromCSV() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("pioneers.csv");
             InputStreamReader reader = new InputStreamReader(is);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            long id = 1L;
            for (CSVRecord record : csvParser) {
                try {
                    TradingCard card = createTradingCardFromRecord(record, id++);
                    tradingCards.add(card);
                } catch (Exception e) {
                    System.err.println("Error processing record: " + record);
                    e.printStackTrace();
                }
            }

            System.out.println("Successfully loaded " + tradingCards.size() + " trading cards from CSV");
            System.out.println("Sample card: " + tradingCards.get(0)); // Log the first card for verification

        } catch (IOException e) {
            System.err.println("Error loading CSV file:");
            e.printStackTrace();
        }
    }

    private TradingCard createTradingCardFromRecord(CSVRecord record, long id) {
        String name = record.get("Name");
        String specialty = record.get("Specialty");
        String contribution = record.get("Contribution");
        String priceStr = record.get("Price");
        String imageUrl = record.get("ImageUrl");

        BigDecimal price = parsePrice(priceStr);
        return new TradingCard(id, name, specialty, contribution, price, imageUrl);
    }

    private BigDecimal parsePrice(String priceStr) {
        try {
            return new BigDecimal(priceStr.replaceAll("[^0-9.]", ""));
        } catch (NumberFormatException e) {
            System.err.println("Error parsing price: " + priceStr + ", using default value 0");
            return BigDecimal.ZERO;
        }
    }

    public List<TradingCard> getPaginatedCards(int page, int size) {
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, tradingCards.size());

        if (fromIndex >= tradingCards.size()) {
            return new ArrayList<>();
        }

        return tradingCards.subList(fromIndex, toIndex);
    }

    public List<TradingCard> filterAndSortCards(BigDecimal minPrice, BigDecimal maxPrice, String specialty, String sort) {
        return tradingCards.stream()
                .filter(card -> minPrice == null || card.getPrice().compareTo(minPrice) >= 0)
                .filter(card -> maxPrice == null || card.getPrice().compareTo(maxPrice) <= 0)
                .filter(card -> specialty == null || card.getSpecialty().equalsIgnoreCase(specialty))
                .sorted(getSortComparator(sort))
                .collect(Collectors.toList());
    }

    private Comparator<TradingCard> getSortComparator(String sort) {
        if ("price".equalsIgnoreCase(sort)) {
            return Comparator.comparing(TradingCard::getPrice);
        }
        return Comparator.comparing(TradingCard::getName);
    }

    public List<TradingCard> searchCards(String query) {
        String lowercaseQuery = query.toLowerCase();
        return tradingCards.stream()
                .filter(card ->
                        card.getName().toLowerCase().contains(lowercaseQuery) ||
                                card.getContribution().toLowerCase().contains(lowercaseQuery))
                .collect(Collectors.toList());
    }
}