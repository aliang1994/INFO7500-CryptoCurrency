package hw4.net;

import hw4.util.HashMapList;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.*;

public class SynchronousExecutor {
    private Network network;

    public SynchronousExecutor(Network network) {
        this.network = network;
    }

    public SynchronousExecutor run() {
        network.init();

        HashMap<Id,Node> id2node = new HashMap();
        for (Node n : network.getNodes()) {
            id2node.put(n.getId(), n);
        }

        System.out.println("Running synchronous executor for " + network.getRounds() + " rounds. Leader is " + network.getLeader().getId() + ". Malicious nodes are " + network.getMaliciousNodeIds() + ".");
        System.out.println();
        
        for (int round = 0; round < network.getRounds()+1; round++) {
            HashMapList<Node,Message> messages = new HashMapList();
            for (Node from : network.getNodes()) {
                for (Send send : from.safeSend(round)) {
                    if (network.hasEdge(from, id2node.get(send.getTo()))) {
                        messages.put(id2node.get(send.getTo()), new Message(from.getId(), send));
                    }
                }
            }

            print(round, messages);

            for (Node n : network.getNodes()) {
                n.safeReceive(round, messages.get(n));
            }
        }

        System.out.println("\nCommitting...");

        for (Node n : network.getNodes()) {
            n.safeCommit();
        }

        System.out.println("\nFinal Decisions: ");
        for (Node n : network.getNodes()) {
            System.out.println(n.getId() + " -> " + n.getDecisionValue());
        }

        System.out.println("\nAgreement: " + isAgreementSatisfied());
        System.out.println("Validity: " + isValiditySatisfied());

        return this;
    }

    public boolean isAgreementSatisfied() {

        Set<Value> decisionValues = new HashSet();
        for (Node n : network.getNodes()) {
            if (!network.isMalicious(n)) {
                Value v = n.getDecisionValue();
                if (v == null) {
                    return false;
                }
                decisionValues.add(v);
            }
        }

        System.out.println("Decision values: " + decisionValues);
        return decisionValues.size() == 1;
    }
    
    public boolean isValiditySatisfied() {
        Node leader = network.getLeader();
        if (leader == null) throw new RuntimeException("Leader not set");
        if (network.isMalicious(leader)) return true;

        Value leaderv = leader.getLeaderInitialValue();
        if (leaderv == null) throw new RuntimeException();

        for (Node n : network.getNodes()) {
            if (!network.isMalicious(n)) {
                if (n.getDecisionValue() == null || !n.getDecisionValue().equals(leader.getLeaderInitialValue())) {
                    return false;
                }
            }
        }

        return true;
    }

    public void print(int round, HashMapList<Node,Message> messages) {
        System.out.println("\nROUND " + round);
        if (messages.isEmpty()) {
            System.out.println("No messages sent");
        } else {
            for (List<Message> messageList : messages.values()) {
                for (Message msg : messageList) {
                    System.out.println(msg);
                }
            }
        }
    }
}
