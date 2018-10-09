package hw4.consensus.bft;

import hw4.consensus.majority.MajorityVotingPayload;
import hw4.net.Id;
import hw4.net.Message;
import hw4.net.Send;
import hw4.net.Value;
import hw4.util.HashMapList;
import hw4.net.Node;

import java.util.*;

public class UnauthBFTNode extends Node {
	private Value receivedLeaderDecisionValue;
    private boolean isLeaderAndSentInitialValue;
    private boolean hasRelayedLeaderValue;
    private Map<Id,Value> peerId2ReceivedLeaderDecisionValue = new HashMap();
    
	private HashMapList<Id,UnauthBFTPayload> EIGTree;

    public UnauthBFTNode() {
    }

    public HashMapList<Id,UnauthBFTPayload> getEIG(){
    	return EIGTree;
    }
    
    public void buildEIG(){
    	int level = this.getRounds();
    	int numnodes = this.getPeerIds().size() + 1;
    	
    	EIGTree tree = new EIGTree(numnodes, level);
    	this.EIGTree = tree.getTree();
    }

	@Override
    public List<Send> send(int round) {
		buildEIG();
		
		List<Send> sends = new ArrayList();

        if (getIsLeader()) {// if node is leader
            if (getLeaderInitialValue() == null) {
                throw new RuntimeException("Leader decision not set");
            }
            if (!isLeaderAndSentInitialValue) {
                for (Id to : getPeerIds()) {
                	Id from = this.getId();
                    List<Id> list = new ArrayList<Id>();
                    list.add(from);
                    list.add(to);
                	Trace tr = new Trace(list);
                	
                	UnauthBFTPayload pl = new UnauthBFTPayload(tr, this.getLeaderInitialValue());
                    sends.add(new Send(to, pl));
                }
                isLeaderAndSentInitialValue = true;
            }
        } 
        else { // if node is not leader
            if (receivedLeaderDecisionValue != null) {
                if (hasRelayedLeaderValue) {
                    //Do nothing. Already relayed leader value;
                } 
                else {
                    for (Id to : getPeerIds()) {
                    	Id from = this.getId();
                        List<Id> list = new ArrayList<Id>();
                        list.add(from);
                        list.add(to);
                    	Trace tr = new Trace(list);
                    	
                    	UnauthBFTPayload pl = new UnauthBFTPayload(tr, receivedLeaderDecisionValue);
                        sends.add(new Send(to, pl));
                    }
                    hasRelayedLeaderValue = true;
                }
            } 
            else {
                //Do nothing. Haven't heard from leader.
            }
        }
        return sends;
        //return Collections.EMPTY_LIST;
    }

    @Override
    public void receive(int round, List<Message> messages) {
    	if (getIsLeader()) {

        } 
        else { // if node is not Leader
            for (Message m : messages) {
            	UnauthBFTPayload payload = m.getSend().getPayload(UnauthBFTPayload.class);
                if (payload != null) {
                    if (m.getFrom().equals(getLeaderNodeId())) {
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
    	if (getIsLeader()) {
            setDecisionValue(getLeaderInitialValue());
        } 
        else {
            int majority = getPeerIds().size()/2+1;
            HashMapList<Value,Id> value2votes = new HashMapList();
            for (Id n : getPeerIds()) {
                Value nv = peerId2ReceivedLeaderDecisionValue.get(n);
                if (nv == null) {
                    nv = getDefaultValue();
                }
                value2votes.put(nv, n);
            }
            System.out.println("Node " + getId() + "-> FromLeader: " + receivedLeaderDecisionValue + "; PeerVotes: " + peerId2ReceivedLeaderDecisionValue);

            boolean hasMajority = false;
            for (Value v : value2votes.keySet()) {
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
