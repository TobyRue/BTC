package io.github.tobyrue.btc.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.AbstractWindChargeEntity;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class DummyWindCharge extends AbstractWindChargeEntity {
    public DummyWindCharge() {
        super(EntityType.WIND_CHARGE, MinecraftClient.getInstance().world);
        this.age = 3;
    }

    @Override
    protected void createExplosion(Vec3d pos) {

    }

}
