package com.example.pairs_arbitrage_trade_identifier.service;
import com.example.pairs_arbitrage_trade_identifier.dto.StockPriceResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Objects;

@Service
public class StockPriceListService {
    private static final String API_KEY = "e34edaa72b594c8ea49c801d8683bca1"; // replace this
    private static final String BASE_URL = "https://api.twelvedata.com/time_series?symbol={";
    String baseUrl = "https://api.twelvedata.com/time_series";
    private final RestTemplate restTemplate = new RestTemplate();

    public StockPriceResponse getHourlyData(String symbol){
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("symbol", symbol)
                .queryParam("interval", "1h")
                .queryParam("apikey", API_KEY)
                .queryParam("start_date", "2025-01-01")
                .queryParam("end_date", "2025-01-10")
                .toUriString();
        System.out.println("______");
        System.out.println(url);
        return restTemplate.getForObject(url, StockPriceResponse.class);
    }
    public StockPriceResponse getMinutelyData(String symbol){
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("symbol", symbol)
                .queryParam("interval", "1min")
                .queryParam("apikey", API_KEY)
                .queryParam("start_date", "2025-01-01")
                .queryParam("end_date", "2025-01-10")
                .toUriString();
        System.out.println("______");
        System.out.println(url);
        return restTemplate.getForObject(url, StockPriceResponse.class);
    }

    public List<Double> getMinutelyOpenPrices(String symbol){
        StockPriceResponse response = getMinutelyData(symbol);
        if (response == null || response.getValues() == null) return List.of();

        return response.getValues().stream()
                .map(v -> {
                    try {
                        return Double.parseDouble(v.getOpen());
                    } catch (NumberFormatException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }
}
