package binance;

import binance.Constants.OrderSide;

public class Order implements Comparable<Order> {
    private final OrderSide side;
    private final double price;
    private double quantity;

    public Order(OrderSide side, double price, double quantity) {
        this.side = side;
        this.price = price;
        this.quantity = quantity;
    }


    @Override
    public int compareTo(Order o) {
        switch (side) {
            case ASK:
                return Double.compare(this.price, o.get_price()); // ascending
            case BID:
                return Double.compare(o.get_price(), this.price); // descending
            default:
                return -1;
        }
    }

    public double get_price() {
        return this.price;
    }

    public double get_quantity() {
        return this.quantity;
    }

    public OrderSide get_type() {
        return this.side;
    }

    public void set_quantity(double d) {
        this.quantity = d;
    }

}