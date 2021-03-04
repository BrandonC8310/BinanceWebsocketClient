package binance;

import javax.swing.*;
import javax.websocket.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

@javax.websocket.ClientEndpoint
public class App extends JFrame implements ActionListener{
    private final ReentrantLock lock = new ReentrantLock();
    private final JTextArea description;
    private static final int GAP = 2;
    private final JButton connect;
    private final JButton close;
    private final JButton buy;
    private final JButton sell;

    private final JTextField input_area;
    private final JTextArea display_area;

    private final JComboBox drop_down_list;
    private Client client;

    private GridBagConstraints gbc = new GridBagConstraints();
    private GridBagLayout gbLayout = new GridBagLayout();

    private JPanel mainPanel;



    public App() {
        super("Binance Local Order Book");

        // display window
        mainPanel = new JPanel();
        mainPanel.setLayout(gbLayout);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        this.setContentPane(mainPanel);
        GridBagConstraints gbc = new GridBagConstraints();

        // instruction area
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(GAP, GAP, GAP, GAP);
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        description = new JTextArea();
        description.setText( "Instruction:\n1. Choose the market\n" );
        description.append( "2. Click \"Connect\"\n" );
        description.append( "3. Enter the amount\n" );
        description.append( "4. Click \"Buy\" or \"Sell\"\n" );
        description.append( "5. Read the weighted average price\n" );
        description.append( "6. Repeat or click Close to close connection" );
        mainPanel.add(description, gbc);


        // drop down list
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        String[] options = {"BTC - USDT", "Others - not implemented"};
        drop_down_list = new JComboBox( options );
        mainPanel.add(drop_down_list, gbc);


        // Connect button
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        connect = new JButton( "Connect" );
        connect.addActionListener( this );
        mainPanel.add(connect, gbc);


        // input area
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        input_area = new JTextField();
        input_area.setText( "Please enter the quantity of BTC:  " );
        mainPanel.add(input_area, gbc);


        // Buy button
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        buy = new JButton( "Buy" );
        buy.addActionListener( this );
        mainPanel.add(buy, gbc);


        // Sell button
        sell = new JButton( "Sell" );
        sell.addActionListener( this );
        gbc.gridx+=1;
        mainPanel.add(sell, gbc);


        // display area
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        JScrollPane scroll = new JScrollPane();
        display_area = new JTextArea();
        scroll.setViewportView(display_area);
        display_area.setEditable(false);
        mainPanel.add(scroll, gbc);


        // Close Button
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        close = new JButton( "Close" );
        close.addActionListener( this );
        close.setEnabled( false );
        mainPanel.add(close, gbc);


        java.awt.Dimension d = new java.awt.Dimension( 600, 800 );
        setPreferredSize( d );
        setSize( d );
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        setLocationRelativeTo( null );




        // close window operation
        addWindowListener( new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing( WindowEvent e ) {

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
    }



    /**
     * Invoked when an action occurs.
     *
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        if( e.getSource() == buy ) {
            if (client != null) {
                if (!client.is_closed()) {

                    try {
                        lock.lock();
                        StringBuilder sb = new StringBuilder(input_area.getText());
                        sb.delete(0, 34);
                        double quantity = Double.parseDouble(sb.toString());
                        double average_price = client.get_average_price_buy(quantity);
                        String result;
                        if (average_price == -1) {
                            // exceed capacity
                            result = String.format("Max quantity available is %f, please enter an amount less that %f\n", client.get_total_quantity_to_buy(), client.get_total_quantity_to_buy());
                        } else if (average_price == -2) {
                            // negative value / 0
                            result = "please enter an positive value\n";
                        } else {
                            result = String.format("The weighted average price of buying %f BTCs is $%f\n", quantity, average_price);
                        }
                        display_area.setText(result);
                        display_area.requestFocus();
                    } finally {
                        lock.unlock();
                    }


                }
            }


        } else if ( e.getSource() == sell ) {

            if (client != null) {
                if (!client.is_closed()) {

                    try {
                        lock.lock();
                        StringBuilder sb = new StringBuilder(input_area.getText());
                        sb.delete(0, 34);
                        double quantity = Double.parseDouble(sb.toString());
                        double average_price = client.get_average_price_sell(quantity);
                        String result;
                        if (average_price == -1) {
                            // exceed capacity
                            result = String.format("Max quantity available is %f, please enter an amount less that %f\n", client.get_total_quantity_to_sell(), client.get_total_quantity_to_sell());
                        } else if (average_price == -2) {
                            // negative value / 0
                            result = "please enter an positive value\n";
                        } else {
                            result = String.format("The weighted average price of selling %f BTCs is $%f\n", quantity, average_price);
                        }
                        display_area.setText(result);
                        display_area.requestFocus();
                    } finally {
                        lock.unlock();
                    }

                }
            }


        } else if( e.getSource() == connect ) {


            client = new Client("wss://stream.binance.com:9443/ws/btcusdt@depth", "https://www.binance.com/api/v1/depth?symbol=BTCUSDT&limit=1000")

            {

                /**
                 * Developers must implement this method to be notified when a new conversation has
                 * just begun.
                 *
                 * @param userSession the session that has just been activated.
                 * @param config  the configuration used to configure this endpoint.
                 */
                @Override
                @OnOpen
                public void onOpen(Session userSession, EndpointConfig config) {
                    System.out.println("Connected to Binance");
                    display_area.setText("Connected to Binance\n");
                    display_area.setCaretPosition( display_area.getDocument().getLength() );

                    // define the operation on message
                    userSession.addMessageHandler(new MessageHandler.Whole<String>() {
                        /**
                         * Called when the message has been fully received.
                         *
                         * @param message the message data.
                         */
                        @Override
                        @OnMessage
                        public void onMessage(String message) {
                            if (messageHandler != null) {
                                try {
                                    lock.lock();
                                    messageHandler.handleMessage(message);
                                } catch (IOException ioException) {
                                    ioException.printStackTrace();
                                } finally {
                                    lock.unlock();
                                }
                            }
                        }
                    });
                    this.userSession = userSession;

                }

                /**
                 * When a connection is closed
                 * @param userSession he session that has just been activated.
                 * @param reason the reason the session was closed.
                 */
                @Override
                @OnClose
                public void onClose(Session userSession, CloseReason reason) {
                    System.out.println("Disconnected to Binance");
                    this.userSession = null;

                    display_area.setText("Disconnected to Binance" );
                    display_area.setCaretPosition(display_area.getDocument().getLength() );
                    connect.setEnabled( true );
                    close.setEnabled( false );
                }


                /**
                 * Developers may implement this method when the web socket session creates
                 * some kind of error that is not modeled in the web socket protocol.
                 * This may for example be a notification that an incoming message is
                 * too big to handle, or that the incoming message could not be encoded.
                 * @param userSession the session in use when the error occurs.
                 * @param th the throwable representing the problem.
                 */
                @Override
                @OnError
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
        new App();
    }



}
