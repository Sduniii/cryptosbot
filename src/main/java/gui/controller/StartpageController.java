package gui.controller;

import com.binance.api.client.BinanceApiAsyncRestClient;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class StartpageController implements Initializable {

    private String secret = "M7LoyltBakw8cpqnERcuKgO7Uy2pHNYtM4lquwRMECHZM6ORM7uIvRjUcL7GkUJK";
    private String key = "GvY4MH56MxZnWzI9xDFtzVYAZw7t8gV69lTY4JvhUsrHQ0tOorhpzfUnOlaH1a12";
    public void initialize(URL location, ResourceBundle resources) {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(key, secret);
        BinanceApiAsyncRestClient client = factory.newAsyncRestClient();

        System.out.println("v");
        client.getServerTime(System.out::println);

    }
}
