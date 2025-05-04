package com.example.pairs_arbitrage_trade_identifier.dto;

import lombok.Data;

@Data
public class StockPriceData {
    private String datetime;
    private String open;
    private String high;
    private String low;
    private String close;
    private String volume;
}