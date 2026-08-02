package io.github.tobyrue.btc.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class DummyConduitBlockEntity extends ConduitBlockEntity {

    public DummyConduitBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public boolean isEyeOpen() {
        return true;
    }

    @Override
    public float getRotation(float tickDelta) {
        return (this.ticks + tickDelta) * -0.0375F;
    }
}