package binance;

import java.io.IOException;
import java.net.URI;
import javax.websocket.*;

/**
 * ChatServer Client
 *
 * @author Jiji_Sasidharan
 */
@javax.websocket.ClientEndpoint
public abstract class WSClientEndpoint {

    Session userSession = null;
    String endpointURI;
    URI uri;
    WebSocketContainer container;
    MessageHandler messageHandler;


    public WSClientEndpoint(String endpointURI) {
       this.endpointURI = endpointURI;
    }

    /**
     * Callback hook for Connection open events.
     *
     * @param userSession the userSession which is opened.
     */
    @OnOpen
    public abstract void onOpen(Session userSession);



    /**
     * Callback hook for Connection close events.
     *
     * @param userSession the userSession which is getting closed.
     * @param reason the reason for connection close
     */
    @OnClose
    public abstract void onClose(Session userSession, CloseReason reason);


    /**
     * Callback hook for Connection error events.
     *
     * @param userSession the userSession which has exception occurred.
     * @param th the exception which is occurred.
     */
    @OnError
    public void onError(Session userSession, Throwable th) {
        System.out.println("Exception occurred ...\n" + th + "\n" );
        th.printStackTrace();
        this.userSession = userSession;
    }



    /**
     * Callback hook for Message Events. This method will be invoked when a client send a message.
     *
     * @param message The text message
     */
    @OnMessage
    public abstract void onMessage(String message) throws IOException;
//    {
//        if (this.messageHandler != null) {
//            this.messageHandler.handleMessage(message);
//        }
//    }

    /**
     * register message handler
     *
     * @param msgHandler
     */
    public void addMessageHandler(MessageHandler msgHandler) {
        this.messageHandler = msgHandler;
    }

    /**
     * Send a message.
     *
     * @param message
     */
    public void sendMessage(String message) {
        this.userSession.getAsyncRemote().sendText(message);
    }

    /**
     * Message handler.
     *
     * @author Jiji_Sasidharan
     */
    public interface MessageHandler {

        void handleMessage(String message) throws IOException;
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






}