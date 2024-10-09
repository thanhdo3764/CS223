public class HuffmanEncoder {
    Node trie;
    String originalString;
    String encodedTrie = "";

    String[] bitStrings = new String[256];
    /**
     * You can use this node implementation for building your Huffman tree.
     * Feel free to change it, but be sure to update at least compareTo
     * so it will work correctly with the MinHeap
     */
    private class Node implements Comparable<Node>{
        public Node left, right;
        public Character c;
        public int count;

        public Node(Character c, int count) {
            this.c = c;
            this.count = count;
            left = right = null;
        }

        @Override
        public int compareTo(Node o) {
            return count - o.count;
        }

        @Override
        public String toString() {
            if(c == null) { return "null-" + count; }
            return c.toString() + "-" + count;
        }
    }

    public HuffmanEncoder() {
    }

    public Node buildTrie(String s) {
        MinHeapPriorityQueue<Node> charPQ = new MinHeapPriorityQueue<>();
        // Converts char to index and increments the value
        int[] counts = new int[256];
        for(int i = 0; i < s.length(); i++) {
            counts[s.charAt(i)]++;
        }
        // Goes through counts[] and inserts chars to charPQ
        int charsRead = 0;
        for(int j = 0; j < counts.length && charsRead < s.length(); j++) {
            if (counts[j] != 0) {
                charPQ.insert(new Node((char)j, counts[j]));
                charsRead += counts[j];
            }
        }
        // Creates a Huffman trie with low count chars combining first
        while (charPQ.size() > 1) {
            Node x = charPQ.delNext();
            Node y = charPQ.delNext();
            Node p = new Node(null,x.count+y.count);
            p.left = x;
            p.right = y;
            charPQ.insert(p);
        }
        return (charPQ.delNext());
    }
    /**
     * Huffman Part 1: Implement this
     *
     * Generates:
     *      1) Huffman tree
     *      2) Encoded version of same
     *      3) Character bit strings
     *      4) Encoded version of string
     *
     * @param s -- String to be Huffman encoded
     */
    public void encode(String s) {
        originalString = s;
        trie = buildTrie(s);
        getEncodedTree();
        getBitStrings();
        getEncodedText();
    }

    /**
     * Creates an encoded trie using recursion
     *
     * @param n -- the current node
     * @return a string of the encoded tree
     */
    public void preOrderWalkTrie(Node n) {
        if (n == null) return;
        if (n.c == null) encodedTrie += "0";
        else encodedTrie += "1"+n.c;
        preOrderWalkTrie(n.left);
        preOrderWalkTrie(n.right);
    }
    /**
     * Huffman Part 2: Implement this
     *
     * Your string encoding should use the actual letter rather than an
     * ASCII code. So if you are outputting a capital letter A, write "A",
     * not 65.
     *
     * @return String encoding of Huffman tree, as per slides
     */
    public String getEncodedTree() {
        if (encodedTrie.equals("")) preOrderWalkTrie(trie);
        return encodedTrie;
    }

    /**
     * Recursively add 0 and 1 to a string with a pre-order walk.
     * The bit string is added to an array with the correct char index.
     * After the pre-order walk, return the complete array of bit strings.
     *
     * @param n -- the current node
     * @param bits -- the bit string of the current path
     * @param bitStrings -- an array with the bit strings of chars
     * @return an array of bit strings that correspond with their chars
     */
    public String[] preOrderWalkBitStrings(Node n, String bits) {
        if (n.c != null) {
            bitStrings[n.c] = bits;
        }
        // Traverse all the way left first, then right
        if (n.left != null) {
            bits += "0";
            preOrderWalkBitStrings(n.left, bits);
            // Delete the recently added 0 for correct right sibling bit reading
            bits = bits.substring(0,bits.length()-1);
        }
        if (n.right != null) {
            bits += "1";
            preOrderWalkBitStrings(n.right, bits);
        }
        return bitStrings;
    }
    /**
     * Huffman Part 3: Implement this
     *
     * Returns an array of encoded bit strings.
     * Array should have 256 entries (one for each character value 0-255).
     * Entries that correspond to a character in the input should contain
     * the bitstring for that character.
     * Entries whose characters are not in the input should be null.
     *
     * Hint: the trick used for tracking counts in the slides would help here
     *
     * @return array of encoded bit strings
     */
    public String[] getBitStrings() {
        return preOrderWalkBitStrings(trie, "");
    }

    /**
     * Huffman Part 4: Implement this
     *
     * Returns a string corresponding to the Huffman encoding of the
     * string provided to encode(). String should contain only '0's
     * and '1's (i.e. be binary). Other characters will be considered
     * incorrect.
     *
     * @return 'bit' encoding of overall string
     */
    public String getEncodedText() {
        String s = "";
        // Go through the string and append the bit encoding for every char
        String[] bitStrings = getBitStrings();
        for (int i = 0; i < originalString.length(); i++) {
            s += bitStrings[originalString.charAt(i)];
        }
        return s;
    }


    public static void main(String args[]) {
        HuffmanEncoder he = new HuffmanEncoder();

        he.encode("ABRACADABRA!");
        System.out.println(he.getEncodedTree());
        System.out.println(he.getEncodedText());
        System.out.println(he.getEncodedText().length() + " <-- should be 28");
    }

}

