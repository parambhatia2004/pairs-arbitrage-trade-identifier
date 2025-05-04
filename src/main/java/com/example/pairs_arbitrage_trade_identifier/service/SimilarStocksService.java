package com.example.pairs_arbitrage_trade_identifier.service;
import org.springframework.stereotype.Service;

import java.util.*;
@Service
public class SimilarStocksService {
    private final Map<String, List<String>> similarStocks = new HashMap<>();

    public SimilarStocksService() {
        similarStocks.put("AAPL", Arrays.asList("MSFT", "META", "GOOGL"));
        similarStocks.put("MSFT", Arrays.asList("AAPL", "GOOGL", "AMZN"));
        similarStocks.put("META", Arrays.asList("SNAP", "GOOGL", "MSFT"));
        similarStocks.put("TSLA", Arrays.asList("GM", "F", "NIO"));
    }

    public List<String> findSimilarStocks(String ticker) {
        return similarStocks.getOrDefault(ticker, Collections.emptyList());
    }
}
