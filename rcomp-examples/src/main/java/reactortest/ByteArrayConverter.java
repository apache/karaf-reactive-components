package reactortest;

import java.nio.charset.Charset;

public class ByteArrayConverter {
    public static String asString(byte[] ba) {
        return new String(ba, Charset.forName("UTF-8"));
    }
    
    public static Integer asInteger(byte[] ba) {
        return new Integer(asString(ba));
    }
    
    public static Double asDouble(byte[] ba) {
        return new Double(asString(ba));
    }
    
    public static byte[] fromString(String in) {
        return in.getBytes(Charset.forName("UTF-8"));
    }
    
    public static byte[] fromInteger(Integer in) {
        return fromString(in.toString());
    }
    
    
}
