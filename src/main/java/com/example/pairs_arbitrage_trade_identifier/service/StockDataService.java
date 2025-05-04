package com.example.pairs_arbitrage_trade_identifier.service;
import com.example.pairs_arbitrage_trade_identifier.dto.StockPriceData;
import org.springframework.stereotype.Service;
import com.example.pairs_arbitrage_trade_identifier.dto.StockPriceResponse;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Service
public class StockDataService {

    private static final String API_KEY = "XXXXXXXXX"; // replace this
    private static final String BASE_URL = "https://api.twelvedata.com/price";

    private final RestTemplate restTemplate = new RestTemplate();
    public String getStockPrice(String ticker) {
        String uri = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .queryParam("symbol", ticker)
                .queryParam("apikey", API_KEY)
                .toUriString();
        StockPriceData response = restTemplate.getForObject(uri, StockPriceData.class);
        return response.getOpen();
    }
}
