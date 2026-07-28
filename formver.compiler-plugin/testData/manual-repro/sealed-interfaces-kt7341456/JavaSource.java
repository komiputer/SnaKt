public class JavaSource {
    public static Foo get(boolean flag) {
        // Returns a platform type; Java has no knowledge of Kotlin's sealed
        // hierarchy closedness, so this can return anything assignable to Foo,
        // including something outside {Bar, Baz} if the class hierarchy allows it.
        return flag ? Bar.INSTANCE : null;
    }
}
