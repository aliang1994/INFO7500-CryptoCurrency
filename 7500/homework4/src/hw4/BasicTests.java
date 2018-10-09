package hw4;

import hw4.net.SynchronousExecutor;
import hw4.net.*;
import hw4.net.NetworkUtil;
import hw4.consensus.bft.UnauthBFTNode;
import hw4.consensus.follow.FollowLeaderMaliciousNode;
import hw4.consensus.follow.FollowLeaderNode;
import hw4.consensus.majority.MajorityVotingMaliciousNode;
import hw4.consensus.majority.MajorityVotingNode;
import junit.framework.TestCase;
import org.junit.Test;

import static hw4.net.Constants.VALUE_0;
import static hw4.net.Constants.VALUE_1;
import static hw4.net.Constants.VALUE_SET;

public class BasicTests extends TestCase {

    public BasicTests() {
        super(null);
    }
    
    
    // follow leader node -- test
    @Test
    public void test0() {
        int NUM_ROUNDS = 5;
        int NUM_NODES = 4;

        Network net = new Network();
        net.newNodes(FollowLeaderNode.class, NUM_NODES);

        Node leader = net.getNodes().get(0);
        net.setLeader(leader)
                .setValueSet(VALUE_SET)
                .setDefaultValue(VALUE_0)
                .setRounds(NUM_ROUNDS);
        leader.setLeaderInitialValue(VALUE_1);

        NetworkUtil.completeGraph(net);

        SynchronousExecutor e = new SynchronousExecutor(net).run();
        assertTrue(e.isAgreementSatisfied());
        assertTrue(e.isValiditySatisfied());
    }
    
    // follow leader malicious -- test leader is malicious
    @Test
    public void test1() {
        int NUM_ROUNDS = 5;
        int NUM_NODES = 4;

        Network net = new Network();
        net.newNodes(FollowLeaderNode.class, NUM_NODES-1);
        Node malicious = net.newNode(FollowLeaderMaliciousNode.class);        

        Node leader = malicious;
        net.setLeader(leader)
                .setValueSet(VALUE_SET)
                .setDefaultValue(VALUE_0)
                .setRounds(NUM_ROUNDS);
        leader.setLeaderInitialValue(VALUE_1);

        net.setMalicious(malicious);

        NetworkUtil.completeGraph(net);

        SynchronousExecutor e = new SynchronousExecutor(net).run();
        assertFalse(e.isAgreementSatisfied());
    }

    //majority voting node -- test
    @Test
    public void test2() {
        int NUM_ROUNDS = 5;
        int NUM_NODES = 4;

        Network net = new Network();
        net.newNodes(MajorityVotingNode.class, NUM_NODES);

        Node leader = net.getNodes().get(0);
        net.setLeader(leader)
                .setValueSet(VALUE_SET)
                .setDefaultValue(VALUE_0)
                .setRounds(NUM_ROUNDS);
        leader.setLeaderInitialValue(VALUE_1);

        NetworkUtil.completeGraph(net);

        SynchronousExecutor e = new SynchronousExecutor(net).run();
        assertTrue(e.isAgreementSatisfied());
        assertTrue(e.isValiditySatisfied());
    }
    
    // majority voting malicious -- test
    private void testMajorityMalicious(int NUM_NODES) {
        int NUM_ROUNDS = 3;

        Network net = new Network();
        net.newNodes(MajorityVotingNode.class, NUM_NODES-3);
        MajorityVotingMaliciousNode malicious = net.newNode(MajorityVotingMaliciousNode.class);
        MajorityVotingMaliciousNode sybil1 = net.newNode(MajorityVotingMaliciousNode.class);
        MajorityVotingMaliciousNode sybil2 = net.newNode(MajorityVotingMaliciousNode.class);
        
        malicious.addSybil(sybil1);
        malicious.addSybil(sybil2);//malicious nodes can conspire with each other
        System.out.println("1: "+ malicious.getDefaultValue());

        Node leader = malicious;
        net.setLeader(leader)
                .setValueSet(VALUE_SET)
                .setDefaultValue(VALUE_0)
                .setRounds(NUM_ROUNDS);
        leader.setLeaderInitialValue(VALUE_1);

        net.setMalicious(malicious);
        net.setMalicious(sybil1);
        net.setMalicious(sybil2);

        NetworkUtil.completeGraph(net);

        SynchronousExecutor e = new SynchronousExecutor(net).run();
        
        assertFalse(e.isAgreementSatisfied());
    }

    @Test
    public void test3A() {
        testMajorityMalicious(10);
    }

    @Test
    public void test3B() {
        testMajorityMalicious(11);
    }

    @Test
    public void test3C() {
        testMajorityMalicious(12);
    }

    
    // unauthBFTNode -- test
    @Test
    public void test4() {
        int NUM_ROUNDS = 4;
        int NUM_NODES = 4;

        Network net = new Network();
        net.newNodes(UnauthBFTNode.class, NUM_NODES);

        Node leader = net.getNodes().get(0);
        net.setLeader(leader)
                .setValueSet(VALUE_SET)
                .setDefaultValue(VALUE_0)
                .setRounds(NUM_ROUNDS);
        leader.setLeaderInitialValue(VALUE_1);

        NetworkUtil.completeGraph(net);

        SynchronousExecutor e = new SynchronousExecutor(net).run();
        assertTrue(e.isAgreementSatisfied());
        assertTrue(e.isValiditySatisfied());
    }

    //Add more tests for your BFT implementation
    
}
