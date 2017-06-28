package reactortest;

public class DoubleConverter {
    public static byte[] asByteAr(Double d) {
        return ByteArrayConverter.fromString(d.toString());
    }
}
