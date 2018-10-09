package hw4.net;

public class Id implements Comparable<Id> {
    private Integer number;

    public Id(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Id)) return false;
        Id v = (Id) o;
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

    @Override
    public int compareTo(Id id2) {
        return Integer.compare(number, id2.number);
    }
}
