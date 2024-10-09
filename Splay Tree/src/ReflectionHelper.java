import java.lang.reflect.*;

/*
 * This class is based (loosely) on the PrivateAccessor class in Vladimir R.
 * Bossicard's JUnit-Addons project (http://junit-addons.sourceforge.net/)
 *
 * It differs primarily in two ways:
 *      1) It does not support accessing members of superclasses
 *      2) It provides the ability to instantiate nested (private) subclasses
 */


public class ReflectionHelper {

    /**
     * Attempts to retrieve the requested field of the provided object.
     * Can retrieve fields that would not be directly visible from the
     * calling code. A similar method could take a class and return a
     * class variable, instead.
     * <p>
     * Throws NoSuchField if the field isn't directly defined in the object's
     * class (e.g. a typo, or an inherited field). Could amend the function
     * to try and traverse the inheritance chain looking for the field.
     * <p>
     * Shouldn't throw IllegalAccessException, but that would correspond
     * to a failure to make the field accessible.
     *
     * @param object Object whose variable you wish to read
     * @param name   A string giving the name of the variable (e.g. "key")
     * @return The requested field *as an Object*
     * @throws NoSuchFieldException   thrown if the field doesn't exist
     * @throws IllegalAccessException shouldn't happen
     */
    public static Object getInstanceVariable(Object object, String name)
            throws NoSuchFieldException, IllegalAccessException {

        assert (object != null);

        Class cls = object.getClass();
        Field field = cls.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(object);
    }

    /**
     * Attempt to set the designated field in the provided object.
     * Can set fields what would not be directly visible from the calling code.
     * <p>
     * Throws NoSuchField if the field is not directly defined by the object's
     * class. Could be amended to traverse the inheritance chain.
     * <p>
     * Probably shouldn't throw IllegalAccess, but may.
     * <p>
     * NOTE: It is unclear what will happen if you try to set a value using
     * an incompatible type.
     *
     * @param object Object whose variable you wish to set
     * @param name   A string giving the name of the varible (e.g. "key")
     * @param value  The desired new value
     * @throws NoSuchFieldException   field isn't directly defined in object's class
     * @throws IllegalAccessException shouldn't happen (?)
     */
    public static void setInstanceVariable(Object object, String name, Object value)
            throws NoSuchFieldException, IllegalAccessException {

        assert (object != null);

        Class cls = object.getClass();
        Field field = cls.getDeclaredField(name);
        field.setAccessible(true);
        field.set(object, value);
    }

    /**
     * Call a specified method on the provided object, using the provided
     * set of parameters. Can interact with methods that would not be
     * directly visible from the calling code.
     * <p>
     * Parameter types must be specified as an array of Java Class objects,
     * e.g. {}Node.class Integer.class, Double.class}. If you're dealing w/
     * a generic function, use types appropriate to the generic types,
     * e.g. {Comparable.class, Object.class} for a generic class defined with
     * <K extends Comparable<K>, V>
     * <p>
     * NoSuchMethod is thrown if the method is not directly defined in
     * the object's class. It's possible to amend the method to traverse
     * the inheritance chain looking for methods. It may also be thrown if
     * your parameter type list cannot be matched to a method.
     * <p>
     * You'll get an InvocationTargetException if the called method throws
     * an exception. Use getTargetException() or getCause() to see what happened.
     * <p>
     * IllegalAccess shouldn't happen.
     *
     * @param object         object on which to invoke the method
     * @param name           method name as a string, e.g. "rotateRight"
     * @param parameterTypes Array of Class objects specifying param types
     * @param args           Arguments you want to pass to the method
     * @return Value returned by method, *as an Object*
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static Object invokeInstanceMethod(Object object, String name,
                                              Class[] parameterTypes, Object[] args)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        assert (object != null);

        Class cls = object.getClass();
        Method method = cls.getDeclaredMethod(name, parameterTypes);
        method.setAccessible(true);
        return method.invoke(object, args);
    }

    /**
     * Attempt to instantiate a nested (non-static) subclass of some
     * other class. Can return objects which are not normally accessible
     * to the calling code.
     * <p>
     * You must provide an instantiated object of the parent class with
     * which the new object will be associated. This is a Java requirement
     * for non-static subclasses, since they are allowed to interact with
     * their 'containing' object.
     * <p>
     * This method does some magic with class names. Assuming your parent
     * class is BST and your inner class is Node, pass an object of type
     * BST as the first argument, and use "Node" as the second argument.
     * The method assembles the 'full' name "BST$Node" for  you.
     * <p>
     * Specify the parameter types associated with the constructor you
     * wish to call. Java requires that a reference to the 'containing'
     * object be a (hidden) first parameter. DO NOT include this in your list.
     * Your parameter list should match the one defined in the subclass.
     * <p>
     * NOTE: I have not found a good way to downcast a returned object
     * back to its actual type. At present, I recommend building a wrapper
     * (or pseudo-wrapper) class that uses the other methods here to
     * interact with the object.
     *
     * @param object         Object of the parent class's type, which will 'contain' new object
     * @param name           Name of subclass without parent, e.g. "Node" not "BST$Node"
     * @param parameterTypes Array of Class objects specifying constructor param types
     * @param args           Array of objects to hand to constructor
     * @return Instantiated subclass, *as an object*
     * @throws ClassNotFoundException    No class of the specified name exists
     * @throws NoSuchMethodException     Can't find the designed constructor
     * @throws IllegalAccessException    Shouldn't happen
     * @throws InvocationTargetException Contains exception thrown by constructor
     * @throws InstantiationException    Object couldn't be instantiated for some reason
     */
    public static Object instantiateNestedSubclass(Object object, String name,
                                                   Class[] parameterTypes, Object[] args)
            throws ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException,
            InstantiationException {
        assert (object != null);
        assert (parameterTypes.length == args.length);

        Class<?> parentClass = object.getClass();
        String fullName = parentClass.getName() + "$" + name;

        Class<?>[] constTypes = new Class[parameterTypes.length + 1];
        Object[] constArgs = new Object[args.length + 1];
        constTypes[0] = parentClass;
        constArgs[0] = object;

        for (int i = 0; i < parameterTypes.length; i++) {
            constTypes[i + 1] = parameterTypes[i];
            constArgs[i + 1] = args[i];
        }

        Class<?> inner = Class.forName(fullName);
        Constructor<?> innerConst = inner.getDeclaredConstructor(constTypes);
        innerConst.setAccessible(true);

        Object o = innerConst.newInstance(constArgs);

        return o;
    }


}

