
public class Order {

    double value;
    double rate;

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public Order(double value2, double rate2) {
        this.value = value2;
        this.rate = rate2;
    }

    @Override
    public String toString() {
        return "Order [rate=" + rate + ", value=" + value + "]";
    }

}
