package gui.apis;

public interface ExchangeInterface {

    void startWebsocket(String symbol);
    Object getCandlesticks(String symbol);
    void ping();
    long getServertime();
    Object getOrderBook(String symbol, int amount);
    double getLatestPrice(String symbol);
    Object getAllLatestPrices();
    Object getBalances();
    double getBalance(String asset);
    Object getLastTrades(String symbol);
    Object getOpenOrders(String symbol);
    Object getOrderFilled(String symbol, long orderId);
    Object createMarketBuyOrder(String symbol, double quantity);
    Object createMarketSellOrder(String symbol, double quantity);
    Object createLimitBuyOrder(String symbol, double quantity, double price);
    Object createLimitSellOrder(String symbol, double quantity, double price);
    void cancelOrder(String symbol, long orderId);
    Object getWithdrawHistory();
    Object getDepositHistory();
}
