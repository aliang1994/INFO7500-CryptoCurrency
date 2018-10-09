package hw4.consensus.majority;

import hw4.net.Message;
import hw4.net.Send;
import hw4.net.Id;
import hw4.net.Node;
import hw4.net.Value;
import hw4.util.HashMapList;

import java.util.*;

public class MajorityVotingNode extends Node {

    private boolean isLeaderAndSentInitialValue;
    private Value receivedLeaderDecisionValue;
    private boolean hasRelayedLeaderValue;
    private Map<Id,Value> peerId2ReceivedLeaderDecisionValue = new HashMap();

    public MajorityVotingNode() {

    }

    @Override
    public List<Send> send(int round) {
        List<Send> sends = new ArrayList();

        if (getIsLeader()) {
            if (getLeaderInitialValue() == null) {
                throw new RuntimeException("Leader decision not set");
            }

            if (!isLeaderAndSentInitialValue) { // leader sends decisions to peers
                for (Id to : getPeerIds()) {
                    sends.add(new Send(to, new MajorityVotingPayload(getLeaderInitialValue())));
                }

                isLeaderAndSentInitialValue = true;
            }
        } 
        else {  //if the node is not a leader
            if (receivedLeaderDecisionValue != null) {
                if (hasRelayedLeaderValue) {
                    //Do nothing. Already relayed leader value;
                } 
                else {
                    for (Id nodeid : getPeerIds()) {  //nodes send each other relayed values
                        sends.add(new Send(nodeid, new MajorityVotingPayload(receivedLeaderDecisionValue)));
                    }
                    hasRelayedLeaderValue = true;
                }
            } 
            else {
                //Do nothing. Haven't heard from leader.
            }
        }
        return sends;
    }

    @Override
    public void receive(int round, List<Message> messages) {
        if (getIsLeader()) { // if is leader, not receiving anything

        } 
        else {
            for (Message m : messages) {
                MajorityVotingPayload payload = m.getSend().getPayload(MajorityVotingPayload.class);
                if (payload != null) {
                    if (m.getFrom().equals(getLeaderNodeId())) { // received from leader
                        if (receivedLeaderDecisionValue == null) {
                            receivedLeaderDecisionValue = payload.getDecisionValue();
                            peerId2ReceivedLeaderDecisionValue.put(m.getFrom(), payload.getDecisionValue());
                        }
                    } 
                    else {
                        if (!peerId2ReceivedLeaderDecisionValue.containsKey(m.getFrom())) {
                            peerId2ReceivedLeaderDecisionValue.put(m.getFrom(), payload.getDecisionValue());
                        }
                    }
                }
            }
        }
    }

    @Override
    public void commit() {
        if (getIsLeader()) { // if is leader
            setDecisionValue(getLeaderInitialValue());
        } 
        else {
            int majority = getPeerIds().size()/2+1;
            HashMapList<Value,Id> value2votes = new HashMapList(); // des_value, list<id>
            for (Id nodeid : getPeerIds()) {
                Value nv = peerId2ReceivedLeaderDecisionValue.get(nodeid);
                if (nv == null) {
                    nv = getDefaultValue();
                }
                value2votes.put(nv, nodeid);
            }
            System.out.println("Node " + getId() + "-> FromLeader: " + receivedLeaderDecisionValue + "; PeerVotes: " + peerId2ReceivedLeaderDecisionValue);

            boolean hasMajority = false;
            for (Value v : value2votes.keySet()) { // all values
                if (value2votes.get(v).size() >= majority) {
                    setDecisionValue(v);
                    hasMajority = true;
                    break;
                }
            }

            if (!hasMajority) {
                System.out.println("\tNo majority. Use default.");
                setDecisionValue(getDefaultValue());
            }
        }
    }
}
