package hw4.net;

import java.util.List;

public class NetworkUtil {

    public static void completeGraph(Network net) {
        List<Node> nodes = net.getNodes();

        for (int i = 0; i < nodes.size(); i++) {
            for (int j = i + 1; j < nodes.size(); j++) {
                Node ni = nodes.get(i);
                Node nj = nodes.get(j);
                net.addUndirectedEdge(ni, nj);
            }
        }
    }
}
