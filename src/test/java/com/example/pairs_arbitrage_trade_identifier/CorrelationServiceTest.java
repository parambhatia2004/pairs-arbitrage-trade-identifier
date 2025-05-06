package com.example.pairs_arbitrage_trade_identifier;

import com.example.pairs_arbitrage_trade_identifier.service.CorrelationService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CorrelationServiceTest {
    private final CorrelationService correlationService = new CorrelationService();
    @Test
    void testComputePearsonCorrelationService_withValidInput() {
        List<Double> stockA = List.of(1.0, 2.0, 3.0, 4.0, 5.0);
        List<Double> stockB = List.of(2.0, 4.0, 6.0, 8.0, 10.0); // perfectly correlated
        int window = 3;

        List<Double> result = correlationService.computePearsonCorrelationService(stockA, stockB, window);

        // Expect n - window + 1 = 5 - 3 + 1 = 3 correlations
        assertEquals(3, result.size());

        // Each correlation should be close to 1.0 (perfect positive correlation)
        for (Double correlation : result) {
            assertTrue(correlation >= 0.99 && correlation <= 1.0);
        }
    }

    @Test
    void handlesZeroVariance() {
        List<Double> stockA = List.of(1.0, 1.0, 1.0, 1.0);
        List<Double> stockB = List.of(2.0, 2.0, 2.0, 2.0);
        int window = 2;

        List<Double> result = correlationService.computePearsonCorrelationService(stockA, stockB, window);

        // All values should be 0.0 due to zero variance
        for (Double correlation : result) {
            assertEquals(0.0, correlation);
        }
    }
}
