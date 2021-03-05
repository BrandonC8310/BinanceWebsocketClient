package binance;

import com.fasterxml.jackson.databind.ObjectMapper;
import static binance.Constants.EXCEED_CAPACITY;
import static binance.Constants.NON_POSITIVE;

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
     * @param target_qty the quantity being bought or sold
     * @return the weighted average price
     */
    public double get_average_price(double target_qty, Constants.OrderSide side) {

        double accumulated_cost = 0;
        double remaining_qty = target_qty;

        if (side == Constants.OrderSide.ASK) {
            if (target_qty > this.orderbook.get_total_quantity_to_buy()) {
                return EXCEED_CAPACITY;
            } else if (target_qty <= 0) {
                return NON_POSITIVE;
            }
            for (Order o : orderbook.get_ask_orders()) {
                if (remaining_qty > 0) {
                    double order_qty = Math.min(o.get_quantity(), remaining_qty);
                    accumulated_cost += o.get_price() * order_qty;
                    remaining_qty -= order_qty;
                } else {
                    return accumulated_cost / target_qty;
                }
            }
        } else {
            if (target_qty > this.orderbook.get_total_quantity_to_sell()) {
                return EXCEED_CAPACITY;
            } else if (target_qty <= 0) {
                return NON_POSITIVE;
            }
            for (Order o : orderbook.get_bid_orders()) {
                if (remaining_qty > 0) {
                    double order_qty = Math.min(o.get_quantity(), remaining_qty);
                    accumulated_cost += o.get_price() * order_qty;
                    remaining_qty -= order_qty;
                } else {
                    return accumulated_cost / target_qty;
                }
            }
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

