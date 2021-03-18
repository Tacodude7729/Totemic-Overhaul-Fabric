package net.fabricmc.totemicoverhaul.utils;

import java.util.HashSet;
import java.util.Set;


public class SetUtils {
    @SafeVarargs
    public static <T> Set<T> of(T... values) {
        Set<T> set = new HashSet<T>();
        for (T value : values) set.add(value);
        return set;
    }
}
