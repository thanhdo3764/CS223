import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class TopoSort {

    /**
     * Part 1: Implement this
     *
     * Function to perform topological sort on a given Graph object.
     *
     * If a topological ordering exists, return it.
     * Other return null if:
     *      passed a null
     *      passed a graph with < 1 vertex
     *      passed a graph containing cycles
     *
     * Note that undirected graphs won't have topological orderings,
     * but you do not need to explicitly check for that.
     *
     * @param g A graph to be sorted, or null
     * @return  An iterable object with the ordering, or null
     */
    public static Iterable<Integer> sort(Graph g) {
        // g is not a valid graph if g is null or the number of vertices is less than 1
        if (g == null || g.V() < 1) return null;

        PriorityQueue<Integer> noInboundVertices = new PriorityQueue<Integer>();
        LinkedList<Integer> topologicalOrder = new LinkedList<Integer>();
        // Count the in-degrees for each vertex
        int[] inboundCount = new int[g.V()];
        for (int vertex = 0; vertex < g.V(); vertex++) {
            for (int adjacentVertex : g.adj(vertex)) {
                inboundCount[adjacentVertex]++;
            }
        }
        // Add vertices with no in-degrees to noInboundVertices
        for (int vertex = 0; vertex < g.V(); vertex++) {
            if (inboundCount[vertex] == 0) noInboundVertices.add(vertex);
        }
        // While there are still vertices with 0 in-degrees
        while (!noInboundVertices.isEmpty()) {
            // remove a 0 in-degree vertex and put it next in topologicalOrder
            int vertex = noInboundVertices.remove();
            topologicalOrder.add(vertex);
            // Update the inboundCount for the vertices adjacent to the removed vertex
            for (int adjacentVertex : g.adj(vertex)) {
                inboundCount[adjacentVertex]--;
                // If an adjacentVertex now has a 0 in-degree, add it to noInboundVertices
                if (inboundCount[adjacentVertex] == 0) noInboundVertices.add(adjacentVertex);
            }
        }
        if (topologicalOrder.size() == g.V()) return topologicalOrder;
        return null; // There's a cycle
    }
}
