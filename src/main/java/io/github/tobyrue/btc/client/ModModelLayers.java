package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.BTC;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.Identifier;

public class ModModelLayers {
    public static final EntityModelLayer ELDRITCH_LUMINARY =
            new EntityModelLayer(Identifier.of(BTC.MOD_ID, "eldritch_luminaries"), "main");
    public static final EntityModelLayer WATER_BURST =
            new EntityModelLayer(Identifier.of(BTC.MOD_ID, "water_burst"), "main");
    public static final EntityModelLayer COPPER_GOLEM =
            new EntityModelLayer(Identifier.of(BTC.MOD_ID, "copper_golem"), "main");
}
