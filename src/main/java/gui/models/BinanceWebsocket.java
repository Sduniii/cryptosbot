package gui.models;

import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.constant.BinanceApiConstants;
import com.binance.api.client.domain.event.AggTradeEvent;
import com.binance.api.client.domain.event.CandlestickEvent;
import com.binance.api.client.domain.event.DepthEvent;
import com.binance.api.client.domain.event.UserDataUpdateEvent;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.binance.api.client.impl.BinanceApiWebSocketListener;
import gui.apis.Exchange;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BinanceWebsocket implements BinanceApiWebSocketClient, Closeable {

    private OkHttpClient client;
    private List<WebSocket> webSockets;

    public BinanceWebsocket() {
        this.client = new OkHttpClient();
        this.webSockets = new ArrayList<>();
    }

    public void onDepthEvent(String symbol, BinanceApiCallback<DepthEvent> callback) {
        final String channel = String.format("%s@depth", symbol.toLowerCase());
        createNewWebSocket(channel, new BinanceApiWebSocketListener<>(callback, DepthEvent.class));
    }

    @Override
    public void onCandlestickEvent(String symbol, CandlestickInterval interval, BinanceApiCallback<CandlestickEvent> callback) {
        final String channel = String.format("%s@kline_%s", symbol, interval.getIntervalId());
        createNewWebSocket(channel, new BinanceApiWebSocketListener<>(callback, CandlestickEvent.class));
    }

    public void onAggTradeEvent(String symbol, BinanceApiCallback<AggTradeEvent> callback) {
        final String channel = String.format("%s@aggTrade", symbol);
        createNewWebSocket(channel, new BinanceApiWebSocketListener<>(callback, AggTradeEvent.class));
    }

    public void onUserDataUpdateEvent(String listenKey, BinanceApiCallback<UserDataUpdateEvent> callback) {
        createNewWebSocket(listenKey, new BinanceApiWebSocketListener<>(callback, UserDataUpdateEvent.class));
    }

    private void createNewWebSocket(String channel, BinanceApiWebSocketListener<?> listener) {
        String streamingUrl = String.format("%s/%s", BinanceApiConstants.WS_API_BASE_URL, channel);
        Request request = new Request.Builder().url(streamingUrl).build();
        this.webSockets.add(client.newWebSocket(request, listener));
    }

    public void closeWebSocket(String channel) {
        List<WebSocket> toDelete = new ArrayList<>();
        this.webSockets.forEach(webSocket -> {
            if (webSocket.request().url().toString().contains(channel.toLowerCase())) {
                webSocket.cancel();
                //webSocket.close(1000, "");
                toDelete.add(webSocket);
            }
        });

        this.webSockets.removeAll(toDelete);
    }

    @Override
    public void close() {
        client.dispatcher().executorService().shutdown();
    }
}
