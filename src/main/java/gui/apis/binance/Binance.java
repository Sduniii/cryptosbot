package gui.apis.binance;

import com.binance.api.client.BinanceApiAsyncRestClient;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.AssetBalance;
import com.binance.api.client.domain.event.CandlestickEvent;
import com.binance.api.client.domain.general.ExchangeInfo;
import com.binance.api.client.domain.market.*;
import gui.apis.*;
import gui.models.BarData;
import javafx.collections.FXCollections;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class Binance extends Exchange {
    private BinanceApiRestClient restClient;
    private BinanceApiAsyncRestClient asyncRestClient;
    private BinanceApiWebSocketClient webSocketClient;
    private BinanceApiClientFactory factory;
    private Callback candlestickEventCallbacks;


    private ExchangeInfo exchangeInfo;

    public Binance(String apiKey, String apiSecret) {
        super("Binance", apiKey, apiSecret);
        this.candlestickEventCallbacks = new Callback();
        this.factory = BinanceApiClientFactory.newInstance(getApiKey(), getApiSecret());
        this.restClient = this.factory.newRestClient();
        this.exchangeInfo = this.restClient.getExchangeInfo();
        this.exchangeInfo.getSymbols().forEach(symbolInfo -> {
            Asset as = getBaseAssets().stream().filter(asset -> asset.toString().equals(symbolInfo.getBaseAsset())).findFirst().orElse(getAssets().stream().filter(asset -> asset.toString().equals(symbolInfo.getBaseAsset())).findFirst().orElse(new Asset(symbolInfo.getBaseAsset())));
            if(!getAssets().contains(as)) getAssets().add(as);
            if(!getBaseAssets().contains(as)) getBaseAssets().add(as);
            Asset quoteAsset = as.getSymbols().stream().filter(asset -> asset.toString().equals(symbolInfo.getQuoteAsset())).findFirst().orElse(getAssets().stream().filter(asset -> asset.toString().equals(symbolInfo.getQuoteAsset())).findFirst().orElse(new Asset(symbolInfo.getQuoteAsset())));
            if(!as.getSymbols().contains(quoteAsset)) as.addSymbol(quoteAsset);
            if(!getAssets().contains(quoteAsset)) getAssets().add(quoteAsset);
            FXCollections.sort(as.getSymbols(),Asset.comparator());
        });


        FXCollections.sort(getAssets(),Asset.comparator());
        this.asyncRestClient = this.factory.newAsyncRestClient();
    }

    @Override
    public void startWebsocket(String symbol) {
        this.webSocketClient = BinanceApiClientFactory.newInstance().newWebSocketClient();
        this.webSocketClient.onCandlestickEvent(symbol, CandlestickInterval.ONE_MINUTE, this::websocketCallbackCandlestick);
    }

    private void websocketCallbackCandlestick(CandlestickEvent candlestickEvent) {
        candlestickEventCallbacks.callback(new CandleStickEvent<CandlestickEvent>(candlestickEvent));
    }

    @Override
    public List<Candlestick> getCandlesticks(String symbol) {
        return this.restClient.getCandlestickBars(symbol, CandlestickInterval.ONE_MINUTE);
    }

    @Override
    public void clearCallbacks(){
        candlestickEventCallbacks.clearCallbacks();
    }


    @Override
    public void ping() {
        this.restClient.ping();
    }

    @Override
    public long getServertime() {
        return this.restClient.getServerTime();
    }

    @Override
    public OrderBook getOrderBook(String symbol, int amount) {
        return this.restClient.getOrderBook(symbol,amount);
    }

    @Override
    public double getLatestPrice(String symbol) {
        TickerStatistics tickerStatistics = this.restClient.get24HrPriceStatistics(symbol);
        return Double.valueOf(tickerStatistics.getLastPrice());
    }

    @Override
    public List<Double> getAllLatestPrices() {
        List<TickerPrice> allPrices = this.restClient.getAllPrices();
        List<Double> ret =  new ArrayList<>();
        allPrices.forEach(tickerPrice -> ret.add(Double.valueOf(tickerPrice.getPrice())));
        return ret;
    }

    @Override
    public List<AssetBalance> getBalances() {
        Account account = this.restClient.getAccount();
        return account.getBalances();
    }

    @Override
    public double getBalance(String asset){
        Account account = this.restClient.getAccount();
        return Double.valueOf(account.getAssetBalance(asset).getFree());
    }

    @Override
    public Object getLastTrades(String symbol) {
        return null;
    }

    @Override
    public Object getOpenOrders(String symbol) {
        return null;
    }

    @Override
    public Object getOrderFilled(String symbol, long orderId) {
        return null;
    }

    @Override
    public Object createMarketBuyOrder(String symbol, double quantity) {
        return null;
    }

    @Override
    public Object createMarketSellOrder(String symbol, double quantity) {
        return null;
    }

    @Override
    public Object createLimitBuyOrder(String symbol, double quantity, double price) {
        return null;
    }

    @Override
    public Object createLimitSellOrder(String symbol, double quantity, double price) {
        return null;
    }

    @Override
    public void cancelOrder(String symbol, long orderId) {

    }

    @Override
    public Object getWithdrawHistory() {
        return null;
    }

    @Override
    public Object getDepositHistory() {
        return null;
    }

    @Override
    public void registerCallback(CallbackInterface<Event> callback) {
            candlestickEventCallbacks.addCallback(callback);
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public List<BarData> buildData(String symbol) {
        List<Candlestick> candles = getCandlesticks(symbol);
        List<BarData> data = new ArrayList<>();
        candles.forEach(candlestick -> {
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTimeInMillis(candlestick.getOpenTime());
            BarData bar = new BarData(cal,
                    Double.parseDouble(candlestick.getOpen()),
                    Double.parseDouble(candlestick.getHigh()),
                    Double.parseDouble(candlestick.getLow()),
                    Double.parseDouble(candlestick.getClose()),
                    (int) Double.parseDouble(candlestick.getVolume()));
            data.add(bar);
        });
        return data;
    }
}
