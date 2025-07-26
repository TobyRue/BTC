package io.github.tobyrue.btc.util;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class EnumHelper {
    public static String sanitize(String name) {
        return name.toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "");
    }

    private static final Map<Class<?>, Map<String, ?>> ENUM_CACHE = new HashMap<>();

    @Nullable
    @SuppressWarnings("unchecked")
    public static <E extends Enum<E>> E byName(Class<E> clazz, @Nullable String name) {
        if (name == null) {
            return null;
        }

        return (E) ENUM_CACHE.computeIfAbsent(clazz, c -> Arrays.stream(clazz.getEnumConstants()).collect(Collectors.toMap(f -> sanitize(f.name()), f -> f))).get(sanitize(name));
    }

    public static <E extends Enum<E>> void clearNameCache(Class<E> clazz) {
        ENUM_CACHE.remove(clazz);
    }

    public static void clearNameCache() {
        ENUM_CACHE.clear();
    }

    enum PositionMode {
        ABSOLUTE,
        STATIC
    }
}
