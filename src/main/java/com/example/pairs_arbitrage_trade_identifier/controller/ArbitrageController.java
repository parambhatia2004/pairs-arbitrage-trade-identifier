package com.example.pairs_arbitrage_trade_identifier.controller;

import com.example.pairs_arbitrage_trade_identifier.dto.StockPriceResponse;
import com.example.pairs_arbitrage_trade_identifier.dto.TickerInput;
import com.example.pairs_arbitrage_trade_identifier.service.*;
import com.example.pairs_arbitrage_trade_identifier.service.CorrelationService;
import com.example.pairs_arbitrage_trade_identifier.service.PairTradeAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

import java.io.IOException;

@RestController
public class ArbitrageController {
    private final StockDataService stockDataService;
    private final SimilarStocksService similarStocksService;
    private final CorrelationService correlationService;
    private final StockPriceListService stockPriceListService;
    private final PairTradeAnalysisService pairTradeAnalysisService;

    public ArbitrageController(StockDataService stockDataService, PairTradeAnalysisService pairTradeAnalysisService) {
        this.stockDataService = stockDataService;
        this.pairTradeAnalysisService = pairTradeAnalysisService;
        this.similarStocksService = new SimilarStocksService();
        this.stockPriceListService = new StockPriceListService();
        this.correlationService = new CorrelationService();
    }
    @PostMapping("/scan")
    public ResponseEntity<String> scanTicker(@RequestBody TickerInput UserInput) throws IOException {
//        Stock stock;
        String stockPrice = stockDataService.getStockPrice(UserInput.getUserTicker().toUpperCase());
        List<String> similarStocks = similarStocksService.findSimilarStocks(UserInput.getUserTicker().toUpperCase());
        String response = "Ticker: " + UserInput.getUserTicker() +
                ", Price: " + stockPrice +
                ", Similar Stocks: " + String.join(", ", similarStocks);
        return ResponseEntity.ok(response);

    }

    @PostMapping("/hourly_data")
    public ResponseEntity<String> getHourlyData(@RequestBody TickerInput UserInput){
        StockPriceResponse userStockPriceResponse = stockPriceListService.getHourlyData(UserInput.getUserTicker().toUpperCase());

        List<String> similarStocks = similarStocksService.findSimilarStocks(UserInput.getUserTicker());
        StringBuilder response = new StringBuilder();
        response.append("Price Data: ").append(userStockPriceResponse.getValues().get(0).getOpen()).append("\n");
        return ResponseEntity.ok(response.toString());
    }
    @PostMapping("/minutely_data")
    public ResponseEntity<String> getMinutelyData(@RequestBody TickerInput UserInput){
        StockPriceResponse userStockPriceResponse = stockPriceListService.getMinutelyData(UserInput.getUserTicker().toUpperCase());

        List<String> similarStocks = similarStocksService.findSimilarStocks(UserInput.getUserTicker());
        StringBuilder response = new StringBuilder();
        response.append("Price Data: ").append(userStockPriceResponse.getValues().get(0).getOpen()).append("\n");
        return ResponseEntity.ok(response.toString());
    }

    @PostMapping("/compute_correlation")
    public ResponseEntity<String> analyzePairTrade(@RequestBody TickerInput input) {
        String analysis = pairTradeAnalysisService.analyseSimilarStocks(input.getUserTicker().toUpperCase());
        return ResponseEntity.ok(analysis);
    }
}

