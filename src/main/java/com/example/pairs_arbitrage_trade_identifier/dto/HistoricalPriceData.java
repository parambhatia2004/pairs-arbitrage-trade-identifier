package com.example.pairs_arbitrage_trade_identifier.dto;

import lombok.Data;
import java.util.Map;

@Data
public class HistoricalPriceData {
    private String ticker;
    private Map<String, Double> openPrices; // Date string -> Open price
}
