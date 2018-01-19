package gui.apis;

import java.util.ArrayList;
import java.util.List;

public class Callback<T> {
    private List<CallbackInterface<T>> callbacks = new ArrayList<>();

    public void addCallback(CallbackInterface<T> call){
        callbacks.add(call);
    }

    public void clearCallbacks(){
        callbacks.clear();
    }

    public void callback(Event<T> response){
        for (CallbackInterface<T> callbackInterface : callbacks) {
            callbackInterface.callback(response.getEvent());
        }
    }
}
