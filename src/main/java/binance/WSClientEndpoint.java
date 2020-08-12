package binance;

import java.io.IOException;
import java.net.URI;
import javax.websocket.*;


public abstract class WSClientEndpoint extends Endpoint {

    protected Session userSession = null;
    protected String endpointURI;
    protected URI uri;
    protected WebSocketContainer container;
    protected MyMessageHandler messageHandler;
    protected boolean closed = false;


    public WSClientEndpoint(String endpointURI) {
       this.endpointURI = endpointURI;
    }


    /**
     * Connect to the server
     */
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


    /**
     * Close the connection
     * @throws IOException
     */
    public void close() throws IOException {
        this.userSession.close();
        this.closed = true;
    }


    /**
     *  Check if the connection is closed
     * @return is closed or not
     */
    public boolean is_closed() {
        return this.closed;
    }

    /**
     * Message handler.
     */
    public interface MyMessageHandler {
        void handleMessage(String message) throws IOException;
    }


    /**
     * register message handler
     *
     * @param msgHandler the message handler object
     */
    public void add_MessageHandler(MyMessageHandler msgHandler) {
        this.messageHandler = msgHandler;
    }


}