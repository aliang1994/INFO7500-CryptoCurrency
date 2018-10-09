package hw4.consensus.majority;

import hw4.net.Payload;
import hw4.net.Value;

public class MajorityVotingPayload extends Payload {
    private Value decisionValue;

    public MajorityVotingPayload(Value decisionValue) {
        this.decisionValue = decisionValue;
    }

    public Value getDecisionValue() {
        return decisionValue;
    }

    public void setDecisionValue(Value v) {
        this.decisionValue = v;
    }

    @Override
    public String toString() {
        return "{" +
                "decisionValue=" + decisionValue +
                '}';
    }
}
