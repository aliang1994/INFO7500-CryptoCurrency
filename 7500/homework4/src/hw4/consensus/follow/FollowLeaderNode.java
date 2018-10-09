package hw4.consensus.follow;

import hw4.net.Message;
import hw4.net.Send;
import hw4.net.Id;
import hw4.net.Node;
import hw4.net.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FollowLeaderNode extends Node {
    private Value receivedLeaderDecisionValue;
    private boolean isLeaderAndSentInitialValue;

    public FollowLeaderNode() {

    }

    @Override
    public List<Send> send(int round) {
        if (getIsLeader()) {
            if (getLeaderInitialValue() == null) {
                throw new RuntimeException("Leader decision not set");
            }

            if (!isLeaderAndSentInitialValue) {
                List<Send> sends = new ArrayList();
                for (Id to : getPeerIds()) {
                    sends.add(new Send(to, new FollowLeaderPayload(getLeaderInitialValue())));
                }

                isLeaderAndSentInitialValue = true;
                return sends;
            }
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public void receive(int round, List<Message> messages) {
        if (!getIsLeader()) {
            if (this.receivedLeaderDecisionValue == null) {
                for (Message m : messages) {
                    if (m.getFrom().equals(getLeaderNodeId())) {
                        FollowLeaderPayload payload = m.getSend().getPayload(FollowLeaderPayload.class);
                        if (payload != null) {
                            this.receivedLeaderDecisionValue = payload.getDecisionValue();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void commit() {
        if (getIsLeader()) {
            setDecisionValue(getLeaderInitialValue());
        } else {
            setDecisionValue(receivedLeaderDecisionValue);
        }
    }
}
