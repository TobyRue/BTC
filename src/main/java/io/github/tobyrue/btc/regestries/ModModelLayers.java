package io.github.tobyrue.btc.regestries;

import io.github.tobyrue.btc.BTC;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class ModModelLayers {
    public static final EntityModelLayer ELDRITCH_LUMINARY =
            new EntityModelLayer(Identifier.of(BTC.MOD_ID, "eldritch_luminaries"), "main");
    public static final EntityModelLayer WATER_BURST =
            new EntityModelLayer(Identifier.of(BTC.MOD_ID, "water_burst"), "main");
    public static final EntityModelLayer COPPER_GOLEM =
            new EntityModelLayer(Identifier.of(BTC.MOD_ID, "copper_golem"), "main");
    public static final EntityModelLayer TUFF_GOLEM =
            new EntityModelLayer(Identifier.of(BTC.MOD_ID, "tuff_golem"), "main");
    public static final EntityModelLayer EARTH_SPIKE =
            new EntityModelLayer(Identifier.of(BTC.MOD_ID, "earth_spike"), "main");
    public static final EntityModelLayer CREEPER_PILLAR =
            new EntityModelLayer(Identifier.of(BTC.MOD_ID, "creeper_pillar"), "main");
    public static final EntityModelLayer WIND_TORNADO =
            new EntityModelLayer(Identifier.of(BTC.MOD_ID, "wind_tornado"), "main");
    public static final EntityModelLayer SHKB =
            new EntityModelLayer(Identifier.of(BTC.MOD_ID, "super_happy_kill_ball"), "main");
}
