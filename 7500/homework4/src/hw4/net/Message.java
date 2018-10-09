package hw4.net;

public class Message {
    private Id from;
    private Send send;

    public Message(Id from, Send send) {
        this.from = from;
        this.send = send;
    }

    public Send getSend() {
        return send;
    }

    public Id getFrom() {
        return from;
    }

    @Override
    public String toString() {
        return "Message{" +
                "from=" + from +
                ", " + send +
                '}';
    }
}
