import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.*;

public class TopoSortTests {

    @Test
    public void testNullGraph() {
        assertNull(TopoSort.sort(null));
    }

    @Test
    public void testEmptyGraph() {
        Graph g = new AdjMatrixDiGraph(0);
        assertNull(TopoSort.sort(g));
    }

    @Test
    public void testSingleVertex() {
        Graph g = new AdjMatrixDiGraph(1);
        Iterable<Integer> order = TopoSort.sort(g);
        assertNotNull(order);
        for(int v: order) {
            assertEquals(v, 0);
        }
    }

    @Test
    public void testSingleVertexLoop() {
        Graph g = new AdjMatrixDiGraph(1);
        g.addEdge(0, 0);
        Iterable<Integer> order = TopoSort.sort(g);
        assertNull(order);
    }

    @Test
    public void testSingleOrdering() {
        // this test uses the first example from the slides, omitting F
        Graph g = new AdjMatrixDiGraph(5);
        g.addEdge(0, 1);
        g.addEdge(0, 3);
        g.addEdge(1, 2);
        g.addEdge(2, 3);
        g.addEdge(2, 4);
        g.addEdge(3, 4);

        LinkedList<Integer> expOrder = new LinkedList<Integer>();
        for(int i = 0; i < 5; i++) expOrder.add(i);

        Iterable<Integer> order = TopoSort.sort(g);
        assertNotNull(order);

        for(int v: order) {
            assertEquals((Integer)v, (Integer)expOrder.remove());
        }
    }

    @Test
    public void testSimpleCycle() {
        // this is a simple cycle 0 -> 1 -> 2 -> 3 -> 0
        Graph g = new AdjMatrixDiGraph(4);
        g.addEdge(0, 1);
        g.addEdge(1, 2);
        g.addEdge(2, 3);
        g.addEdge(3, 0);

        Iterable<Integer> order = TopoSort.sort(g);
        assertNull(order);
    }

    @Test
    public void testMultipleOrderingsV1() {
        // this test uses the first example from the slides, including F
        Graph g = new AdjMatrixDiGraph(5);
        g.addEdge(0, 1);
        g.addEdge(0, 3);
        g.addEdge(1, 2);
        g.addEdge(2, 3);
        g.addEdge(2, 4);
        g.addEdge(3, 4);

        Iterable<Integer> order = TopoSort.sort(g);
        assertNotNull(order);

        /*
         * Vertex 5 (i.e. F) could reasonably fit anywhere in the order
         * The other vertices have the property that each vertex must
         * be before any vertex with a higher number, so we test that here
         * First, figure out where everyone is in the ordering, then make
         * sure the property holds.
         */
        int[] indices = new int[5];
        int index = 0;
        for(int v: order) {
            indices[v] = index;
            index++;
        }
        for(int i = 0; i < 3; i++) {
            assertTrue(indices[i] < indices[i+1]);
        }
    }

    @Test
    public void testMultipleOrderingsV2(){
        // this test uses the first example from the slides, including F
        AdjMatrixDiGraph g = new AdjMatrixDiGraph(6);
        g.addEdge(0, 1);
        g.addEdge(0, 3);
        g.addEdge(1, 2);
        g.addEdge(2, 3);
        g.addEdge(2, 4);
        g.addEdge(3, 4);
        AdjMatrixDiGraph gold = new AdjMatrixDiGraph(g);

        Iterable<Integer> order = TopoSort.sort(g);
        assertNotNull(order);
        assertTrue(validateOrdering(gold, order));
    }


    @Test
    public void testClassSchedule() {
        // this test uses the class ordering from the slides
        AdjMatrixDiGraph g = new AdjMatrixDiGraph(17);
        g.addEdge(0, 1);
        g.addEdge(1, 2);
        g.addEdge(1, 3);
        g.addEdge(1, 4);
        g.addEdge(1, 5);
        g.addEdge(1, 7);
        g.addEdge(2, 3);
        g.addEdge(2, 7);
        g.addEdge(2, 8);
        g.addEdge(3, 9);
        g.addEdge(3, 10);
        g.addEdge(3, 14);
        g.addEdge(4, 8);
        g.addEdge(4, 9);
        g.addEdge(4, 10);
        g.addEdge(4, 11);
        g.addEdge(5, 6);
        g.addEdge(6, 11);
        g.addEdge(8, 12);
        g.addEdge(11, 12);
        g.addEdge(11, 15);
        g.addEdge(12, 13);
        AdjMatrixDiGraph gold = new AdjMatrixDiGraph(g);

        Iterable<Integer> order = TopoSort.sort(g);
        assertNotNull(order);
        assertTrue(validateOrdering(gold, order));
    }


    private boolean validateOrdering(Graph g, Iterable<Integer> order) {
        int verts = g.V();

        int[] inbound = new int[verts];
        for(int v = 0; v < verts; v++) {
            for(int u: g.adj(v)) {
                inbound[u]++;
            }
        }

        for(int v: order) {
            if(inbound[v] != 0) {
                return false;
            }
            for(int u: g.adj(v)) {
                inbound[u]--;
            }
        }

        return true;
    }



}
