package binance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateEvent {

    private @JsonProperty("E") String event_time;
    private @JsonProperty("s") String symbol;
    private @JsonProperty("U") long first_id;
    private @JsonProperty("u") long final_id;
    private @JsonProperty("b") ArrayList<double[]> bids_from_JSON;
    private @JsonProperty("a") ArrayList<double[]> asks_from_JSON;


    @Override
    public String toString() {
        return "Event time: " + event_time + "\n" +
                "Symbol: " + symbol + "\n" +
                "first id: " + first_id + "\n" +
                "final id: " + final_id + "\n" +
                "bids: " + list_to_str(bids_from_JSON) + "\n" +
                "asks: " + list_to_str(asks_from_JSON) + "\n";
    }


    public String list_to_str(ArrayList<double[]> list) {
        StringBuilder result = new StringBuilder();
        for(double[] floatArray : list){
            String s = "[" + String.valueOf(floatArray[0]) + ", " + String.valueOf(floatArray[1]) + "] ";
            result.append(s);
        }

        return result.toString();

    }




    public long get_first_id() {
        return first_id;
    }

    public long get_final_id() {
        return final_id;
    }

    public ArrayList<Order> get_bids() {

        ArrayList<Order> bids = new ArrayList<Order>();
        for (double[] d : bids_from_JSON) {
            bids.add(new Order("bid", d));
        }

        return bids;
    }

    public ArrayList<Order>get_asks() {

        ArrayList<Order> asks = new ArrayList<Order>();
        for (double[] d : asks_from_JSON) {
            asks.add(new Order("ask", d));
        }

        return asks;
    }

}
