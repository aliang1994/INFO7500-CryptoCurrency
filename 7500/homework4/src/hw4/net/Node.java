package hw4.net;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.*;

public abstract class Node {
    private Id id;
    private Value decisionValue;
    private Set<Value> valueSet;
    private Value leaderInitialValue;
    private Value defaultValue;

    private List<Id> peerIds = new ArrayList();
    private Id leaderNodeId;
    private Integer rounds;

    public Node() {

    }

    void setId(Id id) {
        this.id = id;
    }

    Node setDefaultValue(Value defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public Value getDefaultValue() {
        return this.defaultValue;
    }

    public Value getLeaderInitialValue() {
        return leaderInitialValue;
    }

    public void setLeaderInitialValue(Value leaderInitialValue) {
        this.leaderInitialValue = leaderInitialValue;
    }

    void setValueSet(Set<Value> valueSet) {
        this.valueSet = valueSet;
    }

    public Set<Value> getValueSet() {
        return new HashSet(valueSet);
    }

    void addPeerId(Id adj) {
        this.peerIds.add(adj);
    }

    public List<Id> getPeerIds() {
        return Collections.unmodifiableList(peerIds);
    }

    protected void setDecisionValue(Value decisionValue) {
        this.decisionValue = decisionValue;
    }

    Value getDecisionValue() {
        return this.decisionValue;
    }

    public boolean getIsLeader() {
        return Objects.equals(leaderNodeId, id);
    }

    public Id getId() {
        return id;
    }

    public Id getLeaderNodeId() {
        return leaderNodeId;
    }

    Node setLeaderNodeId(Id leaderNodeId) {
        this.leaderNodeId = leaderNodeId;
        return this;
    }

    public Integer getRounds() {
        return rounds;
    }

    Node setRounds(Integer rounds) {
        this.rounds = rounds;
        return this;
    }

    public List<Id> getReachableNodes() {
        List<Id> reachableNodeIds = new ArrayList(getPeerIds());
        reachableNodeIds.add(getId());
        Collections.sort(reachableNodeIds);
        return reachableNodeIds;
    }

    public final List<Send> safeSend(int round) {
        try {
            List<Send> l = send(round);
            return l == null ? Collections.EMPTY_LIST : l;
        } catch (Throwable t) {
            System.out.println(ExceptionUtils.getStackTrace(t));
            return Collections.EMPTY_LIST;
        }
    }

    public final void safeReceive(int round, List<Message> messages) {
        try {
            receive(round, new ArrayList(messages));
        } catch (Throwable t) {
            System.out.println(ExceptionUtils.getStackTrace(t));
        }
    }

    public final void safeCommit() {
        try {
            commit();
        } catch (Throwable t) {
            System.out.println(ExceptionUtils.getStackTrace(t));
        }
    }


    public abstract void commit();

    public abstract List<Send> send(int round);

    public abstract void receive(int round, List<Message> messages);

}
