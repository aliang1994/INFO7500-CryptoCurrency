package hw4.net;

public class Value {
    private Integer number;

    public Value(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Value)) return false;
        Value v = (Value) o;
        return v.number.equals(number);
    }

    @Override
    public int hashCode() {
        return number;
    }

    @Override
    public String toString() {
        return Integer.toString(number);
    }
}