class MinHeapPriorityQueue<I extends Comparable<I>> implements PriorityQueue<I> {
    // Java doesn't like creating arrays of generic types
    // I'm providing code that will manage this array for you
    private I[] heap;
    private int N;      // number of elements in the heap

    // Java would normally warn us about the cast below.
    // this directive keeps that from happening
    @SuppressWarnings("unchecked")
    public MinHeapPriorityQueue() {
        N = 0;
        heap = (I[]) new Comparable[1];
    }

    /**
     * Priority Queue Part 1: Implement this
     *
     * Insert an item into the priority queue, performing
     * sift/swim operations as appropriate. You *must* maintain
     * a heap array. No fair just searching for the smallest value later
     *
     * Make a call to resize() when you need to resize the array
     *
     * @param item -- Item to insert into the queue
     */
    @Override public void insert(I item) {
        if (N >= heap.length) resize();
        int hole = N;
        // Sift up by switching the places of a hole and its parent
        for (;hole > 0 && item.compareTo(heap[(hole-1)/2]) < 0; hole = (hole - 1) / 2) {
            heap[hole] = heap[(hole-1)/2];
        }
        // The item's position has been found, so place item in the hole
        heap[hole] = item;
        N++;

    }

    /**
     * Priority Queue Part 2: Implement this
     *
     * Extract the next item from queue, perform appropriate sift/swim
     * operations, and return the item. You *must* maintain a heap array.
     * No fair just searching for the smallest value.
     *
     * @return Item with highest priority (i.e. one with lowest value)
     */
    @Override public I delNext() {
        if (N == 0) return null;
        // Instantiate the min and the last node
        I min = heap[0];
        I tmp = heap[--N];
        int hole = 0;
        // Loops through to find tmp's place
        while (2*hole + 1 <= N) {
            // Find the biggest child of the node in question
            int child = 2*hole + 1;
            if (child != N && heap[child+1].compareTo(heap[child]) < 0) {
                child++;
            }
            // Keep looping if child < tmp, and make child the new node in question
            if (heap[child].compareTo(tmp) < 0) {
                heap[hole] = heap[child];
                hole = child;
            } else break;
        }
        heap[hole] = tmp;
        return min;
    }

    /**
     * I assume you use N to track number of items in the heap.
     * If that's not the case, update this method.
     *
     * @return number of items in the heap
     */
    @Override public int size() {
        return N;
    }

    /**
     * Doubles the size of the heap array. Takes care of allocating
     * memory and moving everything, so you don't have to.
     * Call this from insert when you need more array space.
     */
    private void resize() {
        @SuppressWarnings("unchecked")
        I[] newHeap = (I[]) new Comparable[heap.length * 2];
        for(int i = 0; i < N; i++) {
            newHeap[i] = heap[i];
        }
        heap = newHeap;
    }

    /**
     * Returns a stringified version of the array. You shouldn't
     * need to mess with this if you use the heap array. If you do
     * something else, make sure this method still works properly
     * based on whatever your heap storage is, and the format matches exactly.
     *
     * @return a comma-separated list of array values, wrapped in brackets
     */
    @Override public String toString() {
        if(N == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(int i = 0; i < N-1; i++) {
            sb.append(heap[i] + ",");
        }
        sb.append(heap[N-1] + "]");
        return sb.toString();
    }

    public static void main(String args[]) {
        MinHeapPriorityQueue<Integer> pq = new MinHeapPriorityQueue<>();

        pq.insert(5);
        pq.insert(11);
        pq.insert(8);
        pq.insert(4);
        pq.insert(3);
        pq.insert(15);
        System.out.println(pq + " <-- should be [3,4,8,11,5,15]");

        for(int i = 0; i < 6; i++) {
            System.out.println(pq.delNext());
        }
    }
}
