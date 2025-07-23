package io.github.tobyrue.btc.regestries;

import com.chocohead.mm.api.ClassTinkerers;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;


public class EarlyRiser implements Runnable {
    @Override
    public void run() {
        MappingResolver remapper = FabricLoader.getInstance().getMappingResolver();
        String clickEventAction = remapper.mapClassName("intermediary", "net.minecraft.class_2558$class_2559");
        ClassTinkerers.enumBuilder(clickEventAction, String.class, boolean.class).addEnum("CODEX_PAGE", "codex_page", true).build();
        ClassTinkerers.enumBuilder(clickEventAction, String.class, boolean.class).addEnum("CODEX_VALUE", "codex_value", true).build();
    }
}
