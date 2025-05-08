The aim of this project is to identify potential assets that are highly correlated with the users choice of stock, and thus can form part of a pair trade.

This project uses TwelveData API to get minutely data of stocks, and uses Spring as a framework.

Use the mapping /compute_correlation, to find correlated pairs, the other mappings was part of initial development used to validate the functionality of included services.

TODO:
Automatically identify similar stocks, currently uses hardcoded values.

Refine analysis service, currently uses Pearson correlation.

Optimise parameters to identify potential trades.
