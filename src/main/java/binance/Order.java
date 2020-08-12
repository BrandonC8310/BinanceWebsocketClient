package binance;

public class Order implements Comparable<Order> {
    private final String type;
    private final double[] pair;

    public Order(String type, double[] pair) {
        this.type = type;
        this.pair = pair;
    }

    public Order(Order original) {
        this.type = original.type;
        this.pair = original.pair;
    }

    @Override
    public int compareTo(Order o) {
        if (type.equals("bid")) {
            return Double.compare(o.get_price(), this.pair[0]); // descending
        } else {
            return Double.compare(this.pair[0], o.get_price()); // ascending
        }
    }

    public double get_price() {
        return pair[0];
    }

    public double get_quantity() {
        return pair[1];
    }

    public void set_quantity(double d) {
        pair[1] = d;
    }


    public boolean is_bid() {
        return type.equals("bid");
    }

}