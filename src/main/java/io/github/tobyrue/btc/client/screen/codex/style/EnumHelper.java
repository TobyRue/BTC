package io.github.tobyrue.btc.client.screen.codex.style;

import io.github.tobyrue.btc.client.screen.codex.Codex;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EnumHelper {
    public static String sanitize(String name) {
        return name.toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "");
    }
    //TODO
    private static final Map<Class<Enum<?>>, Map<String, ?>> ENUM_CACHE = new HashMap<>();

    @Nullable
    public static <E extends Enum<E>> E byName(Class<E> clazz, @Nullable String name) {
        if (name == null) {
            return null;
        }
        return ENUM_CACHE.computeIfAbsent(clazz, ).get(sanitize(name));
    }
}
