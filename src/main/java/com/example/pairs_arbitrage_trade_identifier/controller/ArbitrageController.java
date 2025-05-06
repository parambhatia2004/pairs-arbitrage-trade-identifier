package com.example.pairs_arbitrage_trade_identifier.controller;

import com.example.pairs_arbitrage_trade_identifier.dto.StockPriceResponse;
import com.example.pairs_arbitrage_trade_identifier.dto.TickerInput;
import com.example.pairs_arbitrage_trade_identifier.dto.HistoricalPriceData;
import com.example.pairs_arbitrage_trade_identifier.service.CorrelationService;
import com.example.pairs_arbitrage_trade_identifier.service.StockDataService;
import com.example.pairs_arbitrage_trade_identifier.service.SimilarStocksService;
import com.example.pairs_arbitrage_trade_identifier.service.StockPriceListService;
import com.example.pairs_arbitrage_trade_identifier.service.CorrelationService;
import lombok.RequiredArgsConstructor;
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

    public ArbitrageController(StockDataService stockDataService) {
        this.stockDataService = stockDataService;
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
    public ResponseEntity<String> getHourlyData(@RequestBody TickerInput UserInput) throws IOException {
        StockPriceResponse userStockPriceResponse = stockPriceListService.getHourlyData(UserInput.getUserTicker().toUpperCase());

        List<String> similarStocks = similarStocksService.findSimilarStocks(UserInput.getUserTicker());
        StringBuilder response = new StringBuilder();
        response.append("Price Data: ").append(userStockPriceResponse.getValues().get(0).getOpen()).append("\n");
        return ResponseEntity.ok(response.toString());
    }
    @PostMapping("/minutely_data")
    public ResponseEntity<String> getMinutelyData(@RequestBody TickerInput UserInput) throws IOException {
        StockPriceResponse userStockPriceResponse = stockPriceListService.getMinutelyData(UserInput.getUserTicker().toUpperCase());

        List<String> similarStocks = similarStocksService.findSimilarStocks(UserInput.getUserTicker());
        StringBuilder response = new StringBuilder();
        response.append("Price Data: ").append(userStockPriceResponse.getValues().get(0).getOpen()).append("\n");
        return ResponseEntity.ok(response.toString());
    }

    @PostMapping("/compute_correlation")
    public void analyzeSimilarStocks(@RequestBody TickerInput UserInput) {
        List<String> similarTickers = similarStocksService.findSimilarStocks(UserInput.getUserTicker());
        List<Double> userStockMinutelyData = stockPriceListService.getMinutelyOpenPrices(UserInput.getUserTicker());

        for (String similarStocks : similarTickers) {
            List<Double> similarStockMinutelyData = stockPriceListService.getMinutelyOpenPrices(similarStocks);
//
//            List<Double> userPrices = new ArrayList<>(userData.getOpenPrices().values());
//            List<Double> simPrices = new ArrayList<>(similarData.getOpenPrices().values());

            List<Double> correlations = correlationService.computePearsonCorrelationService(userStockMinutelyData, similarStockMinutelyData, 10);

            // Log or analyze correlations
            System.out.println("Correlations with " + similarStocks + ": " + correlations);
        }
    }
}

