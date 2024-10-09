import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.AbstractMap;
/**
 * MazeGenerator
 *
 *
 * Given a graph in which vertices are arranged in
 * a grid, we can describe a maze in the following fashion:
 *
 *  (1) create a graph of the grid with a given height and width
 *  (2) build a Spanning Tree (ST) on this graph representing corridors in the maze
 *  (3) select vertices on the ST for the maze start and end points
 *       !! for this project the start point must be on the left side of the ST graph
 *          and the end point must be on the right side of the ST graph !!
 *
 *  We can then use the ST to build a corresponding graph that represents
 *  the walls of the maze.  This graph is the dual of the ST we just built.
 *  The 'maze walls' graph has one more row and one more column than the 'corridors' graph
 *  If we layout the 'maze walls' graph in a grid, the 'corridors' graph verticies can be
 *  placed in-between rows and columns of the 'maze walls' graph.  See the handout
 *  for details.  To do this we:
 *
 *  (1) begin with a graph whose height and width are one larger than the ST graph
 *  (2) remove edges in the wall graph the cross an edge in the corridors graph
 *  (3) remove an edge on the left side to allow an "entrance"
 *  (4) remove an edge on the right side to allow an "exit"
 *
 *  In this project, you'll build both the spanning tree for a maze and the corresponding
 *  graph to represent the walls of the maze.
 */
public class MazeGenerator {

    static class mazeGraph implements GridGraph {
        private final int height;
        private final int width;
        private final int verticesCount;
        private final ArrayList<HashSet<Integer>> mazeAdjacency = new ArrayList<HashSet<Integer>>();

        public mazeGraph(LabeledGridGraph spanningTree) {
            height = spanningTree.getHeight() + 1;
            width = spanningTree.getWidth() + 1;
            verticesCount = height * width;
            // Initialize spanningTreeAdjacency of a full gridGraph
            for (int i = 0; i < verticesCount; i++) {
                mazeAdjacency.add(new HashSet<Integer>());
            }
            for (int i = 0; i < verticesCount; i++) {
                if (i+width < verticesCount) {
                    mazeAdjacency.get(i).add(i + width);
                    mazeAdjacency.get(i + width).add(i);
                }
                if (i/width == (i + 1)/width) {
                    mazeAdjacency.get(i).add(i + 1);
                    mazeAdjacency.get(i + 1).add(i);
                }
            }
            removeWalls(spanningTree);
        }

        public void removeWalls(LabeledGridGraph spanningTree) {
            // Goes through all vertices of spanningTree and their adjacency and deletes maze walls
            for (int spanningTreeVertex = 0; spanningTreeVertex < spanningTree.nVertices(); spanningTreeVertex++) {
                for (int adjacentVertex : spanningTree.adjacent(spanningTreeVertex)) {
                    // This checks for repeat edges and skips them
                    if (adjacentVertex < spanningTreeVertex) continue;
                    // Determine what row and col the spanningTreeVertex lies on
                    int spanningTreeVertexRow = spanningTreeVertex / spanningTree.getWidth();
                    int spanningTreeVertexCol = spanningTreeVertex % spanningTree.getWidth();
                    // Deleting vertical walls
                    if (adjacentVertex - spanningTreeVertex == 1) {
                        int removeFrom = spanningTreeVertexRow*width + spanningTreeVertexCol + 1;
                        mazeAdjacency.get(removeFrom).remove(removeFrom + width);
                        mazeAdjacency.get(removeFrom + width).remove(removeFrom);
                    }
                    // Deleting horizontal walls
                    if (adjacentVertex - spanningTreeVertex == spanningTree.getWidth()) {
                        int removeFrom = (spanningTreeVertexRow + 1)*width + spanningTreeVertexCol;
                        mazeAdjacency.get(removeFrom).remove(removeFrom + 1);
                        mazeAdjacency.get(removeFrom + 1).remove(removeFrom);
                    }
                }
            }
            // Delete start wall
            int startRow = (spanningTree.getVertices("start").get(0))/(spanningTree.getWidth());
            int startVertex = startRow * width;
            mazeAdjacency.get(startVertex).remove(startVertex + width);
            mazeAdjacency.get(startVertex + width).remove(startVertex);
            // Delete end wall
            int endRow = (spanningTree.getVertices("end").get(0))/(spanningTree.getWidth());
            int endVertex = endRow*width + width - 1;
            mazeAdjacency.get(endVertex).remove(endVertex + width);
            mazeAdjacency.get(endVertex + width).remove(endVertex);
        }

