package hw4.consensus.bft;

import hw4.net.Payload;
import hw4.net.Value;

public class UnauthBFTPayload extends Payload {
    private Trace trace;
    private Value decisionValue;

    public UnauthBFTPayload(Trace trace, Value decisionValue) {
        this.trace = trace;
        this.decisionValue = decisionValue;
    }

    public Trace getTrace() {
        return trace;
    }

    public Value getDecisionValue() {
        return decisionValue;
    }

    @Override
    public String toString() {
        return "{" +
                "trace=" + trace +
                ", decisionValue=" + decisionValue +
                '}';
    }
}
