package hw4.net;

import hw4.consensus.bft.UnauthBFTNode;
import hw4.consensus.follow.FollowLeaderMaliciousNode;
import hw4.consensus.follow.FollowLeaderNode;
import hw4.consensus.majority.MajorityVotingMaliciousNode;
import hw4.net.Value;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Constants {

    public static final Value VALUE_0 = new Value(0);
    public static final Value VALUE_1 = new Value(1);

    public static final Set<Value> VALUE_SET = Collections.unmodifiableSet(new HashSet(Arrays.asList(
            VALUE_0,
            VALUE_1)));
}
