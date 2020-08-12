package binance;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.websocket.*;
import java.io.IOException;

@javax.websocket.ClientEndpoint
public class Client extends WSClientEndpoint {

    private Orderbook orderbook = null;
    private boolean first_valid_update = false;
    private long lastUpdateId;
    private String snapshotURI;


    public Client(String endpointURI, String snapshotURI) {
        // open websocket
        super(endpointURI);

        this.snapshotURI = snapshotURI;

    }

    public void add_messageHandler() {

        WSClientEndpoint.MessageHandler messageHandler = message -> {
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
                System.out.println(orderbook);
                lastUpdateId = update.get_final_id();
                orderbook.set_lastUpdateId(lastUpdateId);
            } else {
                System.out.println("Out of sync, abort");
            }

        };

        this.addMessageHandler(messageHandler);
    }

    public void close() throws IOException {

        this.userSession.close();
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


    @Override
    @OnOpen
    public void onOpen(Session userSession) {
        System.out.println("Connected to Binance");
        this.userSession = userSession;
    }

    @Override
    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        System.out.println("Disconnected to Binance");
        this.userSession = null;
    }

    @Override
    @OnMessage
    public void onMessage(String message) throws IOException {

        if (this.messageHandler != null) {
            this.messageHandler.handleMessage(message);
        }
    }

    public static void main(String[] agrs) throws InterruptedException {
        Client c = new Client("wss://stream.binance.com:9443/ws/btcusdt@depth", "https://www.binance.com/api/v1/depth?symbol=BTCUSDT");
        c.add_messageHandler();
        c.connect();
        Thread.currentThread().join();

    }
}

