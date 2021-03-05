package binance;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import binance.Constants.OrderSide;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Orderbook {

    private @JsonProperty("lastUpdateId") long lastUpdateId;
    private @JsonProperty("bids") ArrayList<double[]> bids_from_JSON;
    private @JsonProperty("asks") ArrayList<double[]> asks_from_JSON;
    private @JsonIgnore String jsonstr;

    private ArrayList<Order> bids;
    private ArrayList<Order> asks;

    private double total_bids_quantity;
    private double total_asks_quantity;

    @JsonCreator
    public Orderbook(@JsonProperty("snapshotURI") String snapshotURI)  {
        try {
            URL oracle = new URL(snapshotURI);
            BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
            StringBuilder result = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                result.append(inputLine);
            in.close();
            this.jsonstr = result.toString();
            System.out.println(this.jsonstr);
        } catch (IOException ignore) {

        }
    }



    public String get_JSON() {
        return this.jsonstr;
    }

    public long get_lastUpdateId() {
        return this.lastUpdateId;
    }

    public void set_lastUpdateId(long id) {
        this.lastUpdateId = id;
    }


    public String list_to_str(ArrayList<Order> list) {
        StringBuilder result = new StringBuilder();
        for(Order o : list){
            String s = "[" + o.get_price() + ", " + o.get_quantity() + "] ";
            result.append(s);
        }

        return result.toString();

    }

    public void set_orders() {
        bids = new ArrayList<Order>();
        for (double[] d : bids_from_JSON) {
            bids.add(new Order(OrderSide.BID, d[0], d[1]));
        }

        asks = new ArrayList<Order>();
        for (double[] d : asks_from_JSON) {
            asks.add(new Order(OrderSide.ASK, d[0], d[1]));
        }

        double i = 0;
        for (double[] arr : bids_from_JSON) {
            i += arr[1];
        }
        this.total_bids_quantity = i;
        i = 0;
        for (double[] arr : asks_from_JSON) {
            i += arr[1];
        }
        this.total_asks_quantity = i;
    }

    @Override
    public String toString() {

        System.out.printf("Total bid amount: %f\n", this.total_bids_quantity);
        System.out.printf("Total ask amount: %f\n", this.total_asks_quantity);
        Collections.sort(bids);
        Collections.sort(asks);
        return "lastUpdateId: " + lastUpdateId + "\n" +
                "bids: " + list_to_str(bids) + "\n" +
                "asks: " + list_to_str(asks) + "\n";
    }

    public void update_order(Order new_order) {
        switch (new_order.get_type())
        {
            case BID:
                boolean find = false;
                for (Order o : bids) {
                    if (o.get_price() == new_order.get_price()) {
                        o.set_quantity(new_order.get_quantity());
                        find = true;
                        break;
                    }
                }

                if (!find) {
                    bids.add(new_order);
                }
                break;

            case ASK:
                find = false;
                for (Order o : asks) {
                    if (o.get_price() == new_order.get_price()) {
                        o.set_quantity(new_order.get_quantity());
                        find = true;
                        break;
                    }
                }
                if (!find) {
                    asks.add(new_order);
                }
                break;
        }

        double i = 0;
        for (double[] arr : bids_from_JSON) {
            i += arr[1];
        }
        this.total_bids_quantity = i;
        i = 0;
        for (double[] arr : asks_from_JSON) {
            i += arr[1];
        }
        this.total_asks_quantity = i;

    }

    public void remove_and_sort() {
        bids.removeIf(o -> o.get_quantity() == 0);
        asks.removeIf(o -> o.get_quantity() == 0);
        Collections.sort(bids);
        Collections.sort(asks);
    }

    public double get_total_quantity_to_buy() {
        return this.total_asks_quantity;
    }

    public double get_total_quantity_to_sell() {
        return this.total_bids_quantity;
    }

    public ArrayList<Order> get_ask_orders() {
        return asks;
    }


    public ArrayList<Order> get_bid_orders() {
        return bids;
    }



}
