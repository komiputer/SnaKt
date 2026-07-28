package jlib;

/** Java source of sealed-typed values. Both methods have Kotlin platform types. */
public class JProvider {
    /** Kotlin sees `Foo!`; may be null at runtime. */
    public static klib.Foo nullable() {
        return null;
    }

    /** Erasure lets a non-Foo object escape through a `Foo`-typed slot. */
    @SuppressWarnings("unchecked")
    public static <T> T coerce(Object o) {
        return (T) o;
    }
}
