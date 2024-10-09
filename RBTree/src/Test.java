public class Test {
    String hi;
    public static void main(String args[]) {
        String hi = "hi";
        String no = hi;
        hi = "foo";
        System.out.println(hi+" "+no);
    }
}