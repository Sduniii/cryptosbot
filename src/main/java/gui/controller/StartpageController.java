package gui.controller;

import com.binance.api.client.domain.event.CandlestickEvent;
import gui.Listener.OnCloseListener;
import gui.apis.*;
import gui.apis.binance.Binance;
import gui.models.BarData;
import gui.models.CandleStickChart;
import gui.models.DateAxis;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.*;


public class StartpageController implements Initializable, OnCloseListener, CallbackInterface<CandlestickEvent> {

    @FXML
    BorderPane borderPane;
    @FXML
    ChoiceBox<Exchange> cbExchange;
    @FXML
    ListView<Asset> lvAsset1;
    @FXML
    ListView<Asset> lvAsset2;

    private CandleStickChart candleStickChart;

    private Binance binance;

    private GregorianCalendar last;
    private ObservableList<Exchange> exchanges = FXCollections.observableArrayList();

    public void initialize(URL location, ResourceBundle resources) {
        String secret = "";
        String key = "";
        binance = new Binance(key, secret);
        binance.registerCandlestickCallback(this);
        exchanges.add(binance);

        cbExchange.setItems(exchanges);
        cbExchange.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            lvAsset1.setItems(newValue.getBaseAssets());
            FXCollections.sort(lvAsset1.getItems(),Asset.comparator());
        });

        lvAsset1.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            lvAsset2.setItems(newValue.getSymbols());
            FXCollections.sort(lvAsset2.getItems(),Asset.comparator());
        });
        lvAsset2.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                Exchange ex = cbExchange.getSelectionModel().getSelectedItem();
                Asset asset1 = lvAsset1.getSelectionModel().getSelectedItem();

                if (asset1 != null && ex != null && newValue != null) {
                    borderPane.setCenter(null);
                    last = new GregorianCalendar();
//                    StackPane chartContainer = new StackPane();
                    String symbol = asset1.toString() + newValue.toString();
                    candleStickChart = new CandleStickChart(ex.getName()+ " - " +  symbol.toUpperCase(), ex.buildCandlestickData(symbol.toUpperCase(), 1000*60*60));
//                    chartContainer.getChildren().add(candleStickChart);
//                    final Rectangle zoomRect = new Rectangle();
//                    zoomRect.setManaged(false);
//                    zoomRect.setFill(Color.LIGHTSEAGREEN.deriveColor(0, 1, 1, 0.5));
//                    chartContainer.getChildren().add(zoomRect);
//                    setUpZooming(zoomRect, candleStickChart);
//                    borderPane.setCenter(chartContainer);
                    borderPane.setCenter(candleStickChart);
                    if(ex instanceof Binance) {
                        ((Binance) ex).startWebsocket(symbol.toUpperCase());
                    }
                }

        });
    }


    public void shiftSeriesValue(XYChart.Series<String, Number> series, String label, double newValue) {
        int numOfPoint = series.getData().size();
        for (int i = 0; i < numOfPoint - 1; i++) {
            XYChart.Data<String, Number> ShiftDataUp =
                    series.getData().get(i + 1);
            Number shiftNumberValue = ShiftDataUp.getYValue();
            String shiftStringValue = ShiftDataUp.getXValue();
            XYChart.Data<String, Number> ShiftDataDn =
                    series.getData().get(i);
            ShiftDataDn.setYValue(shiftNumberValue);
            ShiftDataDn.setXValue(shiftStringValue);
        }
        XYChart.Data<String, Number> lastData =
                series.getData().get(numOfPoint - 1);
        lastData.setYValue(newValue);
        lastData.setXValue(label);
    }

    private void setUpZooming(final Rectangle rect, final CandleStickChart zoomingNode) {
        final ObjectProperty<Point2D> mouseAnchor = new SimpleObjectProperty<>();
        final MouseEvent[] e = {null};
        zoomingNode.setOnMousePressed(event -> {
            e[0] = event;
            mouseAnchor.set(new Point2D(event.getX(), event.getY()));
            rect.setWidth(0);
            rect.setHeight(0);
        });
        zoomingNode.setOnMouseDragged(event -> {
            e[0] = event;
            double x = event.getX();
            double y = event.getY();
            rect.setX(Math.min(x, mouseAnchor.get().getX()));
            rect.setY(Math.min(y, mouseAnchor.get().getY()));
            rect.setWidth(Math.abs(x - mouseAnchor.get().getX()));
            rect.setHeight(Math.abs(y - mouseAnchor.get().getY()));
        });
        zoomingNode.setOnMouseReleased(event -> {
            if (e[0].getEventType() == MouseEvent.MOUSE_DRAGGED) doZoom(rect, zoomingNode);
        });
    }

    private void doZoom(Rectangle zoomRect, CandleStickChart chart) {
        Point2D zoomTopLeft = new Point2D(zoomRect.getX(), zoomRect.getY());
        Point2D zoomBottomRight = new Point2D(zoomRect.getX() + zoomRect.getWidth(), zoomRect.getY() + zoomRect.getHeight());
        final NumberAxis yAxis = chart.getYAxis();
        Point2D yAxisInScene = yAxis.localToScene(0, 0);
        final DateAxis xAxis = chart.getXAxis();
        Point2D xAxisInScene = xAxis.localToScene(0, 0);
        double xOffset = zoomTopLeft.getX() - yAxisInScene.getX();
        double yOffset = zoomBottomRight.getY() - xAxisInScene.getY();
        Date leftBorder = xAxis.getValueForDisplay(zoomRect.getX());
        Date RightBorder = xAxis.getValueForDisplay(zoomRect.getX() + zoomRect.getWidth());

        xAxis.setLowerBound(leftBorder);
        xAxis.setUpperBound(RightBorder);
        double yAxisScale = yAxis.getScale();

        yAxis.setLowerBound(yAxis.getLowerBound() + yOffset / yAxisScale);
        yAxis.setUpperBound(yAxis.getLowerBound() - zoomRect.getHeight() / yAxisScale);
        System.out.println(yAxis.getLowerBound() + " " + yAxis.getUpperBound());
        zoomRect.setWidth(0);
        zoomRect.setHeight(0);
    }

    @Override
    public void onClose() {

    }

    @Override
    public void callback(CandlestickEvent candlestick) {
        GregorianCalendar cal = new GregorianCalendar();
        new GregorianCalendar().setTimeInMillis(candlestick.getEventTime());
        Platform.runLater(() -> {
            double time = ((double) (cal.getTime().getTime() - last.getTime().getTime())) / (1000 * 60 * 5);
            if (time > 10) {
                last = cal;
                candleStickChart.addBar(new BarData(cal,
                        Double.parseDouble(candlestick.getOpen()),
                        Double.parseDouble(candlestick.getHigh()),
                        Double.parseDouble(candlestick.getLow()),
                        Double.parseDouble(candlestick.getClose()),
                        (int) Double.parseDouble(candlestick.getVolume())));
            } else {
                candleStickChart.updateLast(new BarData(last,
                        Double.parseDouble(candlestick.getOpen()),
                        Double.parseDouble(candlestick.getHigh()),
                        Double.parseDouble(candlestick.getLow()),
                        Double.parseDouble(candlestick.getClose()),
                        (int) Double.parseDouble(candlestick.getVolume())));
            }
        });
    }
}
