package com.example.pairs_arbitrage_trade_identifier.service;

import java.util.ArrayList;
import java.util.List;

public class CorrelationService {

    public List<Double> computePearsonCorrelationService(List<Double> userStock, List<Double> compareStock, int window) {
        List<Double> correlations = new ArrayList<>();

        int n = userStock.size();
        for (int i = 0; i <= n - window; i++) {
            List<Double> xWindow = userStock.subList(i, i + window);
            List<Double> yWindow = compareStock.subList(i, i + window);
            correlations.add(computeWindowPearsonCorrelation(xWindow, yWindow));
        }

        return correlations;
    }


    private double computeWindowPearsonCorrelation(List<Double> stockA, List<Double> stockB) {
        int n = stockA.size();
        if (n != stockB.size() || n == 0) return 0.0;

        double sumX = 0, sumY = 0, sumX2 = 0, sumY2 = 0, sumXY = 0;

        for (int i = 0; i < n; i++) {
            sumX += stockA.get(i);
            sumY += stockB.get(i);
            sumX2 += Math.pow(stockA.get(i), 2);
            sumY2 += Math.pow(stockB.get(i), 2);
            sumXY += stockA.get(i) * stockB.get(i);
        }

        double numerator = n * sumXY - sumX * sumY;
        double denominator = Math.sqrt((n * sumX2 - sumX * sumX) * (n * sumY2 - sumY * sumY));

        return denominator == 0 ? 0.0 : numerator / denominator;
    }
}
