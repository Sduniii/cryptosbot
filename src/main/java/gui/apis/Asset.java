package gui.apis;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Comparator;

public class Asset {
    private String label;
    private ObservableList<Asset> symbols;

    public Asset(String label){
        this.label = label;
        this.symbols = FXCollections.observableArrayList();
    }

    public ObservableList<Asset> getSymbols() {
        return symbols;
    }

    public void addSymbol(Asset asset){
        symbols.add(asset);
    }

    public static Comparator<Asset> comparator(){
        return Comparator.comparing(Asset::toString);
    }

    @Override
    public String toString(){
        return label;
    }
}