        public int getHeight() {
            return height;
        }

        public int getWidth() {
            return width;
        }

        public List<Integer> adjacent(int vertex) {
            return new ArrayList<Integer>(mazeAdjacency.get(vertex));
        }

        public int nVertices() {
            return verticesCount;
        }
    }

    static class spanningTreeGraph implements LabeledGridGraph {
        private int height;
        private int width;
        private int verticesCount;
        private List<Integer> startVertex = new ArrayList<Integer>();
        private List<Integer> endVertex = new ArrayList<Integer>();
        private ArrayList<HashSet<Integer>> spanningTreeAdjacency = new ArrayList<HashSet<Integer>>();

        public spanningTreeGraph(int width, int height) {
            this.width = width;
            this.height = height;
            this.verticesCount = width*height;
            // Choose a random but valid start and end vertex
            Random r = new Random();
            startVertex.add(r.nextInt(height)*width);
            endVertex.add((r.nextInt(height) + 1)*width - 1);
            // Initialize spanningTreeAdjacency
            for (int i = 0; i < verticesCount; i++) {
                spanningTreeAdjacency.add(new HashSet<Integer>());
            }
            generateGraph(width, height);
        }

        public void generateGraph(int width, int height) {
            Random r = new Random();
            ArrayList<Integer> verticesToAdd = new ArrayList<Integer>();
            // Adds all the vertices to a list
            for (int i = 0; i < verticesCount; i++) {
                verticesToAdd.add(i);
            }
            // Select a random initial vertex to be in the spanning tree and remove it from verticesToAdd
            int initialSpanningTreeVertex = verticesToAdd.remove(r.nextInt(verticesToAdd.size()));
            // Give initialSpanningTreeVertex a placeholder adjacency so isInMaze() returns true
            spanningTreeAdjacency.get(initialSpanningTreeVertex).add(-1);
            // Add the rest of the verticesToAdd to the spanning tree
            while (!verticesToAdd.isEmpty()) {
                // Create Stack for cases when the path hits a dead end
                Stack<Integer> path = new Stack<Integer>();
                // Select a starting point for the random walk
                Integer vertex = verticesToAdd.remove(r.nextInt(verticesToAdd.size()));
                // A list of edges to add to spanningTreeAdjacency later
                ArrayList<AbstractMap.SimpleEntry<Integer,Integer>> edgeList = new ArrayList<AbstractMap.SimpleEntry<Integer,Integer>>();
                while (true) {
                    // Get the next vertex to travel to
                    Integer nextVertex =  randomAdjacent(vertex, verticesToAdd);
                    // Backtrack to the previous vertex if the path is a dead end
                    if (nextVertex == -1) {
                        vertex = path.pop();
                        continue;
                    }
                    verticesToAdd.remove(nextVertex);
                    // Repeat process of selecting random vertex if nextVertex is in the spanningTree
                    if (isInSpanningTree(nextVertex)) {
                        edgeList.add(new AbstractMap.SimpleEntry<Integer,Integer>(vertex,nextVertex));
                        break;
                    }
                    // Add the edge to the edgelist
                    edgeList.add(new AbstractMap.SimpleEntry<Integer,Integer>(vertex,nextVertex));
                    // Continue forward in the path by making vertex = nextVertex
                    path.push(vertex);
                    vertex = nextVertex;
                }
                // The path finally meets the spanning tree, so put the edges in edgeList into spanningTreeAdjacency
                for (AbstractMap.SimpleEntry v :edgeList) {
                    spanningTreeAdjacency.get((int) v.getKey()).add((int) v.getValue());
                    spanningTreeAdjacency.get((int) v.getValue()).add((int) v.getKey());
                }
            }
            // Remove the placeholder edge for the initialSpanningTreeVertex
            spanningTreeAdjacency.get(initialSpanningTreeVertex).remove(-1);
        }

