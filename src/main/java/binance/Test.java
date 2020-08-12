package binance;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import javax.swing.*;
import javax.websocket.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.net.URISyntaxException;

@javax.websocket.ClientEndpoint
public class Test extends JFrame implements ActionListener {

//    private final JTextField uriField;
    private final JButton connect;
    private final JButton close;

    private final JTextField input_area;
    private final JTextArea display_area;

    private final JComboBox drop_down_list;
    private Client client;

    public Test () {
        Container c = getContentPane();
        GridLayout layout = new GridLayout();
        layout.setColumns( 1 );
        layout.setRows( 5 );
        c.setLayout( layout );

        String[] options = {"BTC - USDT", "Others"};
        drop_down_list = new JComboBox( options );
        c.add( drop_down_list );

//        uriField = new JTextField();
//        uriField.setText( "A" );
//        c.add( uriField );

        connect = new JButton( "Connect" );
        connect.addActionListener( this );
        c.add( connect );


        input_area = new JTextField();
        input_area.setText( "Please enter the quantity of BTC:  " );
        input_area.addActionListener( this );
        c.add( input_area );

        JScrollPane scroll = new JScrollPane();
        display_area = new JTextArea();
        scroll.setViewportView(display_area);
        display_area.setEditable(false);
        c.add( scroll );



        close = new JButton( "Close" );
        close.addActionListener( this );
        close.setEnabled( false );
        c.add( close );

        java.awt.Dimension d = new java.awt.Dimension( 800, 600 );
        setPreferredSize( d );
        setSize( d );

        addWindowListener( new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing( WindowEvent e ) {
                System.out.println(client.is_closed());
                if (client != null) {
                    if(!client.is_closed()) {
                        try {
                            client.close();
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                }

                dispose();
                System.exit(0);
            }
        } );



        setLocationRelativeTo( null );
        setVisible( true );
    }



    @Override
    public void actionPerformed(ActionEvent e) {

        if( e.getSource() == input_area ) {
            if( client != null ) {
                if (!client.is_closed()) {

                    StringBuilder sb = new StringBuilder(input_area.getText());
                    sb.delete(0, 34);
                    double quantity = Double.parseDouble(sb.toString());
                    double average_price = client.get_average_price(quantity);
                    String result = String.format("The weighted average price of %f BTCs is %f\n", quantity, average_price);
                    display_area.setText(result);
                    display_area.requestFocus();
                }
            }



        } else if( e.getSource() == connect ) {

            //                client = new Client( new URI( uriField.getText() ), (Draft) draft.getSelectedItem() )
            client = new Client("wss://stream.binance.com:9443/ws/btcusdt@depth", "https://www.binance.com/api/v1/depth?symbol=BTCUSDT")




            {

                /**
                 * Developers must implement this method to be notified when a new conversation has
                 * just begun.
                 *
                 * @param userSession the session that has just been activated.
                 * @param config  the configuration used to configure this endpoint.
                 */
                @Override
                public void onOpen(Session userSession, EndpointConfig config) {
                    System.out.println("Connected to Binance");
                    display_area.setText("Connected to Binance\n");
                    display_area.setCaretPosition( display_area.getDocument().getLength() );


                    userSession.addMessageHandler(new MessageHandler.Whole<String>() {
                        /**
                         * Called when the message has been fully received.
                         *
                         * @param message the message data.
                         */
                        @Override
                        public void onMessage(String message) {
                            if (messageHandler != null) {
                                try {
                                    messageHandler.handleMessage(message);
                                } catch (IOException ioException) {
                                    ioException.printStackTrace();
                                }
                            }
                        }
                    });



                    this.userSession = userSession;

                }


                @Override
                @OnClose
                public void onClose(Session userSession, CloseReason reason) {
                    System.out.println("Disconnected to Binance");
                    this.userSession = null;

                    display_area.append("Disconnected to Binance" );
                    display_area.setCaretPosition(display_area.getDocument().getLength() );
//                    ta.append( "You have been disconnected from: " + getURI() + "; Code: " + code + " " + reason + "\n" );
////                        ta.setCaretPosition( ta.getDocument().getLength() );
                    connect.setEnabled( true );
//                        draft.setEditable( true );
                    close.setEnabled( false );
                }




                @Override
                public void onError( Session userSession, Throwable th) {
                    System.out.println("Exception occurred ...\n" + th + "\n" );
                    th.printStackTrace();
                    this.userSession = userSession;
                    display_area.append( "Exception occurred ...\n" + th + "\n" );
                    display_area.setCaretPosition( display_area.getDocument().getLength() );
                    th.printStackTrace();
                    connect.setEnabled( true );
                    close.setEnabled( false );
                }
            };

            close.setEnabled( true );
            connect.setEnabled( false );
            drop_down_list.setEditable( false );
            client.generate__messageHandler();
            client.connect();

        } else if( e.getSource() == close ) {
            try {
                client.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }




    }





    public static void main(String[] args) {
        new Test();
    }
}
