package com.example.pairs_arbitrage_trade_identifier.dto;
import lombok.Data;

import java.util.List;

@Data
public class StockPriceResponse {
    private String symbol;
    private List<StockPriceData> values;
}
