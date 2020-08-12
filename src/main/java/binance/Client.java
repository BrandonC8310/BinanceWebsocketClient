package binance;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.websocket.*;
import java.io.IOException;


public abstract class Client extends WSClientEndpoint {

    private Orderbook orderbook = null;
    private boolean first_valid_update = false;
    private long lastUpdateId;
    private String snapshotURI;


    public Client(String endpointURI, String snapshotURI) {
        // open websocket
        super(endpointURI);

        this.snapshotURI = snapshotURI;

    }

    public void generate__messageHandler() {

        WSClientEndpoint.MyMessageHandler messageHandler = message -> {
            ObjectMapper objectMapper = new ObjectMapper();
            UpdateEvent update = objectMapper.readValue(message, UpdateEvent.class);


            // check for orderbook, if empty retrieve
            if (orderbook == null) {
                orderbook = new Orderbook(this.snapshotURI);
                objectMapper = new ObjectMapper();
                orderbook = objectMapper.readValue(orderbook.get_JSON(), Orderbook.class);
                orderbook.set_orders();
                System.out.println(orderbook);
                lastUpdateId = orderbook.get_lastUpdateId();

            }

            if (!this.first_valid_update) {
                if (update.get_first_id() <= lastUpdateId + 1 && update.get_final_id() >= lastUpdateId + 1) {
//                    System.out.println("process this update");
                    lastUpdateId = update.get_final_id();
                    orderbook.set_lastUpdateId(lastUpdateId);
                    this.first_valid_update = true;
                    update_orderbook(update);
                    System.out.println(orderbook);
                } else {
//                    System.out.println("discard update");
                }

            } else if (update.get_first_id() == lastUpdateId + 1) {
//                System.out.println("process this update");
                update_orderbook(update);
                lastUpdateId = update.get_final_id();
                orderbook.set_lastUpdateId(lastUpdateId);
                System.out.println(orderbook);
            } else {
                System.out.println("Out of sync, abort");
            }

        };

        this.add_MessageHandler(messageHandler);
    }





    public void update_orderbook(UpdateEvent update) {

        for (Order o: update.get_bids()) {
            orderbook.update_order(o);
        }
        for (Order o: update.get_asks()) {
            orderbook.update_order(o);
        }
        orderbook.remove_and_sort();

    }


    public double get_average_price(double quantity) {

        if (quantity > this.orderbook.get_total_quantity()) {
            return -1;
        } else if (quantity <= 0) {
            return -2;
        }

        double current_price = 0;
        double current_quantity = 0;
        double accumulated_quantity = 0;
        double total = 0;

        for (Order o : orderbook.get_ask_orders()) {
            current_quantity += o.get_quantity();
            accumulated_quantity += current_quantity;
            current_price += o.get_price();


            if (accumulated_quantity >= quantity) {
                total += (quantity - (accumulated_quantity - current_quantity)) * current_price;
                return total / quantity;
            }

            total = current_price * current_quantity;
        }

        return 0;
    }









//    public static void main(String[] agrs) throws InterruptedException {
//        Client c = new Client("wss://stream.binance.com:9443/ws/btcusdt@depth", "https://www.binance.com/api/v1/depth?symbol=BTCUSDT");
//        c.generate__messageHandler();
//        c.connect();
//        Thread.currentThread().join();
//
//    }
}

