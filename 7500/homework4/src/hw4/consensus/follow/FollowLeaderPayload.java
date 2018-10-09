package hw4.consensus.follow;

import hw4.net.Payload;
import hw4.net.Value;

public class FollowLeaderPayload extends Payload {
    private Value decisionValue;

    public FollowLeaderPayload(Value decisionValue) {
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
