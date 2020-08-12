package binance;

import java.io.IOException;
import java.net.URI;
import javax.websocket.*;

/**
 * ChatServer Client
 *
 * @author Jiji_Sasidharan
 */

public abstract class WSClientEndpoint extends Endpoint {

    Session userSession = null;
    String endpointURI;
    URI uri;
    WebSocketContainer container;
    MyMessageHandler messageHandler;
    protected boolean closed = false;


    public WSClientEndpoint(String endpointURI) {
       this.endpointURI = endpointURI;
    }

    public void connect() {
        try {
            uri = new URI(this.endpointURI);
            container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, uri);

        } catch (Exception e) {
            System.out.println("HAHHA");
            throw new RuntimeException(e);
        }

    }

    public void close() throws IOException {
        this.userSession.close();
        this.closed = true;
    }

    public boolean is_closed() {
        return this.closed;
    }

    /**
     * Message handler.
     *
     * @author Jiji_Sasidharan
     */

    public interface MyMessageHandler {

        void handleMessage(String message) throws IOException;
    }


    /**
     * register message handler
     *
     * @param msgHandler
     */
    public void add_MessageHandler(MyMessageHandler msgHandler) {
        this.messageHandler = msgHandler;
    }




    /**
     * Send a message.
     *
     * @param message
     */
    public void send_Message(String message) {
        this.userSession.getAsyncRemote().sendText(message);
    }












}