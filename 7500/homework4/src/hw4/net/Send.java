package hw4.net;

public class Send {
    private Id to;
    private Payload payload;

    public Send(Id to, Payload payload) {
        this.to = to;
        this.payload = payload;
    }

    public Id getTo() {
        return to;
    }

    public <T extends Payload> T getPayload(Class<T> payLoadClass) {
        if (payload.getClass().equals(payLoadClass)) {
            return (T) payload;
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return "{" +
                "to=" + to +
                ", payload=" + payload +
                '}';
    }
}
