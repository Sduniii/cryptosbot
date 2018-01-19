package gui.apis.binance;

import com.binance.api.client.domain.event.CandlestickEvent;
import gui.apis.Event;

public class CandleStickEvent<T> implements Event<T> {
    private T event;
    public CandleStickEvent(T event){
        this.event = event;
    }

    public T getEvent() {
        return event;
    }
}
