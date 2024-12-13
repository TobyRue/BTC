package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.BTC;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.Identifier;

public class ModModelLayers {
    public static final EntityModelLayer ELDRITCH_LUMINARIES =
            new EntityModelLayer(Identifier.of(BTC.MOD_ID, "eldritch_luminaries"), "main");
}
