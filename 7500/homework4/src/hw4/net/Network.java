package hw4.net;

import hw4.util.HashMapList;

import java.util.*;

public class Network {
    private List<Node> nodes = new ArrayList();
    private Node leader;
    private Value defaultValue;
    private Set<Value> valueSet;
    private Integer rounds;
    private List<Node> malicious = new ArrayList();
    private HashMapList<Node,Node> edges = new HashMapList();

    public Network setMalicious(Node n) {
        this.malicious.add(n);
        return this;
    }

    public boolean hasEdge(Node n1, Node n2) {
        return edges.get(n1).contains(n2) || n1.equals(n2);
    }

    public void addUndirectedEdge(Node n1, Node n2) {
        this.edges.put(n1, n2);
        this.edges.put(n2, n1);
    }

    public boolean isMalicious(Node n) {
        return this.malicious.contains(n);
    }

    public List<Node> getNodes() {
        return Collections.unmodifiableList(nodes);
    }

    public <T extends Node> void newNodes(Class<T> nodeClass, int numNodes) {
        for (int i = 0; i < numNodes; i++) {
            newNode(nodeClass);
        }
    }

    public <T extends Node> T newNode(Class<T> nodeClass) {
        try {
            T n = nodeClass.newInstance();
            n.setId(new Id(nodes.size()+1));
            nodes.add(n);
            return n;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public Network setLeader(Node leader) {
        this.leader = leader;
        return this;
    }
    public Network setValueSet(Set<Value> valueSet) {
        this.valueSet = new HashSet(valueSet);
        return this;
    }

    public Network setDefaultValue(Value defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public void init() {
        for (Node n : getNodes()) {
            n.setValueSet(valueSet);
            n.setDefaultValue(defaultValue);
            n.setLeaderNodeId(leader.getId());
            n.setRounds(rounds);
            for (Node n2 : edges.get(n)) {
                n.addPeerId(n2.getId());
            }
        }
    }

    public Node getLeader() {
        return leader;
    }

    public List<Id> getMaliciousNodeIds() {
        List<Id> maliciousNodeIds = new ArrayList();
        for (Node n : malicious) {
            maliciousNodeIds.add(n.getId());
        }
        return maliciousNodeIds;
    }

    public Integer getRounds() {
        return rounds;
    }

    public Network setRounds(Integer rounds) {
        this.rounds = rounds;
        return this;
    }
}
