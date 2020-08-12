package binance;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.websocket.*;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;


public abstract class Client extends WSClientEndpoint {

    private Orderbook orderbook = null;
    private boolean first_valid_update = false;
    private long lastUpdateId;
    private final String snapshotURI;



    public Client(String endpointURI, String snapshotURI) {
        // open websocket
        super(endpointURI);

        this.snapshotURI = snapshotURI;

    }



    /**
     *  Define the message handler, implement interface WSClientEndpoint.MyMessageHandler
     */
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
                    lastUpdateId = update.get_final_id();
                    orderbook.set_lastUpdateId(lastUpdateId);
                    this.first_valid_update = true;
                    update_orderbook(update);
                    System.out.println(orderbook);
                }

            } else if (update.get_first_id() == lastUpdateId + 1) {

                update_orderbook(update);
                lastUpdateId = update.get_final_id();
                orderbook.set_lastUpdateId(lastUpdateId);
                System.out.println(orderbook);
            }

        };

        this.add_MessageHandler(messageHandler);
    }


    /**
     * Update the local order book regarding the update event
     * @param update the incoming update event
     */
    public void update_orderbook(UpdateEvent update) {

        for (Order o: update.get_bids()) {
            orderbook.update_order(o);
        }
        for (Order o: update.get_asks()) {
            orderbook.update_order(o);
        }
        orderbook.remove_and_sort();


    }



    /**
     *
     * @param quantity the quantity being brought
     * @return the weighted average price
     */
    public double get_average_price_buy(double quantity) {

        if (quantity > this.orderbook.get_total_quantity_to_buy()) {

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


    /**
     *
     * @param quantity the quantity being sold
     * @return the weighted average price
     */
    public double get_average_price_sell(double quantity) {
        if (quantity > this.orderbook.get_total_quantity_to_sell()) {

            return -1;
        } else if (quantity <= 0) {

            return -2;
        }
        double current_price = 0;
        double current_quantity = 0;
        double accumulated_quantity = 0;
        double total = 0;

        for (Order o : orderbook.get_bid_orders()) {
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


    /**
     *
     * @return The total quantity in ask orders
     */
    public double get_total_quantity_to_buy() {
        return this.orderbook.get_total_quantity_to_buy();
    }


    /**
     * @return The total quantity in bid orders
     */
    public double get_total_quantity_to_sell() {
        return this.orderbook.get_total_quantity_to_sell();
    }

}

