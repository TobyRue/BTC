package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.BTC;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public final class RuneTextLoader {
    private static List<String> RUNES;

    public static List<String> getRunes() {
        if (RUNES == null) {
            RUNES = new ArrayList<>();
            try (InputStream stream = RuneTextLoader.class.getResourceAsStream("/assets/btc/texts/runes.txt")) {
                if (stream == null) {
                    throw new java.io.FileNotFoundException("Could not find runes.txt in assets!");
                }

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
                    reader.lines()
                            .map(String::trim)
                            .filter(s -> !s.isEmpty() && !s.startsWith("#"))
                            .forEach(RUNES::add);
                }
            } catch (Exception e) {
                RUNES.add("?");
            }
        }
        return RUNES;
    }

    public static int getRandomIndex() {
        List<String> runes = getRunes();
        return runes.isEmpty() ? 0 : (int)(Math.random() * runes.size());
    }

    public static String get(int index) {
        List<String> runes = getRunes();
        if (runes.isEmpty()) return "?";
        if (index < 0 || index >= runes.size()) {
            return runes.get(0);
        }
        return runes.get(index);
    }
}