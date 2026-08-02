package io.github.tobyrue.btc.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class DummyConduit {
    private static DummyConduitBlockEntity INSTANCE;

    public static ConduitBlockEntity getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DummyConduitBlockEntity(BlockPos.ORIGIN, Blocks.CONDUIT.getDefaultState());
        }
        return INSTANCE;
    }
}