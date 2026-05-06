package io.github.tobyrue.btc.config;

import net.fabricmc.loader.api.FabricLoader;
import java.io.*;
import java.util.Properties;

public class BTCConfig {
    private static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("btc_config.properties").toFile();
    public static boolean placableGunpowder = true;

    public static void load() {
        Properties props = new Properties();
        if (CONFIG_FILE.exists()) {
            try (InputStream is = new FileInputStream(CONFIG_FILE)) {
                props.load(is);
                placableGunpowder = Boolean.parseBoolean(props.getProperty("placableGunpowder", "true"));
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

            writer.println("# Allows for the placing of gunpowder");
            writer.println("# No problems should occur, but in the case of another mod adding placeable gunpowder you can disable this in the case that the overlaping idea causes a problem");
            writer.println("placableGunpowder = " + placableGunpowder);

            // writer.println("# Description of next setting");
            // writer.println("nextSetting = " + nextSettingValue);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}