import java.util.HashMap;
import java.util.ArrayList;
public class EdgeWeightedDiGraph extends WeightedGraph {
    /**
     * You will need some variables for internal storage,
     * but the details are up to you. You may make use
     * of build in Java data structures to provide array/list
     * and map(/symbol table/dictionary) support,
     * or implement your own.
     *
     * If you implement your own classes for internal storage,
     * make sure to include them in this file. You can only
     * submit one file to Autolab, and it doesn't know how
     * to unpack things.
     */
    public ArrayList<HashMap<Integer,Double>> adjacencyHashMap;
    public int edgeCount;
    /**
     * Part 1: Implement this
     *
     * Make sure to call super(V) before doing anything else.
     * Do any other initialization work you need here.
     *
     * As with the example in the slides, we assume that we will
     * be given a number of vertices when we create the object,
     * and that this cannot be changed after the fact.
     *
     * @param V -- Number of vertices in the graph
     */
    public EdgeWeightedDiGraph(int V) {
        super(V);
        edgeCount = 0;
        adjacencyHashMap = new ArrayList<HashMap<Integer,Double>>();
        for (int i = 0; i < V; i++) {
            adjacencyHashMap.add(new HashMap<Integer,Double>());
        }
    }

    /**
     * Part 3: Implement this
     *
     * Do something sensible here. An instance variable you can return
     * in this method is probably a good call.
     *
     * Be sure that you deal with the rules for updating edge count correctly.
     *
     * @return -- Total number of edges in the graph
     */
    public int E() {
        return edgeCount;
    }

    /**
     * Part 2: Implement this
     *
     * This method is used to add an edge or update its weight.
     *
     * Throw an IndexOutOfBoundsException if the user specifies
     * a starting or ending vertex that does not exist.
     *
     * If an edge already exists between v and u, replace its weight.
     * At no time should you end up with more than one edge between
     * any pair of vertices.
     *
     * Loops are not permitted. Throw an IllegalArgumentException
     * if the user tries to specify a loop (i.e. if v == u).
     *
     * negative edge weights are permitted.
     *
     * Replacing a weight should not affect the number of edges
     * in the graph.
     *
     * @param v -- Starting vertex of the edge
     * @param u -- Ending vertex of the edge
     * @param w -- Weight to be given to the edge
     */
    public void addEdge(int v, int u, double w) {
        if (v==u) throw new IllegalArgumentException();
        if (v < 0 || v >= numVertices || u < 0 || u >= numVertices) throw new IndexOutOfBoundsException();
        if (!adjacencyHashMap.get(v).containsKey(u)) edgeCount++;
        adjacencyHashMap.get(v).put(u,w);
    }

    /**
     * Part 5: Implement this
     *
     * Remove the specified edge from the graph, assuming it exists.
     *
     * Throw an IndexOutOfBoundsException if the user specifies
     * a starting or ending vertex that does not exist.
     *
     * If the specified edge does not exist, do nothing.
     *
     * @param v -- Starting vertex of the edge
     * @param u -- Ending vertex of the edge
     */
    public void removeEdge(int v, int u) {
        if (v < 0 || v >= numVertices || u < 0 || u >= numVertices) throw new IndexOutOfBoundsException();
        if (adjacencyHashMap.get(v).containsKey(u)) edgeCount--;
        adjacencyHashMap.get(v).remove(u);
    }

    /**
     * Part 4: Implement this
     *
     * Return the weight of the edge from v to u, or null if no such
     * edge exists.
     *
     * Throw an IndexOutOfBoundsException if the user specifies
     * a starting or ending vertex that does not exist.
     *
     * @param v -- Starting vertex of the edge
     * @param u -- Ending vertex of the edge
     * @return -- Weight of edge (v,u) or null if no edge exists
     */
    @Override public Double edgeWeight(int v, int u) {
        if (v < 0 || v >= numVertices || u < 0 || u >= numVertices) throw new IndexOutOfBoundsException();
        if (!adjacencyHashMap.get(v).containsKey(u)) return null;
        return adjacencyHashMap.get(v).get(u);
    }

    /**
     * Part 6: Implement this
     *
     * Return an iterable object containing the vertices
     * adjacent to v.
     *
     * DO NOT include edge weights in the results.
     *
     * Throw an IndexOutOfBoundsException if the user
     * specifies a vertex that does not exist.
     *
     * Return an empty iterable object if the vertex has
     * no adjacent vertices.
     *
     * @param v -- Vertex whose adjacenies we wish to know
     * @return -- Iterable object with vertices adjacent to v
     */
    public Iterable<Integer> adj(int v) {
        if (v < 0 || v >= numVertices) throw new IndexOutOfBoundsException();
        // Returns a set of vertices that were directed from v
        return adjacencyHashMap.get(v).keySet();
    }
}