        public int randomAdjacent(int vertex, ArrayList<Integer> availableVertices) {
            Random r = new Random();
            ArrayList<Integer> adjacentVertices = new ArrayList<Integer>();
            // Check if vertex above can be adjacent and add it to adjacentVertices
            if (availableVertices.contains(vertex-width) || isInSpanningTree(vertex-width)) {
                adjacentVertices.add(vertex-width);
            }
            // Check if vertex below can be adjacent and add it to adjacentVertices
            if (availableVertices.contains(vertex+width) || isInSpanningTree(vertex+width)) {
                adjacentVertices.add(vertex+width);
            }
            // Check if vertex left can be adjacent and add it to adjacentVertices
            if (vertex % width != 0 && (availableVertices.contains(vertex-1) || isInSpanningTree(vertex-1))) {
                adjacentVertices.add(vertex-1);
            }
            // Check if vertex right can be adjacent and add it to adjacentVertices
            if (vertex % width != width-1 && (availableVertices.contains(vertex+1) || isInSpanningTree(vertex+1))) {
                adjacentVertices.add(vertex+1);
            }
            // If there aren't any valid adjacent vertices, return -1, else, return a random adjacent
            return adjacentVertices.isEmpty() ? -1:adjacentVertices.get(r.nextInt(adjacentVertices.size()));
        }

        public boolean isInSpanningTree(int vertex) {
            if (vertex < 0 || vertex >= verticesCount) return false;
            return !spanningTreeAdjacency.get(vertex).isEmpty();
        }

        public int getHeight() {
            return height;
        }

        public int getWidth() {
            return width;
        }

        public List<Integer> adjacent(int v) {
            return new ArrayList<Integer>(spanningTreeAdjacency.get(v));
        }

        public int nVertices() {
            return verticesCount;
        }

        public List<Integer> getVertices(String label) {
            List<Integer> copyOfVertices = new ArrayList<>();
            if (label.equals("start")) copyOfVertices.addAll(startVertex);
            else if (label.equals("end")) copyOfVertices.addAll(endVertex);
            else return null;
            return copyOfVertices;
        }

        public String getLabel(int v) {
            if (startVertex.get(0).equals(v)) return "start";
            else if (endVertex.get(0).equals(v)) return "end";
            else return null;
        }
    }

    /**
     * Part 1: Implement this method.
     *
     * Given a height and width, generate a graph in which vertices
     * are arranged in a rectangular grid in row-major order (vertices
     * labeled 0 ... (height*width-1) starting at the upper left
     * corner and moving left to right and top to bottom).
     *
     * Return a spanning tree on this graph which can be thought
     * of as the corridors of a maze.
     *
     * IMPORTANT: The graph/spanning tree that is returned MUST be sure
     * to define vertex on the left edge of the graph as a
     * starting location, and a vertex on the right edge of
     * the graph as an end location.
     *
     * @param width   the width of the maze
     * @param height  the height of the maze
     * @return        a spanning tree over the grid
     */
    public static LabeledGridGraph generateST(int width, int height) {
        return new spanningTreeGraph(width,height);
    }

    /**
     * Part 2: Implement this method
     *
     *  Given a GridGraph representing the corridors of a maze,
     *  build the corresponding graph representing the walls of the
     *  maze.
     *
     *  If the input graph is defined on a grid of size
     *  height x width, the output graph is defined on a grid
     *  of size (height + 1) x (width + 1).  That is, the
     *  graph of 'walls' has one more row and one more column
     *  than the graph of 'corridors'. See handout for details.
     *
     *  Note further, that we assume the input GridGraph has
     *  defined start and end vertices which can be accessed through
     *  the GridGraph's getStartVertex() and getEndVertex() methods,
     *  and further that the start vertex is a vertex on the spanning
     *  tree's left edge, and the end vertex is a vertex on the
     *  spanning tree's right edge.
     *
     *  The graph representing the maze should have all exterior
     *  walls intact except for one along the left edge (allowing
     *  an entrance) and one along the right edge (allowing an
     *  exit). For the maze (graph of edges)
     *  there is no starting or ending vertex, so the instance
     *  of GridGraph returned by this method should return -1
     *  when either the getStartVertex() or getEndVertex() methods
     *  are called.
     *
     * @param st
     * @return
     */
    public static GridGraph generateMaze(LabeledGridGraph st) {
        return new mazeGraph(st);
    }

