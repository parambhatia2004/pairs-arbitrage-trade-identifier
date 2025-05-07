package com.example.pairs_arbitrage_trade_identifier.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PairTradeAnalysisService {
    private final StockDataService stockDataService;
    private final SimilarStocksService similarStocksService;
    private final CorrelationService correlationService;
    private final StockPriceListService stockPriceListService;

    private static final int CORR_WINDOW = 30;      // # of points for baseline correlation
    private static final int Z_WINDOW    = 30;      // # of points for spread Z-score
    private static final double CORR_THRESH = 0.8;  // minimum avg |r| for correlation
    private static final double Z_THRESH    = 2.0;

    public String analyseSimilarStocks(String userTicker){
        List<Double> userStockData = stockPriceListService.getMinutelyOpenPrices(userTicker);
        List<String> similarTickers = similarStocksService.findSimilarStocks(userTicker);

        StringBuilder report = new StringBuilder("Pair Trading Analysis for: ")
                .append(userTicker)
                .append("\n----------------------------------------\n");

        for(String similarTicker : similarTickers){
            List<Double> similarStockData = stockPriceListService.getMinutelyOpenPrices(similarTicker);
            List<Double> similarStockCorrelation = correlationService.computePearsonCorrelationService(userStockData, similarStockData, 10);
            double sum = 0;
            for (Double val : similarStockCorrelation) {
                sum += Math.abs(val);
                System.out.println(val);
            }
            double avg = similarStockCorrelation.isEmpty() ? 0.0 : sum / similarStockCorrelation.size();
            System.out.println("AVG: ");
            System.out.println(avg);

            if (avg < CORR_THRESH) {
                report.append(similarTicker)
                        .append(String.format(": low correlation (avg|r|=%.2f)\n", avg));
                continue;
            }

            // 4) Compute spread and rolling Z-scores
            List<Double> spread = computeSpread(userStockData, similarStockData);
            List<Double> zscores = rollingZScore(spread, Z_WINDOW);

            // 5) Detect mean reversion
            boolean mr = hasMeanReversion(zscores, Z_THRESH);
            if (mr) {
                report.append(similarTicker)
                        .append(String.format(": MEAN-REVERTING (avg|r|=%.2f)\n", avg));
            } else {
                report.append(similarTicker)
                        .append(String.format(": no mean reversion (avg|r|=%.2f)\n", avg));
            }
        }

        return report.toString();
    }

    private double computeBeta(List<Double> x, List<Double> y) {
        int n = x.size();
        double sumX  = x.stream().mapToDouble(d -> d).sum();
        double sumY  = y.stream().mapToDouble(d -> d).sum();
        double sumXY = 0.0;
        for (int i = 0; i < n; i++) {
            sumXY += x.get(i) * y.get(i);
        }
        double sumX2 = 0.0;
        for (double d : x) {
            sumX2 += d * d;
        }


        // Cov(X,Y) = sumXY - sumX*sumY/n
        // Var(X)   = sumX2 - (sumX^2)/n
        double cov = sumXY - (sumX * sumY) / n;
        double var = sumX2 - (sumX * sumX) / n;
        return (var == 0.0) ? 0.0 : cov / var;
    }

    private List<Double> computeSpread(List<Double> x, List<Double> y) {
        double beta = computeBeta(x, y);
        int n = x.size();
        List<Double> spread = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            spread.add(x.get(i) - beta * y.get(i));
        }
        return spread;
    }

    private List<Double> rollingZScore(List<Double> spread, int window) {
        int n = spread.size();
        List<Double> zscores = new ArrayList<>();
        for (int i = 0; i <= n - window; i++) {
            List<Double> w = spread.subList(i, i + window);
            double mean = w.stream().mapToDouble(d -> d).average().orElse(0.0);
            double sd   = Math.sqrt(w.stream()
                    .mapToDouble(d -> (d - mean) * (d - mean))
                    .sum() / window);
            double current = spread.get(i + window - 1);
            zscores.add(sd == 0.0 ? 0.0 : (current - mean) / sd);
        }
        return zscores;
    }

    private boolean hasMeanReversion(List<Double> zscores, double threshold) {
        boolean diverged = false;
        for (double z : zscores) {
            if (!diverged && Math.abs(z) >= threshold) {
                diverged = true;            // diverged beyond threshold
            } else if (diverged && Math.abs(z) <= 0.0) {
                return true;                // came back through mean
            }
        }
        return false;
    }
}
