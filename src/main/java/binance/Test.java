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

    private final JTextField uriField;
    private final JButton connect;
    private final JButton close;
    private final JTextArea test_area;
    private final JTextField chatField;
//    private final JComboBox draft;
    private Client client;

    public Test () {
        Container c = getContentPane();
        GridLayout layout = new GridLayout();
        layout.setColumns( 1 );
        layout.setRows( 5 );
        c.setLayout( layout );

//        Draft[] drafts = { new Draft_6455() };
//        draft = new JComboBox( drafts );
//        c.add( draft );

        uriField = new JTextField();
        uriField.setText( "A" );
        c.add( uriField );

        connect = new JButton( "Connect" );
        connect.addActionListener( this );
        c.add( connect );



        JScrollPane scroll = new JScrollPane();
        test_area = new JTextArea();
        scroll.setViewportView(test_area);
        c.add( scroll );

        chatField = new JTextField();
        chatField.setText( "" );
        chatField.addActionListener( this );
        c.add( chatField );

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
//                if( cc != null ) {
//                    cc.close();
//                }
                dispose();
                System.exit(0);
            }
        } );



        setLocationRelativeTo( null );
        setVisible( true );
    }



    @Override
    public void actionPerformed(ActionEvent e) {

        if( e.getSource() == chatField ) {
//            if( cc != null ) {
//                cc.send( chatField.getText() );
//                chatField.setText( "" );
//                chatField.requestFocus();
//            }

        } else if( e.getSource() == connect ) {

//                client = new Client( new URI( uriField.getText() ), (Draft) draft.getSelectedItem() )
            client = new Client("wss://stream.binance.com:9443/ws/btcusdt@depth", "https://www.binance.com/api/v1/depth?symbol=BTCUSDT") ;


//            {
//
//                @Override
//                @OnMessage
//                public void onMessage( String message ) throws IOException {
//                    client.add_messageHandler();
//                    if (this.messageHandler != null) {
//                        this.messageHandler.handleMessage(message);
//                    }
//                    test_area.append( "got: " + message + "\n" );
//                    test_area.setCaretPosition( test_area.getDocument().getLength());
//                }
//
//
//            };

//            {
//
//
//                @Override
//                @OnOpen
//                public void onOpen(Session userSession) {
//                    System.out.println("Connected to Binance");
//                    this.userSession = userSession;
//                    test_area.append("Connected to Binance\n");
//                    test_area.setCaretPosition( test_area.getDocument().getLength() );
//
//
////                        ta.append( "You are connected to ChatServer: " + getURI() + "\n" );
////                        ta.setCaretPosition( ta.getDocument().getLength() );
//                }
//
//                @Override
//                @OnClose
//                public void onClose(Session userSession, CloseReason reason) {
//                    System.out.println("Disconnected to Binance");
//                    this.userSession = null;
//
//                    test_area.append("Disconnected to Binance" );
//                    test_area.setCaretPosition(test_area.getDocument().getLength() );
//                    connect.setEnabled( true );
//                    uriField.setEditable( true );
////                        draft.setEditable( true );
//                    close.setEnabled( false );
//                }
//
//                @Override
//                @OnMessage
//                public void onMessage( String message ) throws IOException {
//                    client.add_messageHandler();
//                    if (this.messageHandler != null) {
//                        this.messageHandler.handleMessage(message);
//                    }
//                    test_area.append( "got: " + message + "\n" );
//                    test_area.setCaretPosition( test_area.getDocument().getLength());
//                }
//
//
//
//
////                    @Override
////                    public void onClose( int code, String reason, boolean remote ) {
////                        ta.append( "You have been disconnected from: " + getURI() + "; Code: " + code + " " + reason + "\n" );
////                        ta.setCaretPosition( ta.getDocument().getLength() );
////                        connect.setEnabled( true );
////                        uriField.setEditable( true );
////                        draft.setEditable( true );
////                        close.setEnabled( false );
////                    }
//
////                    @Override
////                    public void onError( Exception ex ) {
////                        ta.append( "Exception occurred ...\n" + ex + "\n" );
////                        ta.setCaretPosition( ta.getDocument().getLength() );
////                        ex.printStackTrace();
////                        connect.setEnabled( true );
////                        uriField.setEditable( true );
////                        draft.setEditable( true );
////                        close.setEnabled( false );
////                    }
//            };

            close.setEnabled( true );
            connect.setEnabled( false );
            uriField.setEditable( false );
//                draft.setEditable( false );
            client.add_messageHandler();
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
