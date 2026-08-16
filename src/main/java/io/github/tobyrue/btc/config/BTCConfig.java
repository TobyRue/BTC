package io.github.tobyrue.btc.config;

import net.fabricmc.loader.api.FabricLoader;
import java.io.*;
import java.util.Properties;

public class BTCConfig {
    private static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("btc_config.properties").toFile();
    public static boolean placableGunpowder = true;
    public static boolean placableNautilusShell = true;

    public static void load() {
        Properties props = new Properties();
        if (CONFIG_FILE.exists()) {
            try (InputStream is = new FileInputStream(CONFIG_FILE)) {
                props.load(is);
                placableGunpowder = Boolean.parseBoolean(props.getProperty("placableGunpowder", "true"));
                placableNautilusShell = Boolean.parseBoolean(props.getProperty("placableNautilusShell", "true"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            save();
        }
    }

    public static void save() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CONFIG_FILE))) {
            writer.println("# BTC Mod Configuration File");
            writer.println();

            writer.println("# Allows for the placing of gunpowder and nautilus shell");
            writer.println("# No problems should occur, but in the case of another mod adding placeable gunpowder or nautilus shells you can disable this in the case that the overlaping idea causes a problem");
            writer.println("placableGunpowder = " + placableGunpowder);
            writer.println("placableNautilusShell = " + placableNautilusShell);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}