package gui.apis;

import com.binance.api.client.*;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.AssetBalance;
import com.binance.api.client.domain.event.CandlestickEvent;
import com.binance.api.client.domain.market.*;

import java.util.ArrayList;
import java.util.List;

public class Binance extends Exchange implements ExchangeInterface {
    private BinanceApiRestClient restClient;
    private BinanceApiAsyncRestClient asyncRestClient;
    private BinanceApiWebSocketClient webSocketClient;
    private BinanceApiClientFactory factory;
    private Callback<CandlestickEvent> candlestickEventCallbacks;

    public Binance(String name, String apiKey, String apiSecret) {
        super(name, apiKey, apiSecret);
        this.candlestickEventCallbacks = new Callback<>();
        this.factory = BinanceApiClientFactory.newInstance(apiKey, apiSecret);
        this.restClient = this.factory.newRestClient();
        this.asyncRestClient = this.factory.newAsyncRestClient();
    }



    @Override
    public void startWebsocket(String symbol) {
        this.webSocketClient = BinanceApiClientFactory.newInstance().newWebSocketClient();
        this.webSocketClient.onCandlestickEvent(symbol, CandlestickInterval.ONE_MINUTE, this::websocketCallbackCandlestick);
    }

    @Override
    public List<Candlestick> getCandlesticks(String symbol) {
        return this.restClient.getCandlestickBars(symbol, CandlestickInterval.ONE_MINUTE);
    }

    public void websocketCallbackCandlestick(CandlestickEvent response) {
        candlestickEventCallbacks.callback(response);
    }

    public void registerCallback(CallbackInterface<CandlestickEvent> call) {
        candlestickEventCallbacks.addCallback(call);
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
}
