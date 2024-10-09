import org.junit.Test;
import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Vector;

public class RotationTests {

    private static class NodeAccessor {

        public static Object buildNode(SplaySymbolTable tree, Integer key, Integer val) {
            Class<?>[] types = new Class<?>[]{Comparable.class, Object.class};
            Object[] args = new Object[]{key, val};
            try {
                Object o = ReflectionHelper.instantiateNestedSubclass(tree, "Node", types, args);
                return o;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
            return null;
        }

        public static Object buildNode(SplaySymbolTable tree, Integer key, Integer val, Object parent) {
            Class<?> nodeClass = null;
            try {
                nodeClass = Class.forName("SplaySymbolTable$Node");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
            Class<?>[] types = new Class<?>[]{Comparable.class, Object.class, nodeClass};
            Object[] args = new Object[]{key, val, parent};
            try {
                Object o = ReflectionHelper.instantiateNestedSubclass(tree, "Node", types, args);
                return o;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
            return null;
        }

        public static Object getLeft(Object o) {
            try {
                Object l = ReflectionHelper.getInstanceVariable(o, "left");
                return l;
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }

        public static void setLeft(Object o, Object l) {
            try {
                ReflectionHelper.setInstanceVariable(o, "left", l);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        public static Object getRight(Object o) {
            try {
                Object r = ReflectionHelper.getInstanceVariable(o, "right");
                return r;
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }

        public static void setRight(Object o, Object r) {
            try {
                ReflectionHelper.setInstanceVariable(o, "right", r);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        public static Object getParent(Object o) {
            try {
                Object p = ReflectionHelper.getInstanceVariable(o, "parent");
                return p;
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }

        public static void setParent(Object o, Object p) {
            try {
                ReflectionHelper.setInstanceVariable(o, "parent", p);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        public static void setChildren(Object o, Object l, Object r) {
           setLeft(o, l);
           setRight(o, r);
        }
    }

    @Test
    /* Right rotate updates children correctly */
    public void testRightRotate() {
        SplaySymbolTable<Integer, Integer> tree = new SplaySymbolTable<>();

        // start by building the tree by hand

        // parentage is easy-ish to set up when we make the nodes
        Object t = NodeAccessor.buildNode(tree, 12, 12);
        Object p = NodeAccessor.buildNode(tree, 10, 10, t);
        Object x = NodeAccessor.buildNode(tree, 8, 8, p);
        Object a = NodeAccessor.buildNode(tree, 5, 5, x);
        Object b = NodeAccessor.buildNode(tree, 9, 9, x);
        Object c = NodeAccessor.buildNode(tree, 11, 11, p);

        // now set children
        NodeAccessor.setLeft(t, p);
        NodeAccessor.setChildren(p, x, c);
        NodeAccessor.setChildren(x, a, b);

        // set up for rotation call
        Class<?> nodeClass = null;
        try {
            nodeClass = Class.forName("SplaySymbolTable$Node");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Class<?>[] types = {nodeClass};
        Object [] args = {p};

        // attempt the rotate (which should work)
        try {
            ReflectionHelper.invokeInstanceMethod(tree, "rotateRight", types, args);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        // test that children are now as we expect
        assertEquals(a, NodeAccessor.getLeft(x)); // a should (still) be left of x
        assertEquals(p, NodeAccessor.getRight(x)); // p should now be right of x
        assertEquals(b, NodeAccessor.getLeft(p)); // b should now be left of p
        assertEquals(c, NodeAccessor.getRight(p)); // c should (still) be right of x
    }

    @Test
    /* Right rotate updates children when b is absent */
    public void testRightRotateNoMid() {
        SplaySymbolTable<Integer, Integer> tree = new SplaySymbolTable<>();

        // start by building the tree by hand

        // parentage is easy-ish to set up when we make the nodes
        Object t = NodeAccessor.buildNode(tree, 4, 4);
        Object p = NodeAccessor.buildNode(tree, 10, 10, t);
        Object x = NodeAccessor.buildNode(tree, 8, 8, p);
        Object a = NodeAccessor.buildNode(tree, 5, 5, x);
        Object c = NodeAccessor.buildNode(tree, 11, 11, p);

        // now set children
        NodeAccessor.setRight(t, p);
        NodeAccessor.setChildren(p, x, c);
        NodeAccessor.setLeft(x,a);

        // set up for rotation call
        Class<?> nodeClass = null;
        try {
            nodeClass = Class.forName("SplaySymbolTable$Node");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Class<?>[] types = {nodeClass};
        Object [] args = {p};

        // attempt the rotate (which should work)
        try {
            ReflectionHelper.invokeInstanceMethod(tree, "rotateRight", types, args);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        // test that children are now as we expect
        assertEquals(a, NodeAccessor.getLeft(x)); // a should (still) be left of x
        assertEquals(p, NodeAccessor.getRight(x)); // p should now be right of x
        assertNull(NodeAccessor.getLeft(p)); // p shouldn't have a left child
        assertEquals(c, NodeAccessor.getRight(p)); // c should (still) be right of x
    }

    @Test
    /* Right rotate updates parents when all (relevant) subtrees are present */
    public void testRightRotateParentUpdate() {
        SplaySymbolTable<Integer, Integer> tree = new SplaySymbolTable<>();

        // start by building the tree by hand

        // parentage is easy-ish to set up when we make the nodes
        Object t = NodeAccessor.buildNode(tree, 12, 12);
        Object p = NodeAccessor.buildNode(tree, 10, 10, t);
        Object x = NodeAccessor.buildNode(tree, 8, 8, p);
        Object a = NodeAccessor.buildNode(tree, 5, 5, x);
        Object b = NodeAccessor.buildNode(tree, 9, 9, x);
        Object c = NodeAccessor.buildNode(tree, 11, 11, p);

        // now set children
        NodeAccessor.setLeft(t, p);
        NodeAccessor.setChildren(p, x, c);
        NodeAccessor.setChildren(x, a, b);

        // set up for rotation call
        Class<?> nodeClass = null;
        try {
            nodeClass = Class.forName("SplaySymbolTable$Node");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Class<?>[] types = {nodeClass};
        Object [] args = {p};

        // attempt the rotate (which should work)
        try {
            ReflectionHelper.invokeInstanceMethod(tree, "rotateRight", types, args);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        // test that parentage is now as we'd expect
        assertEquals(t, NodeAccessor.getParent(x)); // t should be parent of x
        assertEquals(x, NodeAccessor.getParent(p)); // x should be parent of p
        assertEquals(p, NodeAccessor.getParent(b)); // p should be parent of b

        // test that rotate relinked x
        assertEquals(x, NodeAccessor.getLeft(t)); // x should be left child of t
    }

    @Test
    /* Right rotate updates parents when interior subtree is missing */
    public void testRightRotateParentUpdateNoMid() {
        SplaySymbolTable<Integer, Integer> tree = new SplaySymbolTable<>();

        // start by building the tree by hand

        // parentage is easy-ish to set up when we make the nodes
        Object t = NodeAccessor.buildNode(tree, 4, 4);
        Object p = NodeAccessor.buildNode(tree, 10, 10, t);
        Object x = NodeAccessor.buildNode(tree, 8, 8, p);
        Object a = NodeAccessor.buildNode(tree, 5, 5, x);
        Object c = NodeAccessor.buildNode(tree, 11, 11, p);

        // now set children
        NodeAccessor.setRight(t, p);
        NodeAccessor.setChildren(p, x, c);
        NodeAccessor.setLeft(x,a);

        // set up for rotation call
        Class<?> nodeClass = null;
        try {
            nodeClass = Class.forName("SplaySymbolTable$Node");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Class<?>[] types = {nodeClass};
        Object [] args = {p};

        // attempt the rotate (which should work)
        try {
            ReflectionHelper.invokeInstanceMethod(tree, "rotateRight", types, args);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        // test that parentage is now as we'd expect
        assertEquals(t, NodeAccessor.getParent(x)); // t should be parent of x
        assertEquals(x, NodeAccessor.getParent(p)); // x should be parent of p

        // test that rotate relinked x
        assertEquals(x, NodeAccessor.getRight(t)); // x should be right child of t
    }

    @Test
    /* Right rotate correctly updates root */
    public void testRightRotateParentUpdateRoot() {
        SplaySymbolTable<Integer, Integer> tree = new SplaySymbolTable<>();

        // start by building the tree by hand

        // parentage is easy-ish to set up when we make the nodes
        Object p = NodeAccessor.buildNode(tree, 10, 10);
        Object x = NodeAccessor.buildNode(tree, 8, 8, p);
        Object a = NodeAccessor.buildNode(tree, 5, 5, x);
        Object b = NodeAccessor.buildNode(tree, 9, 9, x);
        Object c = NodeAccessor.buildNode(tree, 11, 11, p);

        // now set children
        NodeAccessor.setChildren(p, x, c);
        NodeAccessor.setChildren(x, a, b);

        // set root in the tree
        try {
            ReflectionHelper.setInstanceVariable(tree, "root", p);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        // set up for rotation call
        Class<?> nodeClass = null;
        try {
            nodeClass = Class.forName("SplaySymbolTable$Node");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Class<?>[] types = {nodeClass};
        Object [] args = {p};

        // attempt the rotate (which should work)
        try {
            ReflectionHelper.invokeInstanceMethod(tree, "rotateRight", types, args);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        // test that parentage is now as we'd expect
        assertNull(NodeAccessor.getParent(x)); // x should have no parent
        assertEquals(x, NodeAccessor.getParent(p)); // x should be parent of p
        assertEquals(p, NodeAccessor.getParent(b)); // p should be parent of b

        // root should now point to x
        Object root = null;
        try {
            root = ReflectionHelper.getInstanceVariable(tree, "root");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        assertEquals(x, root); // x should be root of tree

    }

    @Test
    /* Left rotate updates children correctly */
    public void testLeftRotate() {
        SplaySymbolTable<Integer, Integer> tree = new SplaySymbolTable<>();

        // start by building the tree by hand

        // parentage is easy-ish to set up when we make the nodes
        Object t = NodeAccessor.buildNode(tree, 10, 10);
        Object p = NodeAccessor.buildNode(tree, 4, 4, t);
        Object x = NodeAccessor.buildNode(tree, 8, 8, p);
        Object a = NodeAccessor.buildNode(tree, 3, 3, p);
        Object b = NodeAccessor.buildNode(tree, 5, 5, x);
        Object c = NodeAccessor.buildNode(tree, 9, 9, x);

        // now set children
        NodeAccessor.setLeft(t, p);
        NodeAccessor.setChildren(p, a, x);
        NodeAccessor.setChildren(x, b, c);

        // set up for rotation call
        Class<?> nodeClass = null;
        try {
            nodeClass = Class.forName("SplaySymbolTable$Node");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Class<?>[] types = {nodeClass};
        Object [] args = {p};

        // attempt the rotate (which should work)
        try {
            ReflectionHelper.invokeInstanceMethod(tree, "rotateLeft", types, args);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        // test that children are now as we expect
        assertEquals(p, NodeAccessor.getLeft(x)); // p should now be left of x
        assertEquals(c, NodeAccessor.getRight(x)); // c should (still) be right of x
        assertEquals(a, NodeAccessor.getLeft(p)); // a should (still) be left of x
        assertEquals(b, NodeAccessor.getRight(p)); // b should now be right of p
    }

    @Test
    /* Left rotate updates children when b is absent */
    public void testLeftRotateNoMid() {
        SplaySymbolTable<Integer, Integer> tree = new SplaySymbolTable<>();

        // start by building the tree by hand

        // parentage is easy-ish to set up when we make the nodes
        Object t = NodeAccessor.buildNode(tree, 2, 2);
        Object p = NodeAccessor.buildNode(tree, 4, 4, t);
        Object x = NodeAccessor.buildNode(tree, 8, 8, p);
        Object a = NodeAccessor.buildNode(tree, 3, 3, p);
        Object c = NodeAccessor.buildNode(tree, 9, 9, x);

        // now set children
        NodeAccessor.setRight(t, p);
        NodeAccessor.setChildren(p, a, x);
        NodeAccessor.setRight(x, c);

        // set up for rotation call
        Class<?> nodeClass = null;
        try {
            nodeClass = Class.forName("SplaySymbolTable$Node");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Class<?>[] types = {nodeClass};
        Object [] args = {p};

        // attempt the rotate (which should work)
        try {
            ReflectionHelper.invokeInstanceMethod(tree, "rotateLeft", types, args);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        // test that children are now as we expect
        assertEquals(p, NodeAccessor.getLeft(x)); // p should now be left of x
        assertEquals(c, NodeAccessor.getRight(x)); // c should (still) be right of x
        assertEquals(a, NodeAccessor.getLeft(p)); // a should (still) be left of x
        assertNull(NodeAccessor.getRight(p)); // p shouldn't have a right child
    }

    @Test
    /* Left rotate updates parents when all (relevant) subtrees are present */
    public void testLeftRotateParentUpdate() {
        SplaySymbolTable<Integer, Integer> tree = new SplaySymbolTable<>();

        // start by building the tree by hand

        // parentage is easy-ish to set up when we make the nodes
        Object t = NodeAccessor.buildNode(tree, 10, 10);
        Object p = NodeAccessor.buildNode(tree, 4, 4, t);
        Object x = NodeAccessor.buildNode(tree, 8, 8, p);
        Object a = NodeAccessor.buildNode(tree, 3, 3, p);
        Object b = NodeAccessor.buildNode(tree, 5, 5, x);
        Object c = NodeAccessor.buildNode(tree, 9, 9, x);

        // now set children
        NodeAccessor.setLeft(t, p);
        NodeAccessor.setChildren(p, a, x);
        NodeAccessor.setChildren(x, b, c);

        // set up for rotation call
        Class<?> nodeClass = null;
        try {
            nodeClass = Class.forName("SplaySymbolTable$Node");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Class<?>[] types = {nodeClass};
        Object [] args = {p};

        // attempt the rotate (which should work)
        try {
            ReflectionHelper.invokeInstanceMethod(tree, "rotateLeft", types, args);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        // test that parentage is now as we'd expect
        assertEquals(t, NodeAccessor.getParent(x)); // t should be parent of x
        assertEquals(x, NodeAccessor.getParent(p)); // x should be parent of p
        assertEquals(p, NodeAccessor.getParent(b)); // p should be parent of b

        // test that rotate relinked x
        assertEquals(x, NodeAccessor.getLeft(t)); // x should be left child of t
    }

    @Test
    /* Left rotate updates parents when interior subtree is missing */
    public void testLeftRotateParentUpdateNoMid() {
        SplaySymbolTable<Integer, Integer> tree = new SplaySymbolTable<>();

        // start by building the tree by hand

        // parentage is easy-ish to set up when we make the nodes
        Object t = NodeAccessor.buildNode(tree, 2, 2);
        Object p = NodeAccessor.buildNode(tree, 4, 4, t);
        Object x = NodeAccessor.buildNode(tree, 8, 8, p);
        Object a = NodeAccessor.buildNode(tree, 3, 3, p);
        Object c = NodeAccessor.buildNode(tree, 9, 9, x);

        // now set children
        NodeAccessor.setRight(t, p);
        NodeAccessor.setChildren(p, a, x);
        NodeAccessor.setRight(x, c);

        // set up for rotation call
        Class<?> nodeClass = null;
        try {
            nodeClass = Class.forName("SplaySymbolTable$Node");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Class<?>[] types = {nodeClass};
        Object [] args = {p};

        // attempt the rotate (which should work)
        try {
            ReflectionHelper.invokeInstanceMethod(tree, "rotateLeft", types, args);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        // test that parentage is now as we'd expect
        assertEquals(t, NodeAccessor.getParent(x)); // t should be parent of x
        assertEquals(x, NodeAccessor.getParent(p)); // x should be parent of p

        // test that rotate relinked x
        assertEquals(x, NodeAccessor.getRight(t)); // x should be right child of t
    }

    @Test
    /* Left rotate correctly updates root */
    public void testLeftRotateParentUpdateRoot() {
        SplaySymbolTable<Integer, Integer> tree = new SplaySymbolTable<>();

        // start by building the tree by hand

        // parentage is easy-ish to set up when we make the nodes
        Object p = NodeAccessor.buildNode(tree, 4, 4);
        Object x = NodeAccessor.buildNode(tree, 8, 8, p);
        Object a = NodeAccessor.buildNode(tree, 3, 3, p);
        Object b = NodeAccessor.buildNode(tree, 5, 5, x);
        Object c = NodeAccessor.buildNode(tree, 9, 9, x);

        // now set children
        NodeAccessor.setChildren(p, a, x);
        NodeAccessor.setChildren(x, b, c);

        // set root in the tree
        try {
            ReflectionHelper.setInstanceVariable(tree, "root", p);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        // set up for rotation call
        Class<?> nodeClass = null;
        try {
            nodeClass = Class.forName("SplaySymbolTable$Node");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Class<?>[] types = {nodeClass};
        Object [] args = {p};

        // attempt the rotate (which should work)
        try {
            ReflectionHelper.invokeInstanceMethod(tree, "rotateLeft", types, args);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        // test that parentage is now as we'd expect
        assertNull(NodeAccessor.getParent(x)); // x should have no parent
        assertEquals(x, NodeAccessor.getParent(p)); // x should be parent of p
        assertEquals(p, NodeAccessor.getParent(b)); // p should be parent of b

        // root should now point to x
        Object root = null;
        try {
            root = ReflectionHelper.getInstanceVariable(tree, "root");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        assertEquals(x, root); // x should be root of tree
    }

    @Test
    public void testParentSplay() {
        /*
         * Splay method without stack actually works
         *
         * Structurally, this works like the insert / search test.
         * The difference is that we'll grab a reference to the node for 7
         * when we insert it (since it'll be root) and perform the last "search"
         * by manually invoking splay(node) on 7.
         *
         * Assuming splay(node) works, we should see the same tree as we'd get
         * from a search.
         */
        SplaySymbolTable<Integer, Integer> tree = new SplaySymbolTable<>();
        String[] soln = new String[]{"7:black", "3:black", "2:black", "1:black",
                null, null, null,
                "6:black", "4:black",
                null, null, null,
                "9:black", "8:black", null, null, null};
        Vector<String> solnVector = new Vector<String>(Arrays.asList(soln));
        Integer[] input = new Integer[]{1,2,3,4,9,8,7};

        for(Integer i: input) {
            tree.insert(i, i);
        }
        Object n7 = null;
        try {
            n7 = ReflectionHelper.getInstanceVariable(tree, "root");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        tree.insert(6,6);

        tree.search(3);
        tree.search(9);

        Class<?> nodeClass = null;
        try {
            nodeClass = Class.forName("SplaySymbolTable$Node");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Class<?>[] types = {nodeClass};
        Object [] args = {n7};
        try {
            ReflectionHelper.invokeInstanceMethod(tree, "splay", types, args);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        Vector<String> st = tree.serialize();
        assertEquals(solnVector, st);
    }
}