    /**
     * Saves a maze as a text file. The format is as follows;
     * The first line contains two integers indicating the height and width
     * Vertices are labeled 0 ... (height*width-1) starting at the top
     * left corner and proceeding to the right and down.
     *
     * The remaining lines of the file indicate edges
     * each line contains two integers indicating an edge between
     * those two vertices.
     *
     * @param fname - the name of the text file to save typically ending in '-maze.txt'
     * @param maze - a graph representing the maze
     */
    public static void saveMaze(String fname, GridGraph maze) {
        try {
            FileWriter out = new FileWriter(new File(fname));
            out.write(maze.getWidth() + " " + maze.getHeight() + "\n");
            System.out.println("writing graph...");
            int nVertices = maze.nVertices();
            for(int v1 = 0; v1 < nVertices; v1++) {
                // maze.adjacent() should NOT return null, per the
                // interface description...it may return an empty list.
                for(int v2 : maze.adjacent(v1)) {
                    out.write(v1 + " " + v2 + "\n");
                }
            }
            out.close();
        } catch (IOException e) {
        }
    }

    /**
     * Saves a ST as a text file. The format is as follows;
     * The first line contains two integers indicating the height and width
     * Vertices are labeled 0 ... (height*width-1) starting at the top
     * left corner and proceeding to the right and down
     *
     * The following lines of the file indicate edges
     * each line contains two integers indicating an edge between
     * those two vertices.
     *
     * The final two lines indicate the start and end locations on the ST of the
     * maze.  The start location must be a vertex along the left edge of the graph
     * and the end location must be a vertex along the right edge of the graph.
     *
     * @param fname - the name of the text file to save typically ending in '-maze.txt'
     * @param st - a spanning tree of the maze
     */
    public static void saveST(String fname, LabeledGridGraph st) {
        try {
            FileWriter out = new FileWriter(new File(fname));
            out.write(st.getWidth() + " " + st.getHeight() + "\n");
            System.out.println("writing graph...");
            List<Integer> labeledV;
            labeledV = st.getVertices("start");
            if (labeledV == null || labeledV.size() != 1) {
                System.err.println("'start' vertices look problematic. This graph should have exactly one vertex labeled 'start'");
            }
            labeledV = st.getVertices("end");
            if (labeledV == null || labeledV.size() != 1) {
                System.err.println("'end' vertices look problematic. This graph should have exactly one vertex labeled 'end'");
            }

            int nVertices = st.nVertices();
            for(int v1 = 0; v1 < nVertices; v1++) {
                for(int v2 : st.adjacent(v1)) {
                    out.write(v1 + " " + v2 + "\n");
                }
            }
            out.write( -1 + " " + (st.getVertices("start").get(0)) + " gray\n");
            out.write( -1 + " " + (st.getVertices("end").get(0)) + " gray\n");
            out.close();
        } catch (IOException e) {
        }
    }

    /**
     *
     * @param args <height> <width> <maze file name>
     */
    public static void main(String[] args) {
        int stW = Integer.parseInt(args[0]);
        int stH = Integer.parseInt(args[1]);
        LabeledGridGraph st = MazeGenerator.generateST(stW, stH);
        GridGraph maze = MazeGenerator.generateMaze(st);
        MazeGenerator.saveST(args[0] + "-st.txt", st);
        MazeGenerator.saveMaze(args[0] + "-maze.txt", maze);

    }
}