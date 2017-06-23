package component.api;

public interface Converter<T> {
    boolean canConvert(Class<?> source);
    T convert(Object source);
}
