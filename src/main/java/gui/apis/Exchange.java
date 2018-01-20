package gui.apis;

import gui.models.BarData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public abstract class Exchange {
    String name;
    String apiKey;
    String apiSecret;
    ObservableList<Asset> assets = FXCollections.observableArrayList();
    ObservableList<Asset> baseAssets = FXCollections.observableArrayList();

    public Exchange(String name, String apiKey, String apiSecret){
        this.name = name;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }

    public String getName() {
        return name;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public ObservableList<Asset> getAssets() {
        return assets;
    }

    public ObservableList<Asset> getBaseAssets(){
        return baseAssets;
    }

    public void setAssets(ObservableList<Asset> assets){
        this.assets = assets;
    }

    public void setBaseAssets(ObservableList<Asset> baseAssets){
        this.baseAssets = baseAssets;
    }
    public abstract Object getCandlesticks(String symbol);
    public abstract List<BarData> buildCandlestickData(String symbol, long time);
    public abstract void ping();
    public abstract long getServertime();
    public abstract Object getOrderBook(String symbol, int amount);
    public abstract double getLatestPrice(String symbol);
    public abstract Object getAllLatestPrices();
    public abstract Object getBalances();
    public abstract double getBalance(String asset);
    public abstract Object getLastTrades(String symbol);
    public abstract Object getOpenOrders(String symbol);
    public abstract Object getOrderFilled(String symbol, long orderId);
    public abstract Object createMarketBuyOrder(String symbol, double quantity);
    public abstract Object createMarketSellOrder(String symbol, double quantity);
    public abstract Object createLimitBuyOrder(String symbol, double quantity, double price);
    public abstract Object createLimitSellOrder(String symbol, double quantity, double price);
    public abstract void cancelOrder(String symbol, long orderId);
    public abstract Object getWithdrawHistory();
    public abstract Object getDepositHistory();
}
