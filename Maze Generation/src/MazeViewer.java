import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Utility class for building visualization of mazes.
 */
public class MazeViewer {
    static final int MARGIN = 10; // Padding around maze
    static final int MAG = 20; // Magnification

    static final int EAST = 1;
    static final int NORTH = 2;
    static final int WEST = 4;
    static final int SOUTH = 8;
    static final int START = 16;
    static final int END = 32;

    static final int BOX = -1;
    static final int CIRCLE = -2;

    /**
     * Write the svg file header
     * @param width - width of the graph (nodes per row)
     * @param height - height of the graph (nodes per column)
     * @param out    - the output file writer
     */
    public static void writeHeader(int width, int height, FileWriter out) throws IOException {
        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n");
        out.write("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n");
        out.write("<svg width=\"" + (width*MAG + 2*MARGIN) + "\" height=\"" + (height*MAG + 2*MARGIN) +
                "\" viewBox=\"0 0 " + (width*MAG + 2*MARGIN) + " " + (height*MAG + 2*MARGIN) + "\" " +
                "xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n");
        out.write("<g opacity=\"0.8\">\n" );

    }

    /**
     * Write the svg file footer
     */
    public static void writeFooter(FileWriter out) throws IOException {
        out.write("</g>\n");
        out.write("</svg>\n");
    }

    /**
     * Draw a shape at a specific vertex and write to an output file.
     * Shapes are intended to only be drawn on the st graph coordinate system.
     *
     * @param m      - the marker BOX | CIRCLE
     * @param st_v  - the st vertex ('corridor vertex') to draw the shape at
     * @param color  - color
     * @param w      - width of the mst graph
     * @param out    - the output file writer
     * @throws IOException
     */
    public static void drawLittleShape( int m, int st_v, String color, int w, FileWriter out) throws IOException {
        int cx = ((st_v % w)*MAG + MARGIN + MAG /2);
        int cy = ((st_v / w)*MAG + MARGIN + MAG /2);

        if (m == BOX) {
            out.write("<rect x=\"" + (cx - MAG / 4) + "\" y=\"" + (cy - MAG / 4) + "\" width=\"" +
                    (MAG / 2) + "\" height=\"" + (MAG / 2) +
                    "\" fill=\"" + color + "\" stroke-width=\"1\" stroke=\"black\" />\n");
        }
        else if (m == CIRCLE) {
            out.write("<circle cx=\"" + cx + "\" cy=\"" + cy + "\" r=\"" +
                    (MAG / 4) + "\" fill=\"" + color + "\" stroke-width=\"1\" stroke=\"black\" />\n");

        }
    }

    /**
     * Draw am edge between two vertices and write the line to a file.
     *
     * @param v1 - vertex 1
     * @param v2 - vertex 2
     * @param w  - width of grid
     * @param mazevertex - true iff this edge is from the 'maze' graph (graph representing walls),
     *                     false if this edge is from the st graph (graph representing corridors)
     * @param out        - file output writer
     * @throws IOException
     */
    public static void drawWall(int v1, int v2, int w, boolean mazevertex, FileWriter out) throws IOException {
        int v1x = (v1 % w)* MAG + MARGIN;
        int v1y = (v1 / w)* MAG + MARGIN;
        int v2x = (v2 % w)* MAG + MARGIN;
        int v2y = (v2 / w)* MAG + MARGIN;
        String color = "blue";
        if (!mazevertex) {
            // mst verticies down 1/2 row and right 1/2 row from their
            // corresponding maze vertex
            v1x += .5*MAG;
            v1y += .5*MAG;
            v2x += .5*MAG;
            v2y += .5*MAG;
            color = "gray";
        }
        out.write("<line x1=\"" + v1x + "\" y1=\"" + v1y + "\" x2=\"" + v2x + "\" y2=\"" + v2y +
                "\" stroke=\"" + color + "\" stroke-width=\"2\" />\n");

    }

    /**
     * Given a file basename, read maze and st text files and produce
     * graphics svg files that can be viewed in a browser.
     *
     * @param args - <basename>; given a basename, reads files:
     *               <basename>-maze.txt and <basename>-st.txt
     *               and then produces output files that can be viewed in a web browser:
     *               <basename>-maze.svg   (the maze)
     *               <basename>-st.svg    (the spanning tree)
     *               <basename>-detail.svg (the maze overlaid on top of the spanning tree)
     *
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        System.out.println(args[0]);

        File mazeFile = null;
        File stFile = null;
        Scanner mazeScanner = null;
        Scanner stScanner = null;
        try {
            mazeFile = new File(args[0] + "-maze.txt");
            mazeScanner = new Scanner(mazeFile);
        } catch (FileNotFoundException e) {
            System.out.println("Could not find: '" + args[0] + "-maze.txt'...");
            System.exit(1);
        }
        try {
            stFile = new File(args[0] + "-st.txt");
            stScanner = new Scanner(stFile);
        } catch (FileNotFoundException e) {
            System.out.println("Could not find: '" + args[0] + "-st.txt'...");
            System.exit(1);
        }

        int height, width;
        int r, c;
        int cell;

        int suffix = args[0].lastIndexOf(".");
        String mapbase = args[0];
        if (suffix > 0 )
            mapbase = mapbase.substring(0,suffix);


        try {
            // read height and width
            width = mazeScanner.nextInt();
            height = mazeScanner.nextInt();
            r = c = 0;

            FileWriter mazesvg = new FileWriter(args[0] + "-maze.svg");
            FileWriter stsvg = new FileWriter(args[0] + "-st.svg");
            FileWriter detailsvg = new FileWriter(args[0] + "-detail.svg");

            writeHeader(width, height, mazesvg);
            writeHeader(width, height, detailsvg);

            // now read (height * width) numbers:
            // representing rows 0...(height-1) (labeled from top to bottom)
            // each row with (width) cells
            int v1, v2;
            while(mazeScanner.hasNextInt()) {
                v1 = mazeScanner.nextInt();
                v2 = mazeScanner.nextInt();
                drawWall(v1, v2, width, true, mazesvg);
                drawWall(v1, v2, width, true, detailsvg);
            }
            writeFooter(mazesvg);
            mazesvg.close();

            // now read the st...
            int stWidth = stScanner.nextInt();
            int stHeight = stScanner.nextInt();

            if (stWidth != width-1 || stHeight != height -1) {
                System.err.println("ST height and width should be 1 less than Maze height and width!");
                System.exit(1);
            }
            writeHeader(width-1, height-1, stsvg);

            // a series of lines, each line following one of the formats
            // below:
            // Format 1: (put a shape on the specified st vertex)
            // <shape type> <st vertex> <color>

            while (stScanner.hasNextInt()) {
                int firstarg = stScanner.nextInt();
                int secondarg = stScanner.nextInt();
                String color;
                if (firstarg < 0) {
                    // draw a shape.
                    color = stScanner.next();
                    drawLittleShape(firstarg, secondarg, color, width-1, detailsvg);
                    drawLittleShape(firstarg, secondarg, color, width-1, stsvg);
                } else {
                    // draw a line
                    drawWall(firstarg, secondarg, width-1, false, detailsvg);
                    drawWall(firstarg, secondarg, width-1, false, stsvg);
                }
            }

            writeFooter(stsvg);
            writeFooter(detailsvg);
            stsvg.close();
            detailsvg.close();

        }
        catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }
    }
}

