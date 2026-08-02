package io.github.tobyrue.btc.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class DummyEndCrystal extends EndCrystalEntity {
    public DummyEndCrystal() {
        super(EntityType.END_CRYSTAL, MinecraftClient.getInstance().world);
    }

    @Override
    public boolean shouldShowBottom() {
        return false;
    }
}
